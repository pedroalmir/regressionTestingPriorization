package br.com.infowaypi.ecare.financeiro.faturamento;

public enum GeracaoFaturamentoEnum {
	FATURAMENTO_NORMAL("Normal", new GeracaoFaturamentoNormal()),
	FATURAMENTO_PASSIVO("Passivo", new GeracaoFaturamentoPassivo());
	
	private String tipoFaturamento;
	private AbstractGeracaoFaturamento geracaoFaturamento;
	
	private GeracaoFaturamentoEnum(String tipoFaturamento, AbstractGeracaoFaturamento geracaoFaturamento) {
		this.tipoFaturamento = tipoFaturamento;
		this.geracaoFaturamento = geracaoFaturamento;
	}

	public String getTipoFaturamento() {
		return tipoFaturamento;
	}

	public AbstractGeracaoFaturamento getGeracaoFaturamento() {
		return geracaoFaturamento;
	}
}
