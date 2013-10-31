package br.com.infowaypi.ecarebc.enums;
/**
 * @author Josino
 */
public enum SexoEnum {
    
	MASCULINO("Masculino",1),
	FEMININO("Feminino",2),
	AMBOS("Ambos",3);
	
	private String descricao;
	private Integer value;
	
	SexoEnum(String descricao, int value){
		this.descricao = descricao;
		this.value = value;
	}
	
	public String descricao(){
		return descricao;
	}
	
	public Integer value() {
		return value;
	}
	
	public static SexoEnum getEnum(int value) {
		switch (value) {
		case 1:
			return SexoEnum.MASCULINO;
		case 2:
			return SexoEnum.FEMININO;
		case 3:
			return SexoEnum.AMBOS;
		default:
			return null;
		}
	}
	
}
