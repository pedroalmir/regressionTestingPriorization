package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;

/**
 * Classe para valida��o de estruturas odontol�gicas com template method
 * @author Danilo Nogueira Portela
 */
public abstract class AbstractEstruturaOdontoValidator {

	/** 
	 * Executa o validate de cada classe escrevendo o nome da classe no in�cio
	 */
	public boolean execute(EstruturaOdonto estrutura, ProcedimentoOdonto proc) throws Exception{
//		System.out.println(this.getClass().getSimpleName());
		return this.templateValidator(estrutura, proc);
	}

	protected abstract boolean templateValidator(EstruturaOdonto estrutura, ProcedimentoOdonto proc) throws Exception;
}
