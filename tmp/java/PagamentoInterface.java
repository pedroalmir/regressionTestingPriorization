package br.com.infowaypi.ecarebc.financeiro.conta;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.msr.situations.ColecaoSituacoesInterface;

public interface PagamentoInterface extends ColecaoSituacoesInterface{
	public static final String ABERTO = "Aberto(a)";
	public static final String VENCIDO = "Vencido(a)";
	public static final String PAGO = "Pago(a)";
	public static final String CANCELADO = "Cancelado(a)";
	public static final String INADIMPLENTE ="Inadimplente";
	public static final String NEGOCIADO = "Negociado(a)";
	public static final String PARCELADO = "Parcelado(a)";
	
	public void processarVencimento();
	/**
	 * Utilizado para recuperar o valor da Cobrança.
	 * @return Retorna o valor da Cobrança.
	 */
	public  BigDecimal getValorCobrado();
	public  void setValorCobrado(BigDecimal valorCobrado);

	public Date getDataVencimento();
	public void setDataVencimento(Date dataVencimento);

	public String getCompetenciaFormatada();
	public Date getCompetencia();
	public void setCompetencia(Date competencia);

	public Date getDataPagamento();
	public void setDataPagamento(Date dataPagamento);

	public BigDecimal getValorPago();
	public void setValorPago(BigDecimal valorPago);
}