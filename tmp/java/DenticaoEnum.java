package br.com.infowaypi.ecarebc.odonto.enums;

public enum DenticaoEnum {

	NENHUMA("Nenhum(a)"),
	TODOS("Todos(as)"),
	PERMANENTE("Permanente"),
	DECIDUO("Decíduo");
	
	
	private String descricao;
	
	DenticaoEnum(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
