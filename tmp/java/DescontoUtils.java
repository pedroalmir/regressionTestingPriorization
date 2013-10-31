package br.com.infowaypi.ecare.procedimentos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infowaypi.ecare.procedimentos.enums.ProcedimentoDescontoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

public class DescontoUtils {

	/**
	 * Calcula o desconto para um tipo de procedimento da guia q é aplicado em mais de uma face de um dente
	 * @param enumProc
	 */
	@SuppressWarnings("unchecked")
	public static void calcularDescontoParaProcedimento(Collection<ProcedimentoOdonto> procedimentos, ProcedimentoDescontoEnum enumProc){
		Map<String, List<ProcedimentoOdonto>> mapDentes = organizarProcedimentosPorElemento(procedimentos);
		
		Integer numRestauracoes;
		
		//Buscando o procedimento
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("codigo", enumProc.getCodigo()));
		List<TabelaCBHPM> procs = sa.list(TabelaCBHPM.class);
		TabelaCBHPM procDesconto = procs.get(0);
		
		for (String d : mapDentes.keySet()) {
			numRestauracoes = 0;
			
			for (ProcedimentoOdonto p : mapDentes.get(d)) 
				if(p.getProcedimentoDaTabelaCBHPM().equals(procDesconto))
					++numRestauracoes;
			
			for (ProcedimentoOdonto p : mapDentes.get(d)) {
				if(p.getProcedimentoDaTabelaCBHPM().equals(procDesconto)){
					BigDecimal valorTotal = procDesconto.getValor();
					BigDecimal proporcao = BigDecimal.ZERO;
					
					if (numRestauracoes == 2) 
						proporcao = valorTotal.multiply(enumProc.getPorcOfIndex(0));
					if (numRestauracoes == 3) 
						proporcao = valorTotal.multiply(enumProc.getPorcOfIndex(1));
					if (numRestauracoes == 4) 
						proporcao = valorTotal.multiply(enumProc.getPorcOfIndex(2));
					
					valorTotal = valorTotal.add(proporcao);
					BigDecimal valorProcedimento = valorTotal.divide(new BigDecimal(numRestauracoes), 2, BigDecimal.ROUND_HALF_UP);
					p.setValorAtualDoProcedimento(valorProcedimento);
					p.setValorCoParticipacao(valorProcedimento.divide(new BigDecimal(procDesconto.getModeracao()), 2, BigDecimal.ROUND_HALF_UP));
				}
			}
		}
	}
	
	/**
	 * Organiza os procedimentos da guia em um map com o elemento e sua coleção de procedimentos aplicados
	 * @return
	 */
	public static Map<String, List<ProcedimentoOdonto>> organizarProcedimentosPorElemento(Collection<ProcedimentoOdonto> procedimentos) {
		Map<String, List<ProcedimentoOdonto>> mapElementos = new HashMap<String, List<ProcedimentoOdonto>>();
		List<ProcedimentoOdonto> procs = null;
		String dente = "";
		
		for (ProcedimentoOdonto proc : procedimentos) {
			Integer elementoAplicado = proc.getProcedimentoDaTabelaCBHPM().getElementoAplicado();
			
			if(elementoAplicado.equals(EstruturaOdontoEnum.FACE.getValor())){
//				dente = proc.getFace().getDente().getDescricao();
				
				procs = new ArrayList<ProcedimentoOdonto>();
				if (mapElementos.containsKey(dente)){
					procs = mapElementos.get(dente);
				}
				
				procs.add(proc);
				mapElementos.put(dente, procs);
			}
		}
		return mapElementos;
	}
}
