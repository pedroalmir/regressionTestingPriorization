package br.com.infoway.ecare.services.tarefasCorrecao.relatorioMedAlliance;

import java.util.HashMap;
import java.util.Map;

public class Relatorio {
	private Map<String, Linha> linhaPorSegurado;
	
	public Relatorio() {
		linhaPorSegurado = new HashMap<String, Linha>();
	}

	public Map<String, Linha> getLinhaPorSegurado() {
		return linhaPorSegurado;
	}
}
