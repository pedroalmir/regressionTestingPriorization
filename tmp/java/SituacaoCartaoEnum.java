package br.com.infowaypi.ecare.enums;

public enum SituacaoCartaoEnum{
	GERADO("Gerado"),
	COBRADO("Cobrado"),
	PAGO("Pago");

	SituacaoCartaoEnum(String descricao){
		this.descricao = descricao;
	}
	
	String descricao;
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
