package br.com.infowaypi.ecarebc.financeiro;

import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.financeiro.conta.ComponenteColecaoContas;
import br.com.infowaypi.ecarebc.financeiro.conta.PagamentoInterface;

public interface FluxoFinanceiroInterface extends PagamentoInterface {
	
	public abstract Long getIdFluxoFinanceiro();
	
	public abstract void setIdFluxoFinanceiro(Long idFluxoFinanceiro);
	
	public abstract ComponenteColecaoContas getColecaoContas();
	
	public void setColecaoContas(ComponenteColecaoContas colecaoContas);
	
	public abstract TitularFinanceiroInterface getTitularFinanceiro();
	
	public Set<GuiaSimples> getGuias();
	
	public void setGuias(Set<GuiaSimples> guias);
	
	public boolean isConsignacao();
	public boolean isFaturamento();
	public boolean isCobranca();
}