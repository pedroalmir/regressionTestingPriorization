package br.com.infowaypi.ecarebc.financeiro;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Marcus bOolean
 *
 */
public abstract class AbstractFinanceiroService {
	private static final String[] AUTORIZACOES_GUIAS_PAGAS = {"243827","237931","242923",
		"130185F",
		"130183H",
		"183259B",
		"130181K",
		"130181J",
		"130203M",
		"130203L",
		"157323G",
		"157323F",
		"183251B",
		"183251C",
		"130514G",
		"130503F",
		"130503E",
		"130503C",
		"130503D",
		"174253C",
		"174253D",
		"106167K",
		"9884L"};
	private static final Long[] IDS_PRESTADORES_NAO_PAGOS = {
		540426L,
		374402L,
		540185L,
		526374L,
		525659L,
		374178L,
		374312L,
		1822240L,
		534008L,
		4946031L,
		374010L,
		539083L,
		525760L,
		525649L,
		525726L,
		594626L,
		373940L,
		525641L,
		528052L,
		570629L,
		373698L
	};
	private static final Long[] IDS_PRESTADORES_ORDENADOS = {525649L,594626L};
	

	
	public static List<String> getAutorizacoesPagas() {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < AUTORIZACOES_GUIAS_PAGAS.length; i++) {
			result.add(AUTORIZACOES_GUIAS_PAGAS[i]);
		}
		
		return result;
	}
	
	public static List<Long> getIdsPrestadoresNaoPagos() {
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < IDS_PRESTADORES_NAO_PAGOS.length; i++) {
			result.add(IDS_PRESTADORES_NAO_PAGOS[i]);
		}
		
		return result;
	}
	
	public static List<Long> getIdsPrestadoresOrdenados() {
		List<Long> result = new ArrayList<Long>();
		for (int i = 0; i < IDS_PRESTADORES_ORDENADOS.length; i++) {
			result.add(IDS_PRESTADORES_ORDENADOS[i]);
		}
		
		return result;
	}
}
