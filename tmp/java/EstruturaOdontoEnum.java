package br.com.infowaypi.ecarebc.odonto.enums;

public enum EstruturaOdontoEnum {

	NENHUM(-1,"Nenhum(a)"),
	TODOS(0,"Todos(as)"),
	DENTICAO(1,"Dentição"),
	ARCADA(2,"Arcada"),
	QUADRANTE(3,"Hemiarcada"),
	DENTE(4,"Elemento"),
	FACE(5,"Face");
	 
	private String descricao;
	private Integer valor;
	
	EstruturaOdontoEnum(Integer valor, String descricao){
		this.descricao = descricao;
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}
	
	public static String getElemento(Integer valor){
		switch (valor) {
			case -1: return EstruturaOdontoEnum.NENHUM.getDescricao();
			case 0: return EstruturaOdontoEnum.TODOS.getDescricao();
			case 1: return EstruturaOdontoEnum.DENTICAO.getDescricao();
			case 2: return EstruturaOdontoEnum.ARCADA.getDescricao();
			case 3: return EstruturaOdontoEnum.QUADRANTE.getDescricao();
			case 4: return EstruturaOdontoEnum.DENTE.getDescricao();
			case 5: return EstruturaOdontoEnum.FACE.getDescricao();
			default: return null;
		}
	}
	
	
	
}
