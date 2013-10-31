package br.com.infowaypi.ecarebc.atendimentos.enums;

public enum SubTipoTratamentoSeriadoEnum {

	//subtipo de primeiro ciclo
	PRIMEIRA_LINHA(1,"1ª linha"),
	SEGUNDA_LINHA(2,"2ª linha"),
	TERCEIRA_LINHA(3,"3ª linha"),
	QUARTA_LINHA(4,"4ª linha"),
	
	//subtipo de hemodiálise
	AGUDA_POR_SESSAO(5,"Aguda por Sessão"),
	CRONICA_POR_SESSAO(6,"Crônica por Sessão"),
	CONTINUA(7,"Contínua (12h)");
	
	private Integer valor;
	private String subTipo;

	private SubTipoTratamentoSeriadoEnum (Integer valor,String subtipo) {
		this.valor = valor;
		this.subTipo = subtipo;
	}

	public String subTipo() {
		return subTipo;
	}

	public Integer valor() {
		return valor;
	}
	
	public static SubTipoTratamentoSeriadoEnum getSubTipoTratamento(Integer valor){
		if (valor.equals(1))
			return SubTipoTratamentoSeriadoEnum.PRIMEIRA_LINHA;
		if (valor.equals(2))
			return SubTipoTratamentoSeriadoEnum.SEGUNDA_LINHA;
		if (valor.equals(3))
			return SubTipoTratamentoSeriadoEnum.TERCEIRA_LINHA;
		if (valor.equals(4))
			return SubTipoTratamentoSeriadoEnum.QUARTA_LINHA;
		if (valor.equals(5))
			return SubTipoTratamentoSeriadoEnum.AGUDA_POR_SESSAO;
		if (valor.equals(6))
			return SubTipoTratamentoSeriadoEnum.CRONICA_POR_SESSAO;
		if (valor.equals(7))
			return SubTipoTratamentoSeriadoEnum.CONTINUA;
		
		return null;
	}

}
