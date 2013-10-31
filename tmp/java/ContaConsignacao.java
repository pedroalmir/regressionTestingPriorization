package br.com.infowaypi.ecare.financeiro.arquivo;

import java.math.BigDecimal;

import br.com.infowaypi.ecare.segurados.Empresa;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;

public class ContaConsignacao extends Conta {
	/**
	 * 
	 */
	public static final int PARCELA = 3;
	
	private static final long serialVersionUID = 1L;
	private BigDecimal valorCoparticipacao;
	private BigDecimal valorFinanciamento;
	private BigDecimal valorPrevidencia;
	private BigDecimal valorRetornoCoparticipacao;
	private BigDecimal percentualAliquota;
	private Empresa empresa;
	
	public ContaConsignacao() {
		valorCoparticipacao = BigDecimal.ZERO;
		valorFinanciamento = BigDecimal.ZERO;
		valorPrevidencia = BigDecimal.ZERO;
		valorRetornoCoparticipacao = BigDecimal.ZERO;
	}
	
	public BigDecimal getValorCoparticipacao() {
		return valorCoparticipacao;
	}
	
	public void setValorCoparticipacao(BigDecimal valorCoparticipacao) {
		this.valorCoparticipacao = valorCoparticipacao;
	}
	
	public BigDecimal getValorFinanciamento() {
		return valorFinanciamento;
	}
	
	public void setValorFinanciamento(BigDecimal valorFinanciamento) {
		this.valorFinanciamento = valorFinanciamento;
	}
	
	public BigDecimal getValorPrevidencia() {
		return valorPrevidencia;
	}
	
	public void setValorPrevidencia(BigDecimal valorPrevidencia) {
		this.valorPrevidencia = valorPrevidencia;
	}
	
	public BigDecimal getValorRetornoCoparticipacao() {
		return valorRetornoCoparticipacao;
	}
	
	public void setValorRetornoCoparticipacao(BigDecimal valorRetornoCoparticipacao) {
		this.valorRetornoCoparticipacao = valorRetornoCoparticipacao;
	}
	
	public Empresa getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public BigDecimal getPercentualAliquota() {
		return percentualAliquota;
	}
	
	public void setPercentualAliquota(BigDecimal percentualAliquota) {
		this.percentualAliquota = percentualAliquota;
	}
	
}
