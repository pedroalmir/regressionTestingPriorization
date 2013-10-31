package br.com.infowaypi.ecarebc.atendimentos.enums;


public enum TipoConsultaEnum {
	
	NORMAL("Normal","10101012"),
	PSICOLOGICA("Psicologica","95000002"),//PSICOTERAPIA INDIVIDUAL - POR SESSAO, ok //nao usado
	FISIOTERAPEUTA("Fisioterapeuta","10101014"),//nao usado E ANTIGO
	/* if[INTEGRACAO] 
	CONSULTA_ODONTOLOGICA("Consulta Odontol�gica", "99000001"),
	else[INTEGRACAO] */
	CONSULTA_ODONTOLOGICA("Consulta Odontol�gica", "99000002"),
	/* end[INTEGRACAO] */
	CLINICO_PROMOTOR("Consulta Cl�nico Promotor", "99000001"),
	CONSULTA_INICIAL("Per�cia Inicial","99000004"),
	CONSULTA_ODONTOLOGICA_ESPECIALIZADA("Consulta Odontol�gica Especializada", "99000002"),
	URGENCIA("Urg�ncia", "10101039");
	
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
