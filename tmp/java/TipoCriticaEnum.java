package br.com.infowaypi.ecarebc.atendimentos.enums;

/**
 * Indica a partir de qual causa uma crítica foi gerada.
 * @author Junior
 *
 */
public enum TipoCriticaEnum {
	
	CRITICA_DLP_SUBGRUPO(1),
	CRITICA_DLP_CID(2);
	
	private int valor;
	
	TipoCriticaEnum(int valor) {
		this.valor = valor;
	}

	public int valor() {
		return valor;
	}

}
