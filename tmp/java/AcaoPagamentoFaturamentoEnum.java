package br.com.infowaypi.ecare.financeiro.faturamento;

public enum AcaoPagamentoFaturamentoEnum {

	INFORMAR_PENDENCIA("Informar Pendência"),
	REGISTRAR_PAGAMENTO("Registrar Pagamento");
	
	private String descricao;

	private AcaoPagamentoFaturamentoEnum(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public AcaoPagamentoFaturamentoEnum getAcao() {
		return this;
	}
}
