package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;

/**
 * Interface responsável por unicaficar os validates de Procedimento e os validates de Guia
 * @author Idelvane/ Dannylvan 
 *
 * @param <G>
 */
public interface AtendimentoValidator<G extends GuiaSimples> {
	
	public boolean execute(G guia) throws Exception;
}
