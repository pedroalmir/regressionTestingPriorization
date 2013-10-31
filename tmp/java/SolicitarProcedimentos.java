package br.com.infowaypi.ecare.services;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecare.atendimentos.GeradorGuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuiasComAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoPossuiPacoteSemProcedimentoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoPossuiProcedimentoSemPacoteValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.opme.ItemOpme;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoAtendimentoUrgenciaValidator;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.SolicitarProcedimentosService;
import br.com.infowaypi.ecarebc.utils.CommandCorrecaoCalculoValorProcedimento;
import br.com.infowaypi.ecarebc.utils.DateUtils;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus Quixabeira
 * 
 */
@SuppressWarnings("unchecked")
public class SolicitarProcedimentos extends SolicitarProcedimentosService {
	public final Integer TIPO_EXTERNO = 1;
	public final Integer TIPO_INTERNO = 2;
	
	public GuiaCompleta buscarGuia(String autorizacao, Prestador prestador) throws Exception {
		if (Utils.isStringVazia(autorizacao)) {
			throw new ValidateException(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		}
		
		GuiaCompleta guia = super.buscarGuias(autorizacao, prestador, false, GuiaCompleta.class, SituacaoEnum.ABERTO, SituacaoEnum.PRORROGADO, SituacaoEnum.SOLICITADO_PRORROGACAO);
		guia.tocarObjetos();
		return guia;
	}
	
	public ResumoGuiasComAcompanhamentoAnestesico adicionarProcedimentos(Integer tipoDeSolicitacao, Collection<Procedimento> exames, 
			boolean acompanhamentoAnestesico, Collection<ItemPacote> pacotes, GuiaCompleta<ProcedimentoInterface> guia, UsuarioInterface usuario) throws Exception {
		return adicionarProcedimentos(tipoDeSolicitacao, exames, acompanhamentoAnestesico, pacotes, guia, null, usuario);
	}
	
	public ResumoGuiasComAcompanhamentoAnestesico adicionarProcedimentos(Integer tipoDeSolicitacao, Collection<Procedimento> exames, 
			boolean acompanhamentoAnestesico, Collection<ItemPacote> pacotes, GuiaCompleta<ProcedimentoInterface> guia, String justificativa, UsuarioInterface usuario) throws Exception {
		
		if (!pacotes.isEmpty()) {
			Assert.isTrue(guia.isInternacao(), "Caro usu�rio, s� � poss�vel inserir pacotes em guias de interna��o.");
		}
		
		if (exames.isEmpty() && pacotes.isEmpty() && guia.getProcedimentosCirurgicosDaSolicitacao().isEmpty() && guia.getOpmesSolicitados().isEmpty()) {
			throw new RuntimeException("Caro usu�rio, insira exames, pacotes, procedimentos ou OPMEs.");
		}
		
		if (guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()) {
			Calendar diaDoAtendimento = Calendar.getInstance();
			diaDoAtendimento.setTime(guia.getSituacao(SituacaoEnum.ABERTO.descricao()).getDataSituacao());
			Calendar agora = Calendar.getInstance();
			
			Integer prazoSolictacao = PainelDeControle.getPainel().getPrazoSolicitacaoEmConsultaUrgencia();
			if (DateUtils.getDiferencaHorasUteis(diaDoAtendimento.getTime(), agora.getTime()) > prazoSolictacao) {
				throw new RuntimeException(MensagemErroEnum.SOLICITACAO_APOS_PRAZO_ATENDIMENTO
								.getMessage(prazoSolictacao.toString(), guia.getAutorizacao(),
									Utils.format(guia.getSituacao(SituacaoEnum.ABERTO.descricao()).getDataSituacao(), "dd/MM/yyyy  HH:mm")));
			}
		}
	
		GuiaCompleta<ProcedimentoInterface> guiaResultado = null;
		ResumoGuiasComAcompanhamentoAnestesico resumoGuiaComAcompanhamento = new ResumoGuiasComAcompanhamentoAnestesico();

		
		if (tipoDeSolicitacao.equals(TIPO_INTERNO)) {
			
			if (!Utils.isStringVazia(justificativa)){
				guia.addQuadroClinico(justificativa);
			}
			
			if (!guia.isInternacao() && !guia.getProcedimentosCirurgicosDaSolicitacao().isEmpty()) {
				throw new ValidateException("S� � poss�vel inserir Procedimentos Cir�rgicos em uma Guia de Interna��o.");
			}
			
			for (ProcedimentoCirurgico cirurgico : guia.getProcedimentosCirurgicosDaSolicitacao()) {
				cirurgico.getSituacao().setUsuario(usuario);
				cirurgico.getSituacao().setMotivo("Solicita��o de Exames/Procedimentos Especiais");
			}
			
			for (Procedimento exame : exames) {
				exame.mudarSituacao(usuario, SituacaoEnum.SOLICITADO.descricao(), "Solicita��o de Exames/Procedimentos Especiais", new Date());
				new ProcedimentoAtendimentoUrgenciaValidator().execute(exame, guia);
				exame.setGuia(guia);
				guia.addProcedimento(exame);
			}
			
			for (ItemOpme itemOpme : guia.getOpmesSolicitados()) {
				Assert.isNotNull(itemOpme.getObservacaoSolicitacao(), "O motivo da solicita��o deve ser informado para OPMEs.");
				itemOpme.getSituacao().setMotivo(itemOpme.getObservacaoSolicitacao());
				itemOpme.getSituacao().setUsuario(usuario);
			}
			
			AbstractSegurado segurado = guia.getSegurado();
			String motivo 				= "Acompanhamento anest�sico solicitado junto com solicita��o de procedimento.";
			String situacao 			= SituacaoEnum.SOLICITADO.descricao();
			
			resumoGuiaComAcompanhamento.setGuiaOrigem(guia);
			
			// Se ele tiver algum exame dentro do grupo pre-estabelecido, criar um resumo aqui e gerar a guia de honor�rio.
			if (acompanhamentoAnestesico) {
				GuiaAcompanhamentoAnestesico guiaAA = new GeradorGuiaAcompanhamentoAnestesico().
						gerarGuiaDeAcompanhamentoAnestesico(
						exames, guia, usuario, segurado, motivo, situacao);
				resumoGuiaComAcompanhamento.setGuiaAcompanhamentoAnestesico(guiaAA);
			}
			
			for (ItemPacote itemPacote : pacotes) {
				guia.addItemPacoteComValidacao(itemPacote);
			}
			
			new InternacaoPossuiPacoteSemProcedimentoValidator().execute(guia);
			new InternacaoPossuiProcedimentoSemPacoteValidator().execute(guia);
			
			guia.corrigeValorProcedimentoInclusoEmPacoteQueInclueHonorario(guia.getProcedimentosNaoCanceladosENegados());
			
			ProcedimentoUtils.aplicaDescontoDaViaDeAcesso(guia);
			
			CommandCorrecaoCalculoValorProcedimento cmd = new CommandCorrecaoCalculoValorProcedimento(guia);
			cmd.execute();
			
			guiaResultado = guia;
			
		} else {
			Assert.isTrue(guia.getOpmesSolicitados().isEmpty(), "N�o � permitido a solicita��o de OPMEs em car�ter de realiza��o EXTERNO. Por favor, altere o car�ter da solicita��o para INTERNO.");
			Assert.isTrue(guia.getProcedimentosCirurgicosDaSolicitacao().isEmpty(), "N�o � permitido a solicita��o de PROCEDIMENTOS CIR�RGICOS em car�ter de realiza��o EXTERNO. Por favor, altere o car�ter da solicita��o para INTERNO.");
			Assert.isTrue(pacotes.isEmpty(), "N�o � permitido a solicita��o de PACOTES em car�ter de realiza��o EXTERNO. Por favor, altere o car�ter da solicita��o para INTERNO.");
			
			SolicitarExamesEspeciais service = new SolicitarExamesEspeciais();
			
			GuiaCompleta guiaFilha = service.criarGuiaExterna(guia.getSegurado(), true, null, guia.getProfissional(), exames, usuario);
			guia.addGuiaFilha(guiaFilha);
			
			if (!Utils.isStringVazia(justificativa)){
				guiaFilha.addQuadroClinico(justificativa);
			}
			
			AbstractSegurado segurado 	= guiaFilha.getSegurado();
			String motivo 				= guiaFilha.getSituacao().getMotivo();
			String situacao 			= guiaFilha.getSituacao().getDescricao();
			
			resumoGuiaComAcompanhamento.setGuiaOrigem((GuiaCompleta) guiaFilha);
			
			//criar aqui uma decis�o para fazer com que a guia de acompanhamento seja criada.
			if (acompanhamentoAnestesico) {
				GuiaAcompanhamentoAnestesico guiaAA = new GeradorGuiaAcompanhamentoAnestesico().
				gerarGuiaDeAcompanhamentoAnestesico(
						exames, guiaFilha, usuario, segurado, motivo, situacao);
				resumoGuiaComAcompanhamento.setGuiaAcompanhamentoAnestesico(guiaAA);
			}
			
			guiaFilha.validate();
			guiaResultado = (GuiaCompleta<ProcedimentoInterface>) guiaFilha;
		}
		
		return resumoGuiaComAcompanhamento;
	}


	public void salvarGuia(ResumoGuiasComAcompanhamentoAnestesico resumoGuiaComAcompanhamento) throws Exception {
		resumoGuiaComAcompanhamento.getGuiaOrigem().recalcularValores();
		super.salvarGuia(resumoGuiaComAcompanhamento.getGuiaOrigem());

		if (resumoGuiaComAcompanhamento.getGuiaAcompanhamentoAnestesico() != null) {
			resumoGuiaComAcompanhamento.getGuiaAcompanhamentoAnestesico().recalcularValores();
			super.salvarGuia(resumoGuiaComAcompanhamento.getGuiaAcompanhamentoAnestesico(), false, false);
		}

	}
}
