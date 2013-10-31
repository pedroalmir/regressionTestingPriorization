package br.com.infowaypi.ecare.financeiro.boletos;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.segurados.DependenteInterface;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;

public class BoletoUtils {
	
	public static BigDecimal getValorCoparticipacao(TitularFinanceiroSR titularFinanceiroSR, Map<AbstractSegurado,Set<GuiaSimples>> mapGuiasPorSegurado){
		BigDecimal valorCoparticipacao = BigDecimal.ZERO;
		if(mapGuiasPorSegurado.keySet().contains(titularFinanceiroSR)){
			for (GuiaSimples guia : mapGuiasPorSegurado.get(titularFinanceiroSR)) {
				valorCoparticipacao = valorCoparticipacao.add(guia.getValorCoParticipacao());
			}
		}
		
		for (DependenteInterface dependente: titularFinanceiroSR.getDependentes()) {
			if(mapGuiasPorSegurado.keySet().contains(dependente)){
				for (GuiaSimples guia : mapGuiasPorSegurado.get(dependente)) {
					valorCoparticipacao = valorCoparticipacao.add(guia.getValorCoParticipacao());
				}
			}
			
		}
		
		return valorCoparticipacao;
	}
	
	public static Set<GuiaSimples> getGuiasCoparticipacao(TitularFinanceiroSR titularFinanceiroSR, Map<AbstractSegurado,Set<GuiaSimples>> mapGuiasPorSegurado){
		Set<GuiaSimples> guias = new HashSet<GuiaSimples>();
		
		if(mapGuiasPorSegurado.keySet().contains(titularFinanceiroSR)){
			guias.addAll(mapGuiasPorSegurado.get(titularFinanceiroSR));
		}
		
		for (DependenteInterface dependente: titularFinanceiroSR.getDependentes()) {
			if(mapGuiasPorSegurado.keySet().contains(dependente)){
				guias.addAll(mapGuiasPorSegurado.get(dependente));
			}
			
		}
		
		return guias;
	}
	
	public static BigDecimal getValorCoparticipacaoSuplementar(TitularFinanceiroSR dependenteSuplementar, Set<GuiaSimples> guiasPorSegurado, Cobranca cobranca){
		BigDecimal valorCoparticipacao = BigDecimal.ZERO;
		
		if(guiasPorSegurado == null || guiasPorSegurado.isEmpty())
			return valorCoparticipacao;
		
		for (GuiaSimples guia : guiasPorSegurado) {
			cobranca.getGuias().add(guia);
			guia.setFluxoFinanceiro(cobranca);
			valorCoparticipacao = valorCoparticipacao.add(guia.getValorCoParticipacao());
		}
			
		return valorCoparticipacao;
	}
	
	
}
