package br.com.infowaypi.ecare.segurados.validators;

import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação básica de dependentes
 * @author Danilo Nogueira Portela
 */
public class DependenteValidator extends AbstractSeguradoValidator<Dependente>{
 
	protected boolean templateValidator(Dependente dep) throws ValidateException{
		
		return true;
	}
}
