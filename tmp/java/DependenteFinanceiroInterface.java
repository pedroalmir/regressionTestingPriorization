package br.com.infowaypi.ecarebc.financeiro;

import br.com.infowaypi.msr.situations.ColecaoSituacoesInterface;

public interface DependenteFinanceiroInterface extends ColecaoSituacoesInterface{
	
	public abstract TitularFinanceiroInterface getTitularFinanceiro();

}