package br.com.infowaypi.ecarebc.enums;
/**
 * @author Josino Rodrigues 
 */
 
public enum DependenciaEnum {

	TITULAR(0,"Titular"),
	IRMAO(1,"Irmão"),
	MARIDO_MULHER(2,"Marido/Mulher"),
	PAI_MAE(3,"Pai/Mãe"),
	FILHO(4,"Filho(a)"),
	OUTROS(5,"Outros"),
	SOBRINHO(6,"Sobrinho(a)");
	
	private String descricao;
	private int value;
	
	DependenciaEnum(int value, String descricao){
		this.descricao = descricao;
		this.value = value;
	}
	
	public int value(){
		return this.value;
	}
	
	public String descricao(){
		return this.descricao;
	}
	
	public static DependenciaEnum getEnum(int value) {
		switch (value) {
		case 0:
			return DependenciaEnum.TITULAR;
		case 1:
			return DependenciaEnum.IRMAO;
		case 2:
			return DependenciaEnum.MARIDO_MULHER;
		case 3:
			return DependenciaEnum.PAI_MAE;
		case 4:
			return DependenciaEnum.FILHO;
		case 5:
			return DependenciaEnum.OUTROS;
		case 6:
			return DependenciaEnum.SOBRINHO;
		default:
			return null;
		}
	}
	
}
