/*
 * Atualizado em 02/02/2006
 */
package br.com.infowaypi.ecarebc.financeiro.conta;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ComponenteColecaoContas implements ComponenteColecaoContasInterface {

	private static final long serialVersionUID = 1L;
	
	private Long idColecaoContas;
	private Set<ContaInterface> contas;

	public ComponenteColecaoContas(Set<ContaInterface> contas){
		this();
		this.contas = contas;
	}
	
	public ComponenteColecaoContas() {
		super();
		contas = new LinkedHashSet<ContaInterface>();
	}
	
	public Long getIdColecaoContas() {
		return idColecaoContas;
	}

	protected void setIdColecaoContas(Long idColecaoContas) {
		this.idColecaoContas = idColecaoContas;
	}
	
	public Set<ContaInterface> getContas() {
		return contas;
	}

	public void setContas(Set<ContaInterface> contas) {
		this.contas = contas;
	}
	
	public void add(ContaInterface conta){
		this.contas.add(conta);
	}

	public void addAll(Collection< ContaInterface> contas){
		this.contas.addAll(contas);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append("Id", this.idColecaoContas)
				.append("Numero de contas", this.contas.size())
				.toString();
	}
}