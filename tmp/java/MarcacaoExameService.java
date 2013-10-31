package br.com.infowaypi.ecarebc.service.exames;

import static br.com.infowaypi.msr.utils.Assert.isNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecare.enums.TipoLiberacaoForaLimiteEnum;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.validators.AbstractProcedimentoValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoCarenciaValidator;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Agente;
import br.com.infowaypi.ecarebc.service.MarcacaoService;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para marcação de exames do plano de saúde
 * @author Infoway Team
 * @changes Idelvane, Danilo Portela
 */
public class MarcacaoExameService extends MarcacaoService<GuiaExame<ProcedimentoInterface>> {

	public static Collection<AbstractProcedimentoValidator> procedimentosDiretorValidators = new ArrayList<AbstractProcedimentoValidator>();
	static{
		procedimentosDiretorValidators.add(new ProcedimentoCarenciaValidator());
	}
	
	public boolean internacao; 
	
	public MarcacaoExameService(){
		super();
		this.internacao = false;
	}
	
	public void setInternacao(boolean internacao) {
		this.internacao = internacao;
	}

	@Override
	public GuiaExame<ProcedimentoInterface> getGuiaInstanceFor(UsuarioInterface usuario) {
		return new GuiaExame<ProcedimentoInterface>(usuario);
	}

	@SuppressWarnings("unchecked")
	public GuiaExame<Procedimento> criarGuiaPrestador(AbstractSegurado segurado, Prestador prestador,UsuarioInterface usuario,
			Profissional solicitante,Collection<Procedimento> procedimentos)
			throws Exception {
		
		Assert.isNotNull(segurado, "Segurado deve ser informado!");
		Assert.isNotNull(prestador, "Prestador deve ser informado!");
		Assert.isNotEmpty(procedimentos, "A guia deve conter pelo menos um procedimento.");
		if (!internacao)
			Assert.isNotNull(solicitante, "O solicitante deve ser informado!");
		
		prestador.tocarObjetos();
		
		GuiaExame guiaGerada = criarGuia(segurado, prestador, usuario, solicitante, null,null,procedimentos,Utils.hoje(), 
				MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(), Agente.PRESTADOR);
		
		return guiaGerada;

	}

	public GuiaExame<Procedimento> criarGuiaMarcador(AbstractSegurado segurado, UsuarioInterface usuario, Profissional solicitante, Collection<Procedimento> procedimentos) throws Exception {
		Assert.isNotNull(segurado, "Segurado deve ser informado!");
		Assert.isNotNull(solicitante, "O solicitante deve ser informado!");

		GuiaExame guiaGerada = criarGuia(segurado, null, usuario, solicitante, null, null, procedimentos, Utils.hoje(), 
				MotivoEnum.AGENDADA_NO_ecare.getMessage(), Agente.MARCADOR);
		
		return guiaGerada;
	}

	@SuppressWarnings("unchecked")
	public GuiaExame<Procedimento> criarGuiaLancamento(AbstractSegurado segurado, Boolean ignorarValidacao, String dataDeAtendimento, Profissional solicitante, Collection<Procedimento> procedimentos, UsuarioInterface usuario) throws Exception {

		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		isNotNull(solicitante, MensagemErroEnum.SOLICITANTE_NAO_INFORMADO.getMessage());
		Assert.isNotEmpty(procedimentos, MensagemErroEnum.GUIA_SEM_PROCEDIMENTOS.getMessage());

		setIgnoreValidacao(ignorarValidacao);

		// verificar
		// if (!ignorarValidacao){
		// segurado.isCumpriuCarencia(GuiaInterface.CARENCIA_MINIMA, "Exames");
		// }

		String motivo = "";

		if (usuario.getRole().equals(Role.ROOT.getValor()))
			motivo = MotivoEnum.AUTORIZADO_PELO_TI.getMessage();
		if (usuario.getRole().equals(Role.CENTRAL_DE_SERVICOS.getValor())) 
			motivo = MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage();
		if(usuario.getRole().equals(Role.AUDITOR.getValor()))
			motivo = MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage();
		if(usuario.getRole().equals(Role.DIGITADOR.getValor()))
			motivo = MotivoEnum.AUTORIZADO_PELO_DIGITADOR.getMessage();
		if (usuario.getRole().equals(Role.DIRETORIA_MEDICA.getValor()))
			motivo = MotivoEnum.AUTORIZADO_PELA_DIRETORIA.getMessage();

		for (ProcedimentoInterface procedimento : procedimentos) 
			procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), motivo, new Date());

		GuiaExame guiaGerada =  criarGuia(segurado, null, usuario, solicitante, null, null, procedimentos, dataDeAtendimento, motivo, Agente.SUPERVISOR);
		return guiaGerada;
	}
	
	/**
	 * 
	 * criar uma guia para o role central.
	 * 
	 * @param segurado
	 * @param ignorarValidacao
	 * @param dataDeAtendimento
	 * @param solicitante
	 * @param procedimentos
	 * @param usuario
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public GuiaExame<Procedimento> criarGuiaExameCentral(AbstractSegurado segurado, Boolean ignorarValidacao, String dataDeAtendimento, Profissional solicitante, Collection<Procedimento> procedimentos, UsuarioInterface usuario) throws Exception {

		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		isNotNull(solicitante, MensagemErroEnum.SOLICITANTE_NAO_INFORMADO.getMessage());
		Assert.isNotEmpty(procedimentos, MensagemErroEnum.GUIA_SEM_PROCEDIMENTOS.getMessage());

		setIgnoreValidacao(ignorarValidacao);


		String motivo = MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage();

		GuiaExame guiaGerada =  criarGuia(segurado, null, usuario, solicitante, null, null, procedimentos, dataDeAtendimento, motivo, Agente.SUPERVISOR);
		
		boolean isLiberadoAuditor = guiaGerada.getLiberadaForaDoLimite() == TipoLiberacaoForaLimiteEnum.LIBERACAO_AUDITOR.codigo();
		if (guiaGerada.isPossuiPeriodicidadeEmProcedimento() || isLiberadoAuditor) {
			for (ProcedimentoInterface procedimento : procedimentos) {
				procedimento.getSituacao().setDescricao(SituacaoEnum.SOLICITADO.descricao());
				procedimento.getSituacao().setMotivo(MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage());
			}
			guiaGerada.getSituacao().setDescricao(SituacaoEnum.SOLICITADO.descricao());
			guiaGerada.getSituacao().setMotivo(MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage());
		}
		
		return guiaGerada;
	}

	public GuiaExame<Procedimento> criarGuiaExterna(AbstractSegurado segurado, Boolean ignorarValidacao, String dataDeAtendimento, Profissional solicitante, Collection<Procedimento> procedimentos, UsuarioInterface usuario) throws Exception {

		Assert.isNotNull(segurado, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		// isNotNull(solicitante, MensagemErroEnum.SOLICITANTE_NAO_INFORMADO.getMessage());
		Assert.isNotEmpty(procedimentos, MensagemErroEnum.GUIA_SEM_PROCEDIMENTOS.getMessage());

		setIgnoreValidacao(ignorarValidacao);

		String motivo = MotivoEnum.SOLICITACAO_DE_EXAMES_EXTERNOS_ESPECIAIS.getMessage();

		GuiaExame guiaGerada = criarGuia(segurado, null, usuario, solicitante, null, null, procedimentos, dataDeAtendimento, motivo, Agente.PRESTADOR);
		guiaGerada.getSituacao().setDescricao(SituacaoEnum.SOLICITADO.descricao());
		guiaGerada.setExameExterno(true);
		return guiaGerada;
	}

	protected void confirmarExame(GuiaExame guia) throws Exception {
		if (guia.getSituacao().getUsuario().isPrestador()){ 
			if (guia.isPermiteMatMed()){
				guia.getSituacao().setDescricao(SituacaoEnum.ABERTO.descricao());
				guia.getSituacao().setMotivo(MotivoEnum.GUIA_EXAME_AGUARDANDO_FECHAMENTO.getMessage());
			}
			else
				guia.getSituacao().setDescricao(SituacaoEnum.CONFIRMADO.descricao());
			guia.setDataTerminoAtendimento(new Date());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onAfterCriarGuia(GuiaExame<ProcedimentoInterface> guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		for (ProcedimentoInterface procedimento : procedimentos) {
			if (guia.getSituacao().getUsuario().isPrestador())
				procedimento.mudarSituacao(guia.getSituacao().getUsuario(), SituacaoEnum.CONFIRMADO.descricao(), MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(), new Date());
			if (guia.getSituacao().getUsuario().isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor()))
				procedimento.mudarSituacao(guia.getSituacao().getUsuario(), SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.SOLICITACAO_NAO_AUTORIZADA.getMessage(), new Date());
			guia.addProcedimento(procedimento);
			procedimento.calcularCampos();
		}
		confirmarExame(guia);
	}
	@SuppressWarnings("unchecked")
	public void onAfterCriarGuiaCentral(GuiaExame<ProcedimentoInterface> guia, Collection<? extends ProcedimentoInterface> procedimentos) throws Exception {
		boolean existeProcedimentoNivel1 = false;
		boolean existeProcedimentoOutrosNiveis = false;
		
		for (ProcedimentoInterface procedimento : procedimentos) {
			if(procedimento.getProcedimentoDaTabelaCBHPM().getNivel().equals(TabelaCBHPM.NIVEL_1)) {
				existeProcedimentoNivel1 = true;
			}else {
				existeProcedimentoOutrosNiveis = true;
			}
			procedimento.mudarSituacao(guia.getSituacao().getUsuario(), SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.SOLICITACAO_NAO_AUTORIZADA.getMessage(), new Date());
			guia.addProcedimento(procedimento);
			procedimento.calcularCampos();
		}
		
		if(existeProcedimentoNivel1 && existeProcedimentoOutrosNiveis) {
			throw new Exception(MensagemErroEnum.GUIA_PROCEDIMENTOS_NIVEIS_MISTURADOS.getMessage());
		}else if (existeProcedimentoNivel1 && !existeProcedimentoOutrosNiveis) {
			boolean algumProcedimentoPossuiPeriodicidade = false;
			for (ProcedimentoInterface procedimento : procedimentos) {
				if (procedimento.isPossuiPeriodicidade()){
					procedimento.getSituacao().setDescricao(SituacaoEnum.SOLICITADO.descricao());
					procedimento.getSituacao().setMotivo("Procedimento com periodicidade.");
					algumProcedimentoPossuiPeriodicidade = true;
				}
				else {
					procedimento.getSituacao().setDescricao(SituacaoEnum.AUTORIZADO.descricao());
					procedimento.getSituacao().setMotivo(MotivoEnum.AUTORIZADO_PELA_CENTRAL_DE_SERVICOS.getMessage());
				}
			}

			if (algumProcedimentoPossuiPeriodicidade) {
				guia.getSituacao().setDescricao(SituacaoEnum.SOLICITADO.descricao());
				guia.getSituacao().setMotivo(MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage());
			}
			else {
				guia.getSituacao().setDescricao(SituacaoEnum.AUTORIZADO.descricao());
				guia.getSituacao().setMotivo(MotivoEnum.AUTORIZADO_PELA_CENTRAL_DE_SERVICOS.getMessage());
			}
		}else if (!existeProcedimentoNivel1 && existeProcedimentoOutrosNiveis) {
			guia.getSituacao().setDescricao(SituacaoEnum.SOLICITADO.descricao());
			guia.getSituacao().setMotivo(MotivoEnum.SOLICITADO_PELA_CENTRAL_DE_SERVICOS.getMessage());
		}
	}

	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}

	protected boolean isUserPrestador(String role) {

		if (role.equals(UsuarioInterface.ROLE_PRESTADOR) || role.equals(UsuarioInterface.ROLE_PRESTADOR_COMPLETO) || role.equals(UsuarioInterface.ROLE_PRESTADOR_CONSULTA) || role.equals(UsuarioInterface.ROLE_PRESTADOR_EXAME) || role.equals(UsuarioInterface.ROLE_PRESTADOR_CONS_EXM) || role.equals(UsuarioInterface.ROLE_PRESTADOR_INT_URG) || role.equals(UsuarioInterface.ROLE_PRESTADOR_ODONTOLOGICO))
			return true;

		return false;
	}
	
	@Override
	protected void validateGuiaSuperUsuario(GuiaExame<ProcedimentoInterface> guia) throws Exception {
		super.validateGuiaSuperUsuario(guia);
		
		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
			for (AbstractProcedimentoValidator validator : this.procedimentosDiretorValidators) {
				validator.execute(procedimento,guia);
			}
		}
		
		
	}

}