package br.com.infowaypi.ecare.services.recurso;

import java.util.List;
/**
 * Classe de resumo para auxiliar no fluxo de deferimento/indeferimento de recurso de glosa.
 * @author Luciano Rocha
 *
 */
public class ResumoDeferimentoIndeferimentoDeRecurso {

	private GuiaRecursoGlosa recurso;
	private List<GuiaRecursoGlosa> recursos;
	
	public ResumoDeferimentoIndeferimentoDeRecurso() {
	}
	
	public ResumoDeferimentoIndeferimentoDeRecurso(List<GuiaRecursoGlosa> recursos){
		this.recursos = recursos;
	}
	
	public GuiaRecursoGlosa getRecurso() {
		return recurso;
	}

	public void setRecurso(GuiaRecursoGlosa recurso) {
		this.recurso = recurso;
	}

	public List<GuiaRecursoGlosa> getRecursos() {
		return recursos;
	}

	public void setRecursos(List<GuiaRecursoGlosa> recursos) {
		this.recursos = recursos;
	}
}