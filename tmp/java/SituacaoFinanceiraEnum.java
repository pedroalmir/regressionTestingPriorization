package br.com.infowaypi.ecarebc.enums;

public enum SituacaoFinanceiraEnum {
	ABERTA(1, "Aberto(a)"),
	CANCELADA(2, "Cancelado(a)"),
	FECHADA(3, "Fechado(a)"),
	FATURADA(4, "Faturado(a)");
	
	private int value;
	private String descricao;
	
	SituacaoFinanceiraEnum(int value, String descricao){
		this.value = value;
		this.descricao = descricao;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}