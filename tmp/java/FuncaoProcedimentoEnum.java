package br.com.infowaypi.ecare.financeiro.ordenador;

public enum FuncaoProcedimentoEnum {
	
	PROFISSIONAL_RESPONSAVEL(1,"Profissional Respons�vel"),
	CIRURGIAO(2,"Cirurgi�o"),
	PRIMEIRO_AUXILIAR(3,"1� Auxiliar"),
	SEGUNDO_AUXILIAR(4,"2� Auxiliar"),
	TERCEIRO_AUXILIAR(5,"3� Auxiliar"),
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
