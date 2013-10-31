package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
/**
 * Interface que define métodos utilizados na filtragem e no processamento das críticas
 * @author wislanildo
 *
 */
public interface ServiceApresentacaoCriticasFiltradas {

	/**
	 * Método usado para informar os tipos de críticas exibíveis no fluxo em questão.
	 */
	public void filtrarCriticasApresentaveis(GuiaSimples<?> guia);
	
	/**
	 * Método responsável por marcar a crítica como avaliada e armazenar a descisão do
	 * auditor sobre o procedimento associado a esta crítica.
	 * 
	 * @param guia
	 */
	public void processaSituacaoCriticas(GuiaSimples<?> guia);
}
