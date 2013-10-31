package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.validators.AtendimentoValidator;

/**
 * Classe para validação de guias com template method
 * @author Erick Passos
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractGuiaValidator<G extends GuiaSimples> implements AtendimentoValidator<G>{
 
	/**
	 * Executa o validate da classe escrevendo o nome da classe validadora no início
	 */
	public boolean execute(G guia) throws Exception{
		return this.templateValidator(guia);
	}

	protected abstract boolean templateValidator(G guia) throws Exception;
}
