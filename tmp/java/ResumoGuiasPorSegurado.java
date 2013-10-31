package br.com.infowaypi.ecare.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.financeiro.faturamento.DetalheValoresFaturamento;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;

/**
 * 
 * @author Diogo Vinícius
 *
 */
public class ResumoGuiasPorSegurado {

	private Set<DetalheValoresFaturamento> detalhesDeGuias;
	private Set<GuiaSimples> guias;
	private AbstractSegurado segurado;
	
	public ResumoGuiasPorSegurado (List<GuiaSimples> guias) {
		this.detalhesDeGuias = new HashSet<DetalheValoresFaturamento>();
		this.detalhesDeGuias.add(new DetalheValoresFaturamento(guias));
		this.guias = new HashSet<GuiaSimples>(guias);
		for (GuiaSimples guia : guias) {
			this.segurado = guia.getSegurado();
			break;
		}
	}

	public Set<DetalheValoresFaturamento> getDetalhesDeGuias() {
		return detalhesDeGuias;
	}

	public Set<GuiaSimples> getGuias() {
		return guias;
	}

	public AbstractSegurado getSegurado() {
		return segurado;
	}
	
}
