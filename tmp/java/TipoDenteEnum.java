package br.com.infowaypi.ecarebc.odonto.enums;

public enum TipoDenteEnum {

	NENHUM("Nenhum(a)"),
	TODOS("Todos(as)"),
	INCISIVO_CENTRAL("Incisivo Central"),
	INCISIVO_LATERAL("Incisivo Lateral"),
	CANINO("Canino"),
	PREMOLAR1("1� Pr�-molar"),
	PREMOLAR2("2� Pr�-molar"),
	MOLAR1("1� Molar"),
	MOLAR2("2� Molar"),
	MOLAR3("3� Molar");
	
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
