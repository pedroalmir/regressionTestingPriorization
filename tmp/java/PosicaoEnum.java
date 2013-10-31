package br.com.infowaypi.ecarebc.odonto.enums;

public enum PosicaoEnum {

	NENHUMA("Nenhum(a)"),
	TODOS("Todos(as)"),
	SUPERIOR("Superior"),
	INFERIOR("Inferior");
	
	private String descricao;
	
	PosicaoEnum(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
