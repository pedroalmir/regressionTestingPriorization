package br.com.infowaypi.ecarebc.enums;


/**
 * @author Danilo Nogueira Portela
 */
 
public enum TipoSeguradoEnum {

	TODOS(0," Todos"),
	TITULAR(1,"Titular"),
	DEPENDENTE(2,"Dependente"),
	PENSIONISTA(3,"Pensionista"),
	DEPENDENTE_SUPLEMENTAR(4,"Dependente Suplementar");
	
	
	private String descricao;
	private int value;
	
	TipoSeguradoEnum(int value, String descricao){
		this.descricao = descricao;
		this.value = value;
	}
	
	public int value(){
		return this.value;
	}
	
	public String descricao(){
		return this.descricao;
	}
	
	/**
	 * Retorna o tipo de segurado a partir de seu valor.
	 */
	public static String getTipoSegurado(int valor) {
		for (TipoSeguradoEnum tipo : TipoSeguradoEnum.values()) {
			if (tipo.value() == valor) {
				return tipo.descricao();
			}
		}
		
		return null;
	}
	
}
