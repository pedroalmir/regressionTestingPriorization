package br.com.infowaypi.ecarebc.enums;

public enum Visibilidade {

	MEDICO(0,"Médico"),
	AMBOS(1,"Ambos"),
	ODONTOLOGICO(2,"Odontológico");
	
	private String descricao;
	private Integer valor;
	
	Visibilidade(Integer valor,String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}
	
	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static Visibilidade getEnum(int valor){
		switch (valor) {
		case 0:
			return Visibilidade.MEDICO;
		case 1:
			return Visibilidade.AMBOS;
		case 2:
			return Visibilidade.ODONTOLOGICO;
		default:
			return null;
		}
		
	}
}
