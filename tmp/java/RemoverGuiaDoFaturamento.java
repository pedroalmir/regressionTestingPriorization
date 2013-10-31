package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.financeiro.faturamento.FaturamentoPassivo;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

public class RemoverGuiaDoFaturamento {

	public static void main(String[] args) throws Exception {
		
		String competencia = "09/2012";
		long PRONTOLINDA = 29815917L;
		long SAO_MARCOS = 374002L;
		
		SearchAgent sa = new SearchAgent();

		List<String> autorizacoesProntolinda = Arrays.asList("1752268");
		
		List<String> autorizacoesSaoMarcos = Arrays.asList("1751772",
				"1748410", "1755392", "1762643", "1748555", "1752052",
				"1752009", "1752015", "1762646", "1762647", "1755858",
				"1750037", "1748681", "1762401", "1753512", "1766375",
				"1753621", "1751985", "1748415", "1753821", "1749525",
				"1766652", "1755710", "1759543", "1756318", "1752618",
				"1767513", "1762624", "1752286");
		
		List<String> autorizacoesAlterarSituacao = Arrays.asList("1751772",
				"1748410", "1755392", "1762643", "1748555", "1752052",
				"1752009", "1752015", "1762646", "1762647", "1755858",
				"1750037", "1748681", "1762401", "1753512", "1766375",
				"1753621", "1751985", "1748415", "1753821", "1749525",
				"1766652", "1755710", "1759543", "1756318", "1752618",
				"1767513", "1762624", "1752286", "1752268");
		
		
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		corrigeSituacao(sa, autorizacoesAlterarSituacao);
		tx.commit();
		
//		corrigirFaturamento(sa, competencia, autorizacoesProntolinda, PRONTOLINDA);
//		corrigirFaturamento(sa, competencia, autorizacoesSaoMarcos, SAO_MARCOS);
		
		
	}

	private static void corrigeSituacao(SearchAgent sa, List<String> autorizacoesAlterarSituacao) throws Exception {
		sa.addParameter(new In("autorizacao",autorizacoesAlterarSituacao));
		List<GuiaFaturavel> guias = sa.list(GuiaFaturavel.class);
		
		Usuario usuario = ImplDAO.findById(1L, Usuario.class);
		
		for(GuiaFaturavel guia : guias){
			System.out.println("Antes: " + guia.getSituacao().getDescricao());
			
			SituacaoInterface situacaoAnterior = guia.getSituacaoAnterior(guia.getSituacao());
			guia.mudarSituacao(usuario, situacaoAnterior.getDescricao(), "Guia não paga no faturamento 09/2012 e ajustada para entrar no faturamento 10/2012", Utils.createData("04/12/2012"));
			ImplDAO.save(guia);
			
			System.out.println("Depois: " + guia.getSituacao().getDescricao());
		}
	}

	private static void corrigirFaturamento(SearchAgent sa, String competencia, List<String> autorizacoes, long prestador ) throws Exception {
		sa.clearAllParameters();
		sa.addParameter(new In("autorizacao", autorizacoes));
		List<GuiaFaturavel> guias = sa.list(GuiaFaturavel.class);
		
		BigDecimal somatorioGuias = getSomatorioGuias(guias);
		
		sa.clearAllParameters();
		sa.addParameter(new Equals("competencia", Utils.getCompetencia(competencia)));
		sa.addParameter(new Equals("status", Constantes.FATURAMENTO_FECHADO));
		sa.addParameter(new Equals("prestador.idPrestador", prestador));
		FaturamentoPassivo faturamento = sa.uniqueResult(FaturamentoPassivo.class);
		
		desassociarGuiasDoFaturamento(guias, faturamento);
		
		
		String fantasia = faturamento.getPrestador().getPessoaJuridica().getFantasia();
		
		System.out.println("Valor faturamento " + fantasia + " antes da intervenção: " + faturamento.getValorBruto());
		faturamento.setValorBruto(faturamento.getValorBruto().subtract(somatorioGuias));
		System.out.println("Valor faturamento " + fantasia + " depois da intervenção: " + faturamento.getValorBruto());
		
		ImplDAO.save(faturamento);
	}

	private static void desassociarGuiasDoFaturamento(List<GuiaFaturavel> guias, FaturamentoPassivo faturamento) throws Exception {
		faturamento.getGuiasFaturaveis().removeAll(guias);
		for(GuiaFaturavel guia : guias){
			guia.setFaturamento(null);
			ImplDAO.save(guia);
		}
	}

	private static BigDecimal getSomatorioGuias(List<GuiaFaturavel> guias) {
		BigDecimal totalGuias = BigDecimal.ZERO;
		for(GuiaFaturavel guia : guias){
			totalGuias = totalGuias.add(guia.getValorTotal());
		}
		return totalGuias;
	}
}
