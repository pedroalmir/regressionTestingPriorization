package br.com.infowaypi.ecarebc.atendimentos.validators;

public enum EnumValidadeGuia {
	
	VALIDADE_GUIA_SIMPLES(30),
	VALIDADE_GUIA_COMPLETA(10),
	VALIDADE_DEMAIS_GUIAS(180);
	
	private int numeroDias;

	private EnumValidadeGuia(int numeroDias) {
		this.numeroDias = numeroDias;
	}

	public int getNumeroDias() {
		return numeroDias;
	}
}
