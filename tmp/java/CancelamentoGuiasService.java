package br.com.infowaypi.ecarebc.service;
 
import static br.com.infowaypi.msr.utils.Assert.isNotEmpty;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;
import br.com.infowaypi.sensews.client.SenseManager;

/**
 * Service para o cancelamento de guias do plano de saúde
 * @author root
 */
@SuppressWarnings("unchecked")
public class CancelamentoGuiasService<S extends SeguradoInterface> extends Service{
	
	public CancelamentoGuiasService(){
		super();
	}
	
	public ResumoGuias<GuiaSimples> buscarGuiasCancelamento(S segurado,String dataInicial, String dataFinal, Prestador prestador, UsuarioInterface usuario) throws Exception{
		SearchAgent sa = getNotSearchSituacoes(SituacaoEnum.FATURADA, SituacaoEnum.CANCELADO, SituacaoEnum.REALIZADO);
		List<GuiaSimples> guias = super.buscarGuias(sa,"",segurado,dataInicial, dataFinal,prestador,false,false, GuiaSimples.class, null);
		isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("CANCELADAS"));
		for(GuiaSimples guia : guias) {
			if(guia.getSituacao().getDescricao().equals(SituacaoEnum.FECHADO.descricao())) {
				throw new RuntimeException("A guia de autorização " + guia.getAutorizacao()+ " está FECHADA e não pode ser CANCELADA.");
			}
		}
		return new ResumoGuias<GuiaSimples>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}
	
	public ResumoGuias<GuiaSimples> buscarGuiasCancelamento(Collection<S> segurados,String dataInicial, String dataFinal, Prestador prestador, UsuarioInterface usuario) throws Exception{
		SearchAgent sa = getNotSearchSituacoes(SituacaoEnum.FATURADA, SituacaoEnum.CANCELADO, SituacaoEnum.REALIZADO);
		List<GuiaSimples> guias = super.buscarGuias(sa,segurados, prestador,false, GuiaSimples.class);
		isNotEmpty(guias, MensagemErroEnum.GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO.getMessage("CANCELADAS"));
		
		for(GuiaSimples guia : guias) {
			if(guia.getSituacao().getDescricao().equals(SituacaoEnum.FECHADO.descricao())) {
				throw new RuntimeException("A guia de autorização " +guia.getAutorizacao()+ " está FECHADA e não pode ser CANCELADA.");
			}
		}
		
		return new ResumoGuias<GuiaSimples>(guias, ResumoGuias.SITUACAO_TODAS, false);
	}

	public GuiaSimples buscarGuiasCancelamento(String autorizacao, Prestador prestador, UsuarioInterface usuario) throws Exception{
		if (Utils.isStringVazia(autorizacao)) {
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		}
		
		GuiaSimples guia = null;
		List<GuiaSimples> guias;
		SearchAgent sa = getNotSearchSituacoes( SituacaoEnum.REALIZADO);
		
		if(usuario.isPrestador()) {
			guia = super.buscarGuias(autorizacao, prestador,false, GuiaSimples.class, SituacaoEnum.AGENDADA,SituacaoEnum.ABERTO,SituacaoEnum.CONFIRMADO, 
					SituacaoEnum.INTERNADO, SituacaoEnum.NAO_PRORROGADO, SituacaoEnum.PRORROGADO, SituacaoEnum.SOLICITADO_INTERNACAO, 
					SituacaoEnum.SOLICITADO_PRORROGACAO, SituacaoEnum.FECHADO, SituacaoEnum.AUTORIZADO, SituacaoEnum.RECEBIDO);
		} else {
			sa.addParameter(new Equals("autorizacao",autorizacao));
			guias = sa.list(GuiaSimples.class);
			Assert.isNotEmpty(guias, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage(autorizacao));
			guia = guias.get(0);
		}
		
		Assert.isNotNull(guia, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage(autorizacao));
		boolean isSuperUsuario = usuario.isPossuiRole(Role.ROOT.getValor(),Role.AUDITOR.getValor(),Role.DIRETORIA_MEDICA.getValor(), Role.DIGITADOR.getValor());

		if((guia.isSituacaoAtual(SituacaoEnum.RECEBIDO.descricao()) && !guia.isHonorarioMedico())
				||(guia.isSituacaoAtual(SituacaoEnum.FATURADA.descricao()))
				||(guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao()) && !isSuperUsuario && !guia.isHonorarioMedico())
				||(guia.isSituacaoAtual(SituacaoEnum.AUDITADO.descricao()) && !isSuperUsuario)		
				||(guia.isSituacaoAtual(SituacaoEnum.INAPTO.descricao()))		
				||(guia.isSituacaoAtual(SituacaoEnum.ORDENADO.descricao()))		
				||(guia.isSituacaoAtual(SituacaoEnum.PAGO.descricao()))		
				||(guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao()))
				||(usuario.isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor()) && guia.isInternacao() 
						&& !guia.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) 
						&& !guia.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao()))){
		
				lancarExcecaoDeGuiaNaoPodeSerCancelada(guia);
		}

		guia.tocarObjetos();
		
		return guia;
		}
		
	private void lancarExcecaoDeGuiaNaoPodeSerCancelada(GuiaSimples<ProcedimentoInterface> guia) throws ValidateException {
		String sitUpperCase = guia.getSituacao().getDescricao().toUpperCase();
		throw new ValidateException(MensagemErroEnum.GUIA_NAO_PODE_SER_CANCELADA.getMessage(guia.getAutorizacao(), sitUpperCase));
	}
		
		
	public void cancelarGuia(String motivo, UsuarioInterface usuario, GuiaSimples guia) throws Exception {
		if (guia.isHonorarioMedico()) {
			cancelarHonorarios(motivo, usuario, guia);
		}
		
		BigDecimal valorParcial = guia.getValorTotal();
		guia.setValorAnterior(valorParcial);
		
		isNotEmpty(motivo, MensagemErroEnum.MOTIVO_CANCELAMENTO_NAO_INFORMADO.getMessage());
		
		boolean isCentralDeServicos = usuario.isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor());
		boolean isAtendente = usuario.isPossuiRole(Role.ATENDENTE.getValor());
		boolean isRoot = usuario.isPossuiRole(Role.ROOT.getValor());
		boolean isAuditor = usuario.isPossuiRole(Role.AUDITOR.getValor());
		boolean isDiretor = usuario.isPossuiRole(Role.DIRETORIA_MEDICA.getValor());
		boolean isDigitador = usuario.isPossuiRole(Role.DIGITADOR.getValor());
		

		if(isCentralDeServicos || isAtendente || isRoot || isAuditor || isDiretor || isDigitador){
			motivo =  "Cancelada " + this.getDescricaoRole(usuario) + motivo;
		}
		else{
			motivo = MotivoEnum.CANCELADO_NO_PRESTADOR.getMessage() + motivo;
		}

		if(!guia.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())){
			guia.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), motivo, new Date());
		}
		
		super.salvarGuia(guia);
		/* if[SENSE_MANAGER] */
		if(guia.isConsultaEletiva()) {
			SenseManager.CANCELAMENTO_CONSULTA.analisar(guia);
		} else if(guia.isExameEletivo()){
			SenseManager.CANCELAMENTO_EXAME.analisar(guia);
		}
		/* end[SENSE_MANAGER] */
	}

	private void cancelarHonorarios(String motivo, UsuarioInterface usuario, GuiaSimples guia) throws Exception {
		GuiaHonorarioMedico guiaHonorario = (GuiaHonorarioMedico) guia;
		
		if (GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo().equals(guiaHonorario.getGrauDeParticipacao())) {
			List<Honorario> honorarios = guiaHonorario.getHonorarios();
			
			for (Honorario honorario : honorarios) {
				ProcedimentoInterface procedimento = honorario.getProcedimento();
				if (procedimento != null){
					voltarSituacaoProcedimento(procedimento);
				}
				if(honorario.isHonorarioExterno()){
					ItemPacoteHonorario item = ((HonorarioExterno)honorario).getItemPacoteHonorario();
					if (item != null){
						for (ProcedimentoInterface proc : item.getProcedimentosCompativeisComAGuiaOrigem()) {
							voltarSituacaoProcedimento(proc);
						}
					}
				}

				honorario = ImplDAO.findById(honorario.getIdHonorario(), honorario.getClass());
				honorario.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), motivo, new Date());
			}
			
			GuiaSimples<ProcedimentoInterface> guiaOrigem = guiaHonorario.getGuiaOrigem();
			guiaOrigem = ImplDAO.findById(guiaOrigem.getIdGuia(), guiaOrigem.getClass());
			guiaOrigem.recalcularValores();
		}
	}
	
	private void voltarSituacaoProcedimento(ProcedimentoInterface procedimento) throws Exception{
		if (procedimento.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())){
			SituacaoInterface situacaoAnterior = procedimento.getSituacaoAnterior(procedimento.getSituacao());
			procedimento.mudarSituacao(situacaoAnterior.getUsuario(), situacaoAnterior.getDescricao(),
					MotivoEnum.CANCELAMENTO_HONORARIO.getMessage(), new Date());
			ImplDAO.save(procedimento);
		}
	}
	
	public String getDescricaoRole(UsuarioInterface usuario){
		if(usuario.isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor()))
			return "na Central de Serviços: ";
		else if(usuario.isPossuiRole(Role.ATENDENTE.getValor()))
				return "pelo Atendente: ";
		else if(usuario.isPossuiRole(Role.ROOT.getValor()))
				return "pelo Ti: ";
		
		else return "pelo Auditor: ";
				
	}

	@SuppressWarnings("unchecked")
	public GuiaSimples selecionarGuia(GuiaSimples guia) throws Exception {
		return super.selecionarGuia(guia);
	}
	
	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}
}
