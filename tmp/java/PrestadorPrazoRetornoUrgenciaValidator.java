package br.com.infowaypi.ecarebc.associados.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class PrestadorPrazoRetornoUrgenciaValidator extends AbstractValidator<Prestador> {

	public boolean templateValidator(Prestador prest) throws ValidateException {
		if(prest.getPeriodoParaVoltaNaUrgencia() > Prestador.PRAZO_MAXIMO_RETORNO_URGENCIA){
			throw new ValidateException("O Prazo m�ximo para retorno de urg�ncia � de 30 dias");
		}
		
		return true;
	}
}
