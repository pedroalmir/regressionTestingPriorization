package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.io.Serializable;

public class FaixaGuiaFaturamento implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long idFaixaGuia;
	private Integer valorFaixaDe;
	private Integer valorFaixaAte;
	
	
	public Long getIdFaixaGuia() {
		return idFaixaGuia;
	}
	public Integer getValorFaixaAte() {
		return valorFaixaAte;
	}
	public void setValorFaixaAte(Integer valorFaixaAte) {
		this.valorFaixaAte = valorFaixaAte;
	}
	public Integer getValorFaixaDe() {
		return valorFaixaDe;
	}
	public void setValorFaixaDe(Integer valorFaixaDe) {
		this.valorFaixaDe = valorFaixaDe;
	}
	public void setIdFaixaGuia(Long idFaixaGuia) {
		this.idFaixaGuia = idFaixaGuia;
	}	
	
	
	
	
	
	
	
}
