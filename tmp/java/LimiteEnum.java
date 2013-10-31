package br.com.infowaypi.ecare.enums;

/**
 * Enum para encapsular os limites de consumo dos segurados
 * @author Danilo Nogueira Portela
 */
public enum LimiteEnum {

	CONSULTA(2, 5, 7, 10),
	EXAME(12, 18, 24, 42),
	CONSULTA_ODONTO(1, 3, 4, 6),
	EXAME_ODONTO(25, 35, 50, 75);
	
	private Integer mensal, trimestral, semestral, anual; 
	
	LimiteEnum(Integer mensal, Integer trimestral, Integer semestral, Integer anual){
		this.mensal = mensal;
		this.trimestral = trimestral;
		this.semestral = semestral;
		this.anual = anual;
	}
	
	public Integer getLimiteMensal(){
		return this.mensal;
	}
	
	public Integer getLimiteTrimestral(){
		return this.trimestral;
	}
	
	public Integer getLimiteSemestral(){
		return this.semestral;
	}
	
	public Integer getLimiteAnual(){
		return this.anual;
	}
}
