package br.com.infowaypi.ecare.enums;

public enum TipoDePerguntaEnum {

	DOENCA		("Doen�a"),
	TRATAMENTO	("Tratamento");

	TipoDePerguntaEnum(String descricao) {
		this.descricao = descricao;
	}

	private String descricao;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
