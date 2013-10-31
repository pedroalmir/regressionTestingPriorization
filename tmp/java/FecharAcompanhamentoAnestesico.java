package br.com.infowaypi.ecare.services.exame;

import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.validators.fechamento.FechamentoDataLimiteValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class FecharAcompanhamentoAnestesico {

	public GuiaAcompanhamentoAnestesico buscarGuia(String autorizacao, Prestador prestador) throws ValidateException {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", autorizacao));
		sa.addParameter(new Equals("prestador", prestador));
		
		GuiaAcompanhamentoAnestesico guia = sa.uniqueResult(GuiaAcompanhamentoAnestesico.class);

		if (guia != null) {
			if (!guia.isEmUmaDasSituacoes(SituacaoEnum.AUTORIZADO.descricao(), SituacaoEnum.ABERTO.descricao())) {
				throw new RuntimeException("Esta guia não pode ser fechada pois se encontra na situação " + guia.getSituacao().getDescricao());
			}
			
			SituacaoInterface situacao;
			if (guia.isSituacaoAtual(SituacaoEnum.ABERTO.descricao())){
				situacao = guia.getSituacao(SituacaoEnum.ABERTO.descricao());
			} else {
				situacao = guia.getSituacao(SituacaoEnum.AUTORIZADO.descricao());
			}
			
			if (situacao != null){
				FechamentoDataLimiteValidator.execute(guia);
			}
			
			tocarObjectos(guia);
			return guia;
		} else {
			throw new RuntimeException(MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage(autorizacao));
		}
	}

	public GuiaAcompanhamentoAnestesico fecharAcompanhamentoAnestesico(String numeroDeRegistro, GuiaAcompanhamentoAnestesico guia, UsuarioInterface usuario) {
		
		if (guia.isFechado()) {
			Date dataFinal = new Date();
			guia.setDataTerminoAtendimento(dataFinal);
			guia.setNumeroDeRegistro(numeroDeRegistro);
			guia.mudarSituacao(usuario, SituacaoEnum.FECHADO.descricao(), MotivoEnum.FECHAMENTO_GUIA_ANESTESISTA.getMessage(), dataFinal);
			
			this.calcularVideoEHorarioEspecial(guia);
			guia.recalcularValores();
			guia.updateValorCoparticipacao();
			
			
		} else {
			throw new RuntimeException("Caso alguma informação esteja incorreta cancele a guia");
		}
		
		return guia;
	}
	
	/**
	 * Calcula o valor de vídeo e horário especial para os procedimentos anestésicos.
	 * @param procedimentos
	 */
	@SuppressWarnings("unchecked")
	private void calcularVideoEHorarioEspecial(GuiaAcompanhamentoAnestesico guia) {
		Set<ProcedimentoInterface> procedimentos = guia.getProcedimentosNaoGlosadosNemCanceladosNemNegados();
		for (ProcedimentoInterface procedimentoInterface : procedimentos) {
			procedimentoInterface.calcularCampos();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void tocarObjectos(GuiaAcompanhamentoAnestesico guia) {
		
		if (guia.getGuiaOrigem() != null){
			guia.getGuiaOrigem().getIdGuia();
			if (guia.getGuiaOrigem().getGuiaOrigem() != null){
				guia.getGuiaOrigem().getGuiaOrigem().isInternacao();
			}
		}
		
		Set<ProcedimentoInterface> procedimentos = guia.getProcedimentos();
		for (ProcedimentoInterface procedimento : procedimentos) {
			procedimento.getHonorariosGuiaHonorarioMedico().size();
		}
		
	}

	public GuiaAcompanhamentoAnestesico conferirDados(GuiaAcompanhamentoAnestesico guia) throws Exception {
		ImplDAO.save(guia);
		return guia;
	}
}
