package br.com.infowaypi.ecarebc.financeiro.faturamento.retencao;

import java.math.BigDecimal;

public interface InssInterface extends ImpostoInterface {

	public Long getIdInss();

	public float getAliquota();
	
	public void setAliquota(float aliquota);

	public BigDecimal getValorDescontoMaximo();

	public void setValorDescontoMaximo(BigDecimal valorDescontoMaximo);

	public BigDecimal getValorDescontoMinimo();

	public void setValorDescontoMinimo(BigDecimal valorDescontoMinimo);

}