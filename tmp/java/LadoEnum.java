package br.com.infowaypi.ecarebc.odonto.enums;

public enum LadoEnum {

	NENHUM("Nenhum(a)"),
	TODOS("Todos(as)"),
	DIREITO("Direito"),
	ESQUERDO("Esquerdo");
	
	private String descricao;
	
	LadoEnum(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
