package br.com.infowaypi.ecarebc.odonto.enums;

public enum TipoDenteEnum {

	NENHUM("Nenhum(a)"),
	TODOS("Todos(as)"),
	INCISIVO_CENTRAL("Incisivo Central"),
	INCISIVO_LATERAL("Incisivo Lateral"),
	CANINO("Canino"),
	PREMOLAR1("1º Pré-molar"),
	PREMOLAR2("2º Pré-molar"),
	MOLAR1("1º Molar"),
	MOLAR2("2º Molar"),
	MOLAR3("3º Molar");
	
	private String descricao;
	
	TipoDenteEnum(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
