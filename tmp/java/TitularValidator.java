package br.com.infowaypi.ecare.segurados.validators;

import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação básica de titulares
 * @author Danilo Nogueira Portela
 */
public class TitularValidator extends AbstractSeguradoValidator<Titular>{
 
	protected boolean templateValidator(Titular tit) throws ValidateException{
		return true;
	}
}
