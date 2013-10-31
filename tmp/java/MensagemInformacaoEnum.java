package br.com.infowaypi.ecarebc.enums;

public enum MensagemInformacaoEnum{
	 
	PADRAO(""),
	ZERAR_INTERNACAO_URGENCIA("O valor da consulta foi zerado por que a guia gerou uma internação em menos de 12 horas após seu atendimento.");
	
	private String descricao;
	
	MensagemInformacaoEnum(String descricao){
		this.descricao = descricao;
	}
	
	public String descricao(){
		return descricao;
	}
	
	public static MensagemInformacaoEnum getEnum(int value) {
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
