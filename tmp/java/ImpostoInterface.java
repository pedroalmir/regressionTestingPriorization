package br.com.infowaypi.ecarebc.financeiro.faturamento.retencao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public interface ImpostoInterface extends Serializable{

//	public Float getValor(Float valorBase);
	public float getAliquota();
	public BigDecimal getValorDeducao();
	public String getDescricao();
	public void setValorBaseCalculo(BigDecimal valorBase);
	public BigDecimal getValorBaseCalculo();
	public BigDecimal getValor();
	public Date getCompetencia();
	public void setCompetencia(Date competencia);
}