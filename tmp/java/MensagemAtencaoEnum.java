package br.com.infowaypi.ecarebc.enums;

public enum MensagemAtencaoEnum{
	 
	PADRAO("");
	
	private String descricao;
	
	MensagemAtencaoEnum(String descricao){
		this.descricao = descricao;
	}
	
	public String descricao(){
		return descricao;
	}
	
	public static MensagemAtencaoEnum getEnum(int value) {
//		switch (value) {
//		case 1:
//			return MensagemEnum.MASCULINO;
//		case 2:
//			return MensagemEnum.FEMININO;
//		case 3:
//			return MensagemEnum.AMBOS;
//		default:
			return null;
//		}
	}
	
}
