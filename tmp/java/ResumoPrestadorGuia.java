package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;

import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * Resumo que apresenta as consultas eletivas e de urgência de um determinado prestador.
 * @author benedito
 *
 */
public class ResumoPrestadorGuia {

	/**
	 * Nome fantasia do prestador.
	 * Ex.: Hospital São Marcos.
	 */
	private String prestadorFantasia;
	
	/**
	 * Quantidade de consultas eletivas do prestador.
	 */
	private int qtdeConsultasEletivas;
	
	/**
	 * Valor das consultas eletivas do prestador.
	 */
	private BigDecimal vlrConsultasEletivas;
	
	/**
	 * Quantidade de consultas de urgência do prestador.
	 */
	private int qtdeConsultasUrgencia;
	
	/**
	 * Valor das consultas de urgência do prestador.
	 */
	private BigDecimal vlrConsultasUrgencia;
	
	/**
	 * Quantidade de consultas eletivas e de urgência do prestador.
	 */
	private int qtdeTotalConsultas;
	
	/**
	 * Valor das consultas eletivas e de urgência do prestador.
	 */
	private BigDecimal vlrTotalConsultas;
	
	/**
	 * Razão entre a quantidade de consultas eletivas e de urgência do prestador e a quantidade de consultas eletivas e de urgência realizadas em um determinado mês.
	 */
	private BigDecimal percentualProducao;
	
	/**
	 * Razão entre o valor das consultas eletivas e de urgência do prestador e o valor das consultas eletivas e de urgência realizadas em um determinado mês.
	 */
	private BigDecimal percentualFaturamento;
	
	public ResumoPrestadorGuia() {
		this.vlrConsultasEletivas = BigDecimal.ZERO;
		this.vlrConsultasUrgencia = BigDecimal.ZERO;
		this.vlrTotalConsultas = BigDecimal.ZERO;
		this.percentualProducao = BigDecimal.ZERO;
		this.percentualFaturamento = BigDecimal.ZERO;
	}

	public ResumoPrestadorGuia(String prestadorFantasia, int qtdeConsultasEletivas, BigDecimal vlrConsultasEletivas, int qtdeConsultasUrgencia, BigDecimal vlrConsultasUrgencia, int qtdeTotalGeral, BigDecimal vlrTotalGeral) {
		this.prestadorFantasia = prestadorFantasia;
		
		this.qtdeConsultasEletivas = qtdeConsultasEletivas;
		this.vlrConsultasEletivas = vlrConsultasEletivas;
		
		this.qtdeConsultasUrgencia = qtdeConsultasUrgencia;
		this.vlrConsultasUrgencia = vlrConsultasUrgencia;
		
		this.qtdeTotalConsultas = this.qtdeConsultasEletivas + this.qtdeConsultasUrgencia;
		this.vlrTotalConsultas = this.vlrConsultasEletivas.add(this.vlrConsultasUrgencia);

		this.percentualProducao = MoneyCalculation.divide((new BigDecimal(this.qtdeTotalConsultas)).multiply(new BigDecimal(100)), new BigDecimal(qtdeTotalGeral).floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);;
		this.percentualFaturamento = MoneyCalculation.divide(this.vlrTotalConsultas.multiply(new BigDecimal(100)), vlrTotalGeral.floatValue()).setScale(2, BigDecimal.ROUND_HALF_UP);;
	}

	public String getPrestadorFantasia() {
		return prestadorFantasia.toUpperCase();
	}

	public void setPrestadorFantasia(String prestadorFantasia) {
		this.prestadorFantasia = prestadorFantasia;
	}

	public int getQtdeConsultasEletivas() {
		return qtdeConsultasEletivas;
	}

	public void setQtdeConsultasEletivas(int qtdeConsultasEletivas) {
		this.qtdeConsultasEletivas = qtdeConsultasEletivas;
	}

	public BigDecimal getVlrConsultasEletivas() {
		return vlrConsultasEletivas;
	}

	public void setVlrConsultasEletivas(BigDecimal vlrConsultasEletivas) {
		this.vlrConsultasEletivas = vlrConsultasEletivas;
	}

	public int getQtdeConsultasUrgencia() {
		return qtdeConsultasUrgencia;
	}

	public void setQtdeConsultasUrgencia(int qtdeConsultasUrgencia) {
		this.qtdeConsultasUrgencia = qtdeConsultasUrgencia;
	}

	public BigDecimal getVlrConsultasUrgencia() {
		return vlrConsultasUrgencia;
	}

	public void setVlrConsultasUrgencia(BigDecimal vlrConsultasUrgencia) {
		this.vlrConsultasUrgencia = vlrConsultasUrgencia;
	}

	public int getQtdeTotalConsultas() {
		return qtdeTotalConsultas;
	}

	public void setQtdeTotalConsultas(int qtdeTotalConsultas) {
		this.qtdeTotalConsultas = qtdeTotalConsultas;
	}

	public BigDecimal getVlrTotalConsultas() {
		return vlrTotalConsultas;
	}

	public void setVlrTotalConsultas(BigDecimal vlrTotalConsultas) {
		this.vlrTotalConsultas = vlrTotalConsultas;
	}

	public BigDecimal getPercentualProducao() {
		return percentualProducao;
	}

	public void setPercentualProducao(BigDecimal percentualProducao) {
		this.percentualProducao = percentualProducao;
	}

	public BigDecimal getPercentualFaturamento() {
		return percentualFaturamento;
	}

	public void setPercentualFaturamento(BigDecimal percentualFaturamento) {
		this.percentualFaturamento = percentualFaturamento;
	}

}
