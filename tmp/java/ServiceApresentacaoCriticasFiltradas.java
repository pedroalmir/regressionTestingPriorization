package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
/**
 * Interface que define m�todos utilizados na filtragem e no processamento das cr�ticas
 * @author wislanildo
 *
 */
public interface ServiceApresentacaoCriticasFiltradas {

	/**
	 * M�todo usado para informar os tipos de cr�ticas exib�veis no fluxo em quest�o.
	 */
	public void filtrarCriticasApresentaveis(GuiaSimples<?> guia);
	
	/**
	 * M�todo respons�vel por marcar a cr�tica como avaliada e armazenar a descis�o do
	 * auditor sobre o procedimento associado a esta cr�tica.
	 * 
	 * @param guia
	 */
	public void processaSituacaoCriticas(GuiaSimples<?> guia);
}
