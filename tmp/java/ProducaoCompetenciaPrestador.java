package br.com.infowaypi.ecare.relatorio.producao;

import br.com.infowaypi.ecarebc.associados.Prestador;

public class ProducaoCompetenciaPrestador extends ProducaoCompetencia {
	
	private Prestador prestador;
	
	public ProducaoCompetenciaPrestador(Prestador prestador){
		this.prestador = prestador;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

}
