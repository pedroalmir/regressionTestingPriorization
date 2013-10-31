package br.com.infowaypi.ecarebc.financeiro;

import java.util.Set;

import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.situations.ColecaoSituacoesInterface;

public interface TitularFinanceiroInterface extends ColecaoSituacoesInterface{
	
	public abstract Set<DependenteFinanceiroInterface> getDependentesFinanceiro();
	
	public abstract Set<FluxoFinanceiroInterface> getFluxosFinanceiros();
	
	public abstract InformacaoFinanceiraInterface getInformacaoFinanceira();
	
	public abstract String getIdentificacao();
	
	public abstract String getNome();
	
	public abstract String getTipo();

}