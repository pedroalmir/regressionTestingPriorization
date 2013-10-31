package br.com.infowaypi.ecarebc.atendimentos.enums;

import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;

public enum TipoProcedimentoEnum {

	PROCEDIMENTO("PN", "Procedimento", Procedimento.class),
	PROCEDIMENTO_ODONTO("PO", "ProcedimentoOdonto", ProcedimentoOdonto.class);
	
	private String tipo;
	private String descricao;
	private Class<? extends Procedimento> classe;
	
	TipoProcedimentoEnum(String tipo, String descricao, Class<? extends Procedimento> classe) {
		this.tipo = tipo;
		this.descricao = descricao;
		this.classe = classe;
	}

	public String tipo() {
		return tipo;
	}

	public String descricao() {
		return descricao;
	}

	public Class<? extends Procedimento> classe() {
		return classe;
	}
	
}