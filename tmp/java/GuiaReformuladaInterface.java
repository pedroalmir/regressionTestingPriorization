package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.situations.SituacaoInterface;

public interface GuiaReformuladaInterface {

	
	public void setAutorizacao(String autorizacao);
	public String getAutorizacao();
	
	public void setSegurado(AbstractSegurado segurado);
	public AbstractSegurado getSegurado();
	
	public void setSituacao(SituacaoInterface situacao);
	public SituacaoInterface getSituacao();

	public void setValorTotal(BigDecimal valorTotal);
	public BigDecimal getValorTotal();
	
	public void setCompetencia(Date competencia);
	public Date getCompetencia();
	
}