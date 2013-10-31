package br.com.infowaypi.ecare.relatorio.financeiro;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
/**
 * Classe que representam um item [Boleto ou Cobranca]
 * utilizado no resumo de um relatorio de fluxo diario de cobrancas
 * */
public class ItemResumoFluxoBoletos implements Comparable<ItemResumoFluxoBoletos>{
	
	//Atributos de um item
	private Date dataPagamento;
	
	private BigDecimal valorPago = BigDecimal.ZERO;
	
	private int quantidadeBoletosDiario = 0;
	/**
	 * Metodo que retorna a data de pagamento de um item
	 * */
	public Date getDataPagamento() {
		return dataPagamento;
	}
	/**
	 * @param dataPagamento
	 * Metodo que configura a data de pagamento de um item
	 * */
	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	/**
	 * Metodo que retorna o valor paga de um item
	 * */
	public BigDecimal getValorPago() {
		return valorPago;
	}
	/**
	 * @param valorPago
	 * Metodo que configura o valor paga de um item
	 * */
	public void setValorPago(BigDecimal valorPago) {
		this.valorPago = valorPago;
	}
	/**
	 * Metodo que retorna a quantidade de itens
	 * */
	public int getQuantidadeBoletosDiario() {
		return quantidadeBoletosDiario;
	}
	/**
	 * @param quantidadeBoletosDiario
	 * Metodo que configura a quantidade de um item
	 * */
	public void setQuantidadeBoletosDiario(int quantidadeBoletosDiario) {
		this.quantidadeBoletosDiario = quantidadeBoletosDiario;
	}
	
	public int compareTo(ItemResumoFluxoBoletos o) {
		return this.getDataPagamento().compareTo(o.getDataPagamento());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ItemResumoFluxoBoletos)) {
			return false;
		}
		ItemResumoFluxoBoletos outroItem = (ItemResumoFluxoBoletos) obj;
		
		return new EqualsBuilder()
		.append(this.getDataPagamento(), outroItem.getDataPagamento())
		.isEquals();
	}
	
}
