package br.com.infowaypi.ecare.financeiro;

import java.util.LinkedHashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.financeiro.conta.Boleto;

public class ResumoBoletos {
	
	private Set<Boleto> boletos;
	
	public ResumoBoletos(){
		boletos = new LinkedHashSet<Boleto>();
	}
	
	public Set<Boleto> getBoletos() {
		return boletos;
	}
	
	public void setBoletos(Set<Boleto> boletos) {
		this.boletos = boletos;
	}
	
	public boolean add(Boleto boleto){
		return boletos.add(boleto);
	}
	
}
