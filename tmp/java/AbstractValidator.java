package br.com.infowaypi.ecarebc.associados.validators;


import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para valida��o de associados com template method
 * @author Danilo Nogueira Portela
 */
public abstract class AbstractValidator<A extends Object>{
 
	/**
	 * Executa o validate de cada classe escrevendo o nome da classe no in�cio
	 */
	public boolean execute(A objeto) throws ValidateException{
		return this.templateValidator(objeto);
	}

	protected abstract boolean templateValidator(A asc) throws ValidateException;
}
