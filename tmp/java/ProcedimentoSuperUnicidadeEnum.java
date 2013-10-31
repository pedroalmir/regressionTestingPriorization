package br.com.infowaypi.ecarebc.atendimentos.enums;
/**
 * 
 * @author Danilo
 *
 */
public enum ProcedimentoSuperUnicidadeEnum {
	
	EXODONTIA_PERMANENTE("Exodontia Permanente","99000005"),
	EXODONTIA_DECIDUO("Exodontia Decíduo","99000055");
	
	private String descricao;
	private String codigo;
	
	ProcedimentoSuperUnicidadeEnum(String descricao, String codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public ProcedimentoSuperUnicidadeEnum getEnumValue(){
		return this;
	}

}
