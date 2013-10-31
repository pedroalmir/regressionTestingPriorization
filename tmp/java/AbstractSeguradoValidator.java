package br.com.infowaypi.ecare.segurados.validators;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe para validação de segurados com template method
 * @author Danilo Nogueira Portela
 */
public abstract class AbstractSeguradoValidator<S extends Segurado>{
 
	/**
	 * Executa o validate da classe escrevendo o nome da classe validadora no início
	 */
	public boolean execute(S segurado, UsuarioInterface usuario) throws ValidateException{
//		System.out.println(this.getClass().getSimpleName());
		return this.templateValidator(segurado);
	}

	protected abstract boolean templateValidator(S segurado) throws ValidateException;
}
