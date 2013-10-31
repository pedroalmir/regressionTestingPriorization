package br.com.infowaypi.ecarebc.enums;

public enum TipoServicoCBHPM {

	CONSULTAS(1,"Consultas"),
	EXAMES(2,"Exames"),
	PROC_MEDICOS(3,"Procedimentos Médicos"),
	TERAPIAS(4,"Terapias");
	
	Integer valor;
	String descricao;
	
	private TipoServicoCBHPM(Integer valor,String descricao) {
		this.descricao = descricao;
		this.valor = valor;
	}
	
	public String descricao(){
		return descricao;
	}
	
	public Integer valor(){
		return valor;
	}
	
}
