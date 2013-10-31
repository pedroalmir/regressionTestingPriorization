package br.com.infowaypi.ecare.resumos;

import java.util.ArrayList;
import java.util.Collection;

public class ResumoGeral<T> {
	/**
	 * Lista de qualquer coisa que vai aparecer no resumo
	 */
	private Collection<T> lista;
	
	public ResumoGeral() {
		this.lista = new ArrayList<T>();
	}
	
	public ResumoGeral(Collection<T> lista) {
		this.lista = new ArrayList<T>(lista);
	}

	public Collection<T> getLista() {
		return lista;
	}

	public void setLista(Collection<T> lista) {
		this.lista = lista;
	}
	
	public int getQuantidade() {
		return lista.size();
	}
	
}
