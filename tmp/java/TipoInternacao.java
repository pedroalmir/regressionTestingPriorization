package br.com.infowaypi.ecarebc.atendimentos.enums;

public enum TipoInternacao {
	
	HOSPITALAR("HOSPITALAR",1),
	HOME_CARE("HOMECARE",2);
	
	String descricao;
	int valor;

	TipoInternacao(String descricao,int valor){
		this.descricao = descricao;
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public int getValor() {
		return valor;
	}

}
