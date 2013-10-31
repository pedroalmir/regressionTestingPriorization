package br.com.infowaypi.ecare.services.financeiro.faturamento.ordenador;

import java.util.List;

import br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador.Ordenador;

public class ResumoOrdenadores {
	List<Ordenador> ordenadores;
	
	public ResumoOrdenadores() {
	}
	
	public ResumoOrdenadores(List<Ordenador> ordenadores) {
		super();
		this.ordenadores = ordenadores;
	}

	public List<Ordenador> getOrdenadores() {
		return ordenadores;
	}

	public void setOrdenadores(List<Ordenador> ordenadores) {
		this.ordenadores = ordenadores;
	}
	
}
