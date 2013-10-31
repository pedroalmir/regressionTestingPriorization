package br.com.infowaypi.ecare.segurados.validators;

import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para valida��o b�sica de dependentes
 * @author Danilo Nogueira Portela
 */
public class DependenteValidator extends AbstractSeguradoValidator<Dependente>{
 
	protected boolean templateValidator(Dependente dep) throws ValidateException{
		
		return true;
	}
}
