package br.com.infowaypi.ecare.relatorio.financeiro;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.com.infowaypi.ecare.financeiro.Cobranca;

/**
 * @author anderson 
 * Classe responsavel por criar um resumo para o fluxo de boletos
 * 
 * */
public class ResumoFluxoBoletos {
	//Atributos representando os boletos
	private Set<ItemResumoFluxoBoletos> fluxosDia;	
	//Somatorio contendo Quantidade total de boletos
	private int quantidadeTotal = 0;
	//Somatorio contendo o valor total dos boletos
	private BigDecimal valorTotal = BigDecimal.ZERO;
	//Construtor	
	public ResumoFluxoBoletos(){
		this.fluxosDia = new TreeSet<ItemResumoFluxoBoletos>();
	}
	//Construtor
	public ResumoFluxoBoletos(Map<Date,Set<Cobranca>> mapaCobrancas){
		this();
		this.computarResumo(mapaCobrancas);
	}
	/**
	 * Metodo que retorna o conjunto de itens[Cobrancas] 
	 * */
	public Set<ItemResumoFluxoBoletos> getFluxosDia() {
		return fluxosDia;
	}
	/**
	 * @param fluxosDia
	 * Metodo que configura o conjunto de itens[Cobrancas] 
	 * */
	public void setFluxosDia(Set<ItemResumoFluxoBoletos> fluxosDia) {
		this.fluxosDia = fluxosDia;
	}
	/**
	 * Metodo que retorna a quantidade total itens[Cobrancas] 
	 * */
	public int getQuantidadeTotal() {
		
		return quantidadeTotal;
	}
	/**
	 * @param quantidadeTotal
	 * Metodo que configura a quantidade total de itens[Cobrancas] 
	 * */
	public void setQuantidadeTotal(int quantidadeTotal) {
		this.quantidadeTotal = quantidadeTotal;
	}
	/**
	 * @param fluxosDia
	 * Metodo que retorna o valor total de itens[Cobrancas] 
	 * */
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	/**
	 * @param valorTotal
	 * Metodo que configura o valor total de itens[Cobrancas] 
	 * */
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	/**
	 * @param mapaCobrancas
	 * Metodo que computa os itens do resumo
	 * 
	 * */
	public void computarResumo(Map<Date, Set<Cobranca>> mapaCobrancas){
		
		ItemResumoFluxoBoletos itemResumo = null;
				
		for (Date data : mapaCobrancas.keySet()) {
			
			itemResumo = new ItemResumoFluxoBoletos();
			
			itemResumo.setDataPagamento(data);
			
			for (Cobranca cobranca : mapaCobrancas.get(data)) {
				
				itemResumo.setQuantidadeBoletosDiario(itemResumo.getQuantidadeBoletosDiario()+1);
				
				itemResumo.setValorPago(itemResumo.getValorPago().add(cobranca.getValorPago()));
				
			}
			this.setValorTotal(valorTotal.add(itemResumo.getValorPago()));
			
			this.setQuantidadeTotal(quantidadeTotal+itemResumo.getQuantidadeBoletosDiario());
			
			fluxosDia.add(itemResumo);
		}
		
	}
	
	
}
