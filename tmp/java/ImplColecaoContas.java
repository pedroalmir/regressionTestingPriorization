/*
 * Criado em 04/05/2006
 */
package br.com.infowaypi.ecarebc.financeiro.conta;

import java.util.Set;

import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;

public abstract class ImplColecaoContas extends ImplColecaoSituacoesComponent implements ComponenteColecaoContasInterface {
	
	private ComponenteColecaoContas colecaoContas;
	
	public ImplColecaoContas() {
		super();
		colecaoContas = new ComponenteColecaoContas();
	}
	
	public Set<ContaInterface> getContas() {
		return this.getColecaoContas().getContas();
	}

	public ComponenteColecaoContas getColecaoContas() {
		return colecaoContas;
	}


	public void setColecaoContas(ComponenteColecaoContas colecaoContas) {
		if (colecaoContas != null)
			this.colecaoContas = colecaoContas;
	}
}