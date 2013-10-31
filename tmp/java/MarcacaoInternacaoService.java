package br.com.infowaypi.ecarebc.service.internacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.validators.AbstractGuiaValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.CarenciaInternacaoValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.exceptions.BeneficiarioCanceladoException;
import br.com.infowaypi.ecarebc.procedimentos.CID;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.ecarebc.service.MarcacaoService;
import br.com.infowaypi.ecarebc.service.exames.MarcacaoExameService;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;
/**
 * Classe básica para criação de services de marcação de internações eletivas
 * @author Infoway Team
 * @changes Idelvane, galera
 */
public abstract class MarcacaoInternacaoService<G extends GuiaCompleta> extends MarcacaoService<G> {
	
	public static final int GUIA_CONSULTA_URGENCIA = 1;
	public static final int GUIA_ATENDIMENTO_SUBSEQUENTE = 2;
	public static final int GUIA_INTERNACAO_URGENCIA = 3;
	public static final int GUIA_INTERNACAO_ELETIVA = 4;
	
	public static final int PRAZO_ATENDIMENTO_URGENCIA = 6;
	public static final int PRAZO_URGENCIA = 12;
	public static final int PRAZO_1DIA = 24;
	public static final int PRAZO_2DIAS = 48;
	public static final int PRAZO_3DIAS = 72;

	private String justificativa;
	private Collection<CID> cids;
	private int tipoTratamento;
	private int tipoAcomodacao;

	public static Collection<AbstractGuiaValidator> marcacaoInternacaoValidators = new ArrayList<AbstractGuiaValidator>();
	
	static{
		marcacaoInternacaoValidators.add(new CarenciaInternacaoValidator());
	}
	
	public MarcacaoInternacaoService(){
		super();
	}
	
	@Override
	public abstract G getGuiaInstanceFor(UsuarioInterface usuario);

	protected <E extends ProcedimentoInterface> G criarGuia(AbstractSegurado segurado,Integer tipoTratamento,
		Integer tipoAcomodacao, Prestador prestador, UsuarioInterface usuario,
		Profissional solicitante,Profissional profissional, Especialidade especialidade, 
		Collection<E> procedimentos, Collection<CID> cids, String justificativa,
		String dataDeAtendimento,String motivo, Agente agente) throws Exception {

		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		
		/* if_not[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
		Assert.isNotNull(prestador, MensagemErroEnum.PRESTADOR_NAO_INFORMADO.getMessage());
		/* end[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
				
		this.justificativa = justificativa;
		this.cids = cids;
		
		if (tipoAcomodacao != null)
			this.tipoAcomodacao = tipoAcomodacao;
		
		if (tipoTratamento != null)
			this.tipoTratamento = tipoTratamento;
		
		G guia = super.criarGuia(segurado, prestador, usuario, solicitante, profissional, especialidade, procedimentos, dataDeAtendimento, motivo, agente);
			
		return guia;
	}
	
	public GuiaSimples addProcedimentos(Prestador prestador, Collection<Procedimento> procedimentos, Collection<ProcedimentoCirurgico> procedimentosCirurgicos,G guia,UsuarioInterface usuario, Agente agente) throws Exception{
		Assert.isNotNull(guia, "Guia inválida!");
		
		BigDecimal valorAnterior = guia.getValorTotal();
		
		if(Agente.PRESTADOR.equals(agente))
			if (isEmptyProcedimentos(procedimentos) && isEmptyProcedimentos(procedimentosCirurgicos) && (guia.getTipoTratamento() == GuiaInternacaoUrgencia.TRATAMENTO_CIRURGICO) && guia.getItensPacote().isEmpty())
				throw new RuntimeException("Devem ser adicionados procedimentos ambulatoriais ou cirúrgicos");

		guia.tocarObjetos();
		
		if (prestador != null && !prestador.equals(guia.getPrestador())){
			return criarGuiaExameUrgencia(prestador, guia, procedimentos, usuario);
		} else {
			boolean isExamesInformados = procedimentos != null;
			boolean isProcedimentosCirurgicosInformados = procedimentosCirurgicos != null;
			
			if (isExamesInformados || isProcedimentosCirurgicosInformados){
				Iterator<ProcedimentoInterface> iterator = guia.getProcedimentos().iterator();
				while(iterator.hasNext()){
					ProcedimentoInterface procedimento = iterator.next();
					if(procedimento.getIdProcedimento() == null)
						iterator.remove();
				}
				
				if(isProcedimentosCirurgicosInformados) {
					guia = this.addProcedimentos(guia, procedimentosCirurgicos);
				}
				
				if(isExamesInformados) {
					guia = this.addProcedimentos(guia, procedimentos);
				}
			}
		}
		
		guia.setValorAnterior(valorAnterior);
		return guia;
	}
	
	
	public GuiaSimples addProcedimentos(Prestador prestador, Collection<Procedimento> procedimentos, G guia, 
			UsuarioInterface usuario, Agente agente) throws Exception{
		
		Assert.isNotNull(guia, "Guia inválida!");
		
		
		if(usuario.isPossuiRole(Role.ROOT.getValor()) || usuario.isPossuiRole(Role.AUDITOR.getValor()) || usuario.isPossuiRole(Role.DIGITADOR.getValor()) 
				||usuario.isPossuiRole(Role.DIRETORIA_MEDICA.getValor()) ){
			
			String tipoUsuario;
			
			if(usuario.isPossuiRole(Role.DIRETORIA_MEDICA.getValor()))
				tipoUsuario = "Diretor";
			else if (usuario.isPossuiRole(Role.ROOT.getValor(),Role.AUDITOR.getValor() ))
				tipoUsuario = (usuario.isPossuiRole(Role.ROOT.getValor())? "Ti" : "Auditor"); 
			else
				tipoUsuario = "Digitador";
			
			for (ProcedimentoInterface procedimento : procedimentos) {
				procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Autorizado pelo :"+ tipoUsuario, new Date());
			}
		}
		
		if(Agente.PRESTADOR.equals(agente))
			if (isEmptyProcedimentos(procedimentos))
				throw new RuntimeException("Devem ser adicionados procedimentos ambulatoriais.");

		guia.tocarObjetos();
		
		
		if (prestador != null && !prestador.equals(guia.getPrestador())){
			return criarGuiaExameUrgencia(prestador, guia, procedimentos, usuario);
		}
		
		return guia;
	}
		
	private GuiaExame criarGuiaExameUrgencia(Prestador prestador, G guia, Collection<Procedimento> procedimentos, UsuarioInterface usuario) throws Exception {
		
		MarcacaoExameService service = new MarcacaoExameService();
		service.setIgnoreValidacao(true);
		service.setInternacao(true);
		
		List<Procedimento> procedimentosExames = new ArrayList<Procedimento>();
		
		if (procedimentos != null) procedimentosExames.addAll(procedimentos);
		
		GuiaExame exame = service.criarGuiaPrestador(guia.getSegurado(), prestador, usuario, guia.getProfissional(), procedimentosExames);
		exame.setGuiaOrigem(guia);
		exame.setExameExterno(true);
		
		if (guia.isInternacao())
			exame.setSolicitante(guia.getSolicitante());
		else
			exame.setSolicitante(guia.getProfissional());
		
		if(usuario.isPossuiRole(Role.DIGITADOR.getValor()))
			exame.getSituacao().setMotivo(MotivoEnum.AUTORIZADO_PELO_ROLE_INFORMADO.getMessage("Digitador"));
		if(usuario.isPossuiRole(Role.ROOT.getValor()))
			exame.getSituacao().setMotivo(MotivoEnum.AUTORIZADO_PELO_ROLE_INFORMADO.getMessage("Ti"));
		if(usuario.isPossuiRole(Role.AUDITOR.getValor()))
			exame.getSituacao().setMotivo(MotivoEnum.AUTORIZADO_PELO_ROLE_INFORMADO.getMessage("Auditor"));
		if(usuario.isPossuiRole(Role.DIRETORIA_MEDICA.getValor()))
			exame.getSituacao().setMotivo(MotivoEnum.AUTORIZADO_PELO_ROLE_INFORMADO.getMessage("Diretoria Médica"));
		
		try{
			exame.validate();
		}catch (BeneficiarioCanceladoException e) {
			
			if(!usuario.getLogin().equals("hemope"))
				throw e;
			
			Date dataCancelamentoBeneficiario = guia.getSegurado().getSituacao().getDataSituacao();
			Calendar dataLimite = Calendar.getInstance();
			dataLimite.add(Calendar.DAY_OF_MONTH, -30);
			
			boolean canceladoHaMaisDe30Dias =  Utils.compareData(dataCancelamentoBeneficiario, dataLimite.getTime()) < 0;
			if(canceladoHaMaisDe30Dias)
				throw e;
		}
		guia.getGuiasFilhas().add(exame);
		
		return exame;
		
		
	}

	private G addProcedimentos(G guia,Collection<? extends Procedimento> procedimentos) throws Exception{
		BigDecimal valorTotal = new BigDecimal(0f);
		for (ProcedimentoInterface procedimento : procedimentos) {
			if(guia.isInternacao())
				procedimento.setValorCoParticipacao(BigDecimal.ZERO);
			guia.addProcedimento(procedimento);
			valorTotal = valorTotal.add(procedimento.getValorTotal());
		}
		return guia;
	}

	private boolean isEmptyProcedimentos(Collection<? extends Procedimento> procedimentos){
		if (procedimentos == null || procedimentos.isEmpty())
			return true;
		return false;
	}
	
	public void finalizar(GuiaCompleta guia) {
		guia.tocarObjetos();
		super.finalizar(guia);
	}
	
	@Override
	public void onAfterCriarGuia(G guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		if (!Utils.isStringVazia(justificativa))
			guia.addQuadroClinico(justificativa);
		
		if (guia.isInternacao()){
			guia.setTipoAcomodacao(this.tipoAcomodacao);
			guia.setTipoTratamento(this.tipoTratamento);
		}
		
		if (cids != null){
			guia.getCids().addAll(cids);
		}
		
		if (!guia.isInternacaoEletiva())
			mudarSituacao(guia);
		
	}

	private void mudarSituacao(GuiaCompleta guia){
		boolean isUserPrestador = this.usuarioDoFluxo.isPossuiRole(UsuarioInterface.ROLE_PRESTADOR_COMPLETO);//guia.getSituacao().getUsuario().getRole().equals(UsuarioInterface.ROLE_PRESTADOR_COMPLETO);
		String motivoSituacao = "";
		if (guia.isAtendimentoUrgencia())
			motivoSituacao = MotivoEnum.PACIENTE_ATENDIMENTO_SUBSEQUENTE.getMessage();

		if (guia.isConsultaUrgencia())
			motivoSituacao = MotivoEnum.PACIENTE_CONSULTA_URGENCIA.getMessage();

//			if (isUserPrestador && guia.isInternacaoUrgencia()){
//				motivoSituacao = MotivoEnum.INTERNACAO_URGENCIA.getDescricao();
//				guia.mudarSituacao(this.usuarioDoFluxo, SituacaoEnum.SOLICITADO_INTERNACAO.descricao(), motivoSituacao, new Date());
//			}	
		
		if(isUserPrestador){
			if (guia.isSituacaoAtual(SituacaoEnum.AGENDADA.descricao()))
				guia.mudarSituacao(
						guia.getSituacao().getUsuario(), 
						SituacaoEnum.ABERTO.descricao(), motivoSituacao, new Date());
		} 
	}

	@Override
	protected void validateGuiaSuperUsuario(G guia) throws Exception {
		super.validateGuiaSuperUsuario(guia);
		
		for (AbstractGuiaValidator validator : this.marcacaoInternacaoValidators) {
			validator.execute(guia);
		}
		
	}
}
