package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de tabela Cbhpm com template method
 * @author Danilo Nogueira Portela
 */
public abstract class AbstractTabelaCBHPMValidator<T extends TabelaCBHPM> {

	/** 
	 * Executa o validate de cada classe escrevendo o nome da classe no início
	 */
	public boolean execute(T cbhpm) throws ValidateException{
//		System.out.println(this.getClass().getSimpleName());
		return this.templateValidator(cbhpm);
	}

	protected abstract boolean templateValidator(T cbhpm) throws ValidateException;
}
