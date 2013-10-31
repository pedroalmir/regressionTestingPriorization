package br.com.infowaypi.ecarebc.atendimentos.enums;


public enum TipoConsultaEnum {
	
	NORMAL("Normal","10101012"),
	PSICOLOGICA("Psicologica","95000002"),//PSICOTERAPIA INDIVIDUAL - POR SESSAO, ok //nao usado
	FISIOTERAPEUTA("Fisioterapeuta","10101014"),//nao usado E ANTIGO
	/* if[INTEGRACAO] 
	CONSULTA_ODONTOLOGICA("Consulta Odontológica", "99000001"),
	else[INTEGRACAO] */
	CONSULTA_ODONTOLOGICA("Consulta Odontológica", "99000002"),
	/* end[INTEGRACAO] */
	CLINICO_PROMOTOR("Consulta Clínico Promotor", "99000001"),
	CONSULTA_INICIAL("Perícia Inicial","99000004"),
	CONSULTA_ODONTOLOGICA_ESPECIALIZADA("Consulta Odontológica Especializada", "99000002"),
	URGENCIA("Urgência", "10101039");
	
	private String descricao;
	private String codigo;
	
	TipoConsultaEnum(String descricao, String codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public static boolean isConsulta(String codigo){
		for(TipoConsultaEnum tipoConsulta : values()){
			if(tipoConsulta.getCodigo().equals(codigo))
				return true;
		}
		return false;
	}
	
	public TipoConsultaEnum getEnumValue(){
		return this;
	}

}
