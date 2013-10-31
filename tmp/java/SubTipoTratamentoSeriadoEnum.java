package br.com.infowaypi.ecarebc.atendimentos.enums;

public enum SubTipoTratamentoSeriadoEnum {

	//subtipo de primeiro ciclo
	PRIMEIRA_LINHA(1,"1� linha"),
	SEGUNDA_LINHA(2,"2� linha"),
	TERCEIRA_LINHA(3,"3� linha"),
	QUARTA_LINHA(4,"4� linha"),
	
	//subtipo de hemodi�lise
	AGUDA_POR_SESSAO(5,"Aguda por Sess�o"),
	CRONICA_POR_SESSAO(6,"Cr�nica por Sess�o"),
	CONTINUA(7,"Cont�nua (12h)");
	
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
