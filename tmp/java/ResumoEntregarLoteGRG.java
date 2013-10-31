package br.com.infowaypi.ecare.resumos;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;

public class ResumoEntregarLoteGRG {

	private Prestador prestador;
	private List<GuiaRecursoGlosa> guias = null;

	public ResumoEntregarLoteGRG(List<GuiaRecursoGlosa> guias, Prestador prestador) {
		this.prestador = prestador;
		this.guias = guias;
		ordenarGuias(guias);
	}
	
	private void ordenarGuias(List<GuiaRecursoGlosa> guias){
		Collections.sort(guias, new Comparator<GuiaRecursoGlosa>(){
			@Override
			public int compare(GuiaRecursoGlosa g1, GuiaRecursoGlosa g2) {
				return g1.getDataTerminoAtendimento().compareTo(g2.getDataTerminoAtendimento());
			}
		});
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public List<GuiaRecursoGlosa> getGuias() {
		return guias;
	}

	public void setGuias(List<GuiaRecursoGlosa> guias) {
		this.guias = guias;
	}

}
