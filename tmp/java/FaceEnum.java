package br.com.infowaypi.ecarebc.odonto.enums;

public enum FaceEnum {

	NENHUMA("Nenhum(a)"),
	TODAS("Todos(as)"),
	OCLUSAL_INCISIVA("Oclusal/Incisiva"),
	LINGUAL_PALATINA("Lingual/Palatina"),
	MESIAL("Mesial"),
	VESTIBULAR("Vestibular"),
	DISTAL("Distal");
	
	private String descricao;
	
	FaceEnum(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
