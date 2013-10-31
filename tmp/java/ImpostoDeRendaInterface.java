package br.com.infowaypi.ecarebc.financeiro.faturamento.retencao;

import java.math.BigDecimal;

public interface ImpostoDeRendaInterface extends ImpostoInterface{

	public Long getIdImpostoDeRenda();

	public float getAliquota();
	
	public void setAliquota(float aliquota);

	public Integer getTipoDePessoa();

	public void setTipoDePessoa(Integer tipoDePessoa);

	public BigDecimal getValorDeducao();

	public void setValorDeducao(BigDecimal valorDeducao);

	public BigDecimal getValorFaixaAte();

	public void setValorFaixaAte(BigDecimal valorFaixaAte);

	public BigDecimal getValorFaixaDe();

	public void setValorFaixaDe(BigDecimal valorFaixaDe);

}