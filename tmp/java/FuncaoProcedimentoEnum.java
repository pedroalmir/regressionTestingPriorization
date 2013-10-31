package br.com.infowaypi.ecare.financeiro.ordenador;

public enum FuncaoProcedimentoEnum {
	
	PROFISSIONAL_RESPONSAVEL(1,"Profissional Responsável"),
	CIRURGIAO(2,"Cirurgião"),
	PRIMEIRO_AUXILIAR(3,"1º Auxiliar"),
	SEGUNDO_AUXILIAR(4,"2º Auxiliar"),
	TERCEIRO_AUXILIAR(5,"3º Auxiliar"),
	ANESTESISTA(6,"Anestesista");
	
	private int valor;
	private String descricao;
	
	private FuncaoProcedimentoEnum(int valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}
	
	public int valor() {
		return valor;
	}
	
	public String descricao() {
		return descricao;
	}
	
}
