package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

/**
 * Classe para validação de procedimentos com template method
 * @author Danilo Nogueira Portela
 */
public abstract class AbstractProcedimentoValidator<P extends ProcedimentoInterface, G extends GuiaSimples> {

	/** 
	 * Executa o validate de cada classe escrevendo o nome da classe no início
	 */
	public boolean execute(P proc, G guia) throws Exception{
//		System.out.println(this.getClass().getSimpleName());
		return this.templateValidator(proc, guia);
	}

	protected abstract boolean templateValidator(P proc, G guia) throws Exception;
}
