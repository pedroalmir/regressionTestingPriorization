package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.utils.Utils;

public class LevantamentoGuiasProcedimentoTrocadoIndevidamenteFecharGuia {

	public static void main(String[] args) {
//		aplicaAcordoProcedimentosConsultaQueGerarramIGF();
		
		SearchAgent sa = new SearchAgent();
		Criteria criteria = sa.createCriteriaFor(GuiaCompleta.class);
		sa.addParameter(new GreaterEquals("dataAtendimento", Utils.createData("01/06/2012")));
		criteria.createAlias("procedimentos", "procs");
		criteria.createAlias("procs.procedimentoDaTabelaCBHPM", "cbhpm");
		criteria.add(Restrictions.ge("cbhpm.codigo", "10101001"));
		criteria.add(Restrictions.le("cbhpm.codigo", "10101045"));
		List<GuiaCompleta> guias = criteria.list();
		
		System.out.println("Somatório antes da intervenção: " + getSomatorioGuias(guias));
		
		aplicarAcordoAProcedimentosDaGuia(guias);
		
		System.out.println("Somatório depois da intervenção: " + getSomatorioGuias(guias));
		
	}

	private static void aplicarAcordoAProcedimentosDaGuia(
			List<GuiaCompleta> guias) {
		for(GuiaCompleta guia : guias){
			Set<ProcedimentoInterface> procedimentos = guia.getProcedimentosSimples();
			for(ProcedimentoInterface procedimento : procedimentos){
				procedimento.aplicaValorAcordo();
			}
		}
	}
	
	private static BigDecimal getSomatorioGuias(List<GuiaCompleta> guias){
		BigDecimal somatorio = BigDecimal.ZERO;
		for(GuiaCompleta guia : guias){
			somatorio = somatorio.add(guia.getValorTotal());
		}
		return somatorio;
	}
	
	private static void aplicaAcordoProcedimentosConsultaQueGerarramIGF() {
		SearchAgent sa = new SearchAgent();
		List<String> autorizacoes09_2012 = Arrays.asList("1752268", "1751772", "1748410", "1755392", "1762643",
				"1748555", "1752052", "1752009", "1752015", "1762646",
				"1762647", "1755858", "1750037", "1748681", "1762401",
				"1753512", "1766375", "1753621", "1751985", "1748415",
				"1753821", "1749525", "1766652", "1755710", "1759543",
				"1756318", "1752618", "1767513", "1762624", "1752286");
		
		sa.addParameter(new In("autorizacao", autorizacoes09_2012));
		List<GuiaCompleta> guias = sa.list(GuiaCompleta.class);
		
		System.out.println("Somatório antes da intervenção: " + getSomatorioGuias(guias));
		
		aplicarAcordoAProcedimentosDaGuia(guias);
		
		System.out.println("Somatório depois da intervenção: " + getSomatorioGuias(guias));
	}
}
