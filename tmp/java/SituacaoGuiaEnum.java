package br.com.infowaypi.ecare.enums;

/**
 * Enum para situação da guia pro relatório de andamento de auditoria.
 */

public enum SituacaoGuiaEnum {
	
	TODAS("Todas"),
	RECEBIDO("Recebido(a)"),
	AUDITADO("Auditado(a)"),
	CONFIRMADO("Confirmado(a)"),;

	private String descricao;
	
	SituacaoGuiaEnum(String descricao){
		this.setDescricao(descricao);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
