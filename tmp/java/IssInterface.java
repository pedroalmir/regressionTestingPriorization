package br.com.infowaypi.ecarebc.financeiro.faturamento.retencao;


public interface IssInterface extends ImpostoInterface {

	public Long getIdIss();

	public float getAliquota();
	
	public void setAliquota(float aliquota);
	
	public Integer getTipoDePessoa();

	public void setTipoDePessoa(Integer tipoDePessoa);

}