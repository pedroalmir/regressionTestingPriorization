package br.com.infowaypi.ecare.financeiro.faturamento;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.resumos.ResumoFaturamentosSR;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.OR;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

public class CancelarFaturamento {

	@SuppressWarnings("unchecked")
	public ResumoFaturamentos buscarFaturamentos(Date competencia, UsuarioInterface usuario) {
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("competencia", competencia));
		
		Equals isAberto = new Equals("status", Constantes.FATURAMENTO_ABERTO);
		
		if(usuario.isPossuiRole("suporte")){
			Equals isFechado = new Equals("status", Constantes.FATURAMENTO_FECHADO);
			sa.addParameter(new OR(isAberto,isFechado));
		} else {
			sa.addParameter(isAberto);
		}
		
		List<AbstractFaturamento> faturamentos = sa.list(AbstractFaturamento.class);
		
		for (AbstractFaturamento faturamento : faturamentos) {
			faturamento.getColecaoContas().getContas().size();
			faturamento.getItensGuiaFaturamento().size();
			for (GuiaFaturavel guia : faturamento.getGuiasFaturaveis()) {
				for (SituacaoInterface situacao : guia.getSituacoes()) {
					situacao.getUsuario();
				}
			}
		}
		
		ResumoFaturamentosSR resumo = new ResumoFaturamentosSR(faturamentos);
		resumo.setCompetencia(competencia);
		Assert.isNotEmpty(resumo.getFaturamentos(), "Não foram encontrados faturamentos abertos para esta competência.");
		
		return resumo;
	}
	
	public ResumoFaturamentos conferirDados(ResumoFaturamentos resumo, UsuarioInterface usuario) throws Exception {
		for (AbstractFaturamento faturamento : resumo.getFaturamentos()) {
			cancelaFaturamento(faturamento, usuario);
		}
		return resumo;
	}

	private static void cancelaFaturamento(AbstractFaturamento faturamento, UsuarioInterface usuario) throws Exception {
		faturamento.setStatus(Constantes.FATURAMENTO_CANCELADO);
		
		for(ContaInterface conta : faturamento.getColecaoContas().getContas()) {
			conta.mudarSituacao(null, Constantes.SITUACAO_CANCELADO, MotivoEnum.CANCELAMENTO_DE_FATURAS.getMessage(), new Date());
			ImplDAO.save(conta);
		}
		
		for (ItemGuiaFaturamento honorarioMedico : faturamento.getItensGuiaFaturamento()) {
			honorarioMedico.setFaturamento(null);
			ImplDAO.save(honorarioMedico);
		}
		
		for (GuiaFaturavel guia : faturamento.getGuiasFaturaveis()) {
			SituacaoInterface situacaoAnterior = guia.getSituacaoAnterior(guia.getSituacao());
			guia.mudarSituacao(usuario, situacaoAnterior.getDescricao(), MotivoEnum.CANCELAMENTO_DE_FATURAS.getMessage(), situacaoAnterior.getDataSituacao());
			guia.setFaturamento(null);
			ImplDAO.save(faturamento);
		}
		
		ImplDAO.save(faturamento);
	}
}
