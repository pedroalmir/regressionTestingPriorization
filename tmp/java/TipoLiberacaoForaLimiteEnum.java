package br.com.infowaypi.ecare.enums;

public enum TipoLiberacaoForaLimiteEnum {
    
	LIBERACAO_NAO_PERMITIDA(0),
	LIBERACAO_AUDITOR(1), 
	LIBERACAO_MPPS(2);
	
	private Integer codigo;
	
	TipoLiberacaoForaLimiteEnum(Integer codigo) {
		this.codigo = codigo;
	}
	
	public Integer codigo(){
		return codigo;
	}
}
