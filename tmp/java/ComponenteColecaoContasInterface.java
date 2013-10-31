/*
 * Atualizado em 02/02/2006
 */
package br.com.infowaypi.ecarebc.financeiro.conta;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public interface ComponenteColecaoContasInterface extends Serializable {

	public Set<ContaInterface> getContas();
	
	public void setContas(Set<ContaInterface> contas);
	
	public void add(ContaInterface conta);
	
	public void addAll(Collection< ContaInterface> contas);
}