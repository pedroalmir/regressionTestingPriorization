package br.com.infowaypi.ecarebc.financeiro.conta;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.utils.Utils;

public abstract class ImplPagamento  extends ImplColecaoSituacoesComponent implements PagamentoInterface {

	protected BigDecimal valorCobrado;
	protected Date dataVencimento;
	protected Date competencia;
	protected Date dataPagamento;
	protected Date dataValidade;
	protected BigDecimal valorPago;
		
	/**
	 * Valor de juros, multas e outros encargos que foram pagos.
	 */
	protected BigDecimal valorJurosMultaEncargosPago;
	/**
	 * Valor de juros, multas e outros encargos que foram cobrados.
	 */
	protected BigDecimal valorJurosMultaEncargosCobrado;
	
	public ImplPagamento(){
		this.valorCobrado = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.valorPago = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.valorJurosMultaEncargosCobrado = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.valorJurosMultaEncargosPago = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal getValorCobrado() {
		return valorCobrado;
	}
	
	public void setValorCobrado(BigDecimal valorCobrado) {
		this.valorCobrado = valorCobrado;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}
	
	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public String getCompetenciaFormatada(){
		return Utils.format(competencia, "MM/yyyy");
	}
	
	public Date getCompetencia() {
		return competencia;
	}
	
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public BigDecimal getValorPago() {
		return valorPago;
	}
	
	public void setValorPago(BigDecimal valorPago) {
		this.valorPago = valorPago;
	}

	public BigDecimal getValorJurosMultaEncargosPago() {
		return valorJurosMultaEncargosPago;
	}

	public void setValorJurosMultaEncargosPago(
			BigDecimal valorJurosMultaEncargosPago) {
		this.valorJurosMultaEncargosPago = valorJurosMultaEncargosPago;
	}

	/** 
	 * @return Adição de multas e juros ao valor cobrado no boleto
	 */
	public BigDecimal getValorCobradoComMultasEJuros(){
		return valorCobrado.add(valorJurosMultaEncargosCobrado);
	}
	
	/**
	 * Fornece o valor de juros, multas e outros encargos que foram cobrados. Campo calculado.
	 * @return
	 */
	public BigDecimal getValorJurosMultaEncargosCobrado() {
		return valorJurosMultaEncargosCobrado;
	}

	/**
	 * Seta o valor de juros, multas e outros encargos que foram cobrados.
	 * @return
	 */
	public void setValorJurosMultaEncargosCobrado(
			BigDecimal valorJurosMultaEncargosCobrado) {
		this.valorJurosMultaEncargosCobrado = valorJurosMultaEncargosCobrado;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}
	
}
