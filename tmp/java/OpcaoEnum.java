package br.com.infowaypi.ecare.utils;

/**
 * Usada para representar valores booleanos como op��es em um combo para perguntas cuja resposta � simples (sim, n�o, etc)
 * @autor Alan Santos, today 14/09/2010 - 10:20:48
 */
public enum OpcaoEnum {
	SIM(true, "Sim"),
	NAO(false, "N�o");
	
	private Boolean valor;
	private String descricao;

	OpcaoEnum(Boolean valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}

	public Boolean getValor() {
		return valor;
	}

	public void setValor(Boolean valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public static OpcaoEnum valueOf(boolean valor){
		for (OpcaoEnum opcaoEnum : OpcaoEnum.values()){
			if (opcaoEnum.getValor().equals(valor)){
				return opcaoEnum;
			}
		}
		return null;		
	}
	
}
