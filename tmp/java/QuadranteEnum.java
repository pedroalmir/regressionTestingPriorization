package br.com.infowaypi.ecarebc.odonto.enums;


/**
 * Enumeration com as especificações de quadrantes
 * @author Danilo Nogueira Portela
 *
 */
public enum QuadranteEnum {

	NENHUM(-1),
	TODOS(0),
	SUPERIOR_DIREITO_PERMANENTE(1),
	SUPERIOR_ESQUERDO_PERMANENTE(2),
	INFERIOR_ESQUERDO_PERMANENTE(3),
	INFERIOR_DIREITO_PERMANENTE(4),
	SUPERIOR_DIREITO_DECIDUO(5),
	SUPERIOR_ESQUERDO_DECIDUO(6),
	INFERIOR_ESQUERDO_DECIDUO(7),
	INFERIOR_DIREITO_DECIDUO(8);
	
	private Integer numero;
	
	QuadranteEnum(Integer numero){
		this.numero = numero;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

}
