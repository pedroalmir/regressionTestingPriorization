package br.com.infowaypi.ecare.services;

import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaAcompanhamentoAnestesico;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class AutorizarAcompanhamentoAnestesico {

	public GuiaAcompanhamentoAnestesico buscarGuia(String autorizacao) {
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", autorizacao.trim()));
		GuiaAcompanhamentoAnestesico guia = sa.uniqueResult(GuiaAcompanhamentoAnestesico.class);
	
		if (guia != null) {
			if (!guia.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())) {
				throw new RuntimeException("Esta guia não pode ser autorizada por que se encontra na situação " + guia.getSituacao().getDescricao());
			}
		} else {
			throw new RuntimeException("Nenhum guia foi encontrada.");
		}
		
		GuiaSimples guiaOrigem = guia.getGuiaOrigem();
		if (guiaOrigem.isEmUmaDasSituacoes(SituacaoEnum.SOLICITADO.descricao(), SituacaoEnum.CANCELADO.descricao(), SituacaoEnum.NAO_AUTORIZADO.descricao())) {
			throw new RuntimeException("A guia de exame " + guiaOrigem.getAutorizacao() + " que originou este acompanhamento anestesico está na situação "+guiaOrigem.getSituacao().getDescricao()+ ". Autorize-a primeiro.");
		}
		tocarObjetos(guia);
		return guia;
	}
	
	private void tocarObjetos(GuiaAcompanhamentoAnestesico guia) {
		Set<ProcedimentoInterface> procedimentos = guia.getProcedimentos();
		for (ProcedimentoInterface procedimentoInterface : procedimentos) {
			procedimentoInterface.getSituacoes().size();
			procedimentoInterface.getHonorariosGuiaHonorarioMedico().size();
		}
	}

	public GuiaAcompanhamentoAnestesico autorizarGuia(GuiaAcompanhamentoAnestesico guia, UsuarioInterface usuario) {
		Set<ProcedimentoInterface> procedimentos = guia.getProcedimentosNaoCanceladosENegados();
		boolean todosNegados = true;
		Date dataAutorizacao = new Date();
		
		for (ProcedimentoInterface procedimento : procedimentos) {
			if (procedimento.getAutorizado()) {
				procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_ROLE_INFORMADO.getMessage(usuario.getRole()), dataAutorizacao);
				todosNegados = false;
			} else {
				if (Utils.isStringVazia(procedimento.getMotivo())) {
					throw new RuntimeException("O motivo da não autorização do procedimento deve ser preenchido.");
				}
				procedimento.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), procedimento.getMotivo(), dataAutorizacao);
			}
		}
		
		if (todosNegados) {
			guia.setAutorizado(false);
			guia.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), MotivoEnum.NAO_AUTORIZADO_PELO_ROLE_INFORMADO.getMessage(usuario.getRole()), dataAutorizacao);
		} else {
			guia.setAutorizado(true);
			guia.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_ROLE_INFORMADO.getMessage(usuario.getRole()), dataAutorizacao);
		}
		
		return guia;
	}
	
	public GuiaAcompanhamentoAnestesico salvarGuia(GuiaAcompanhamentoAnestesico guia) throws Exception {
		ImplDAO.save(guia);
		return guia;
	}
}
