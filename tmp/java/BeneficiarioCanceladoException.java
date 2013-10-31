package br.com.infowaypi.ecarebc.exceptions;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;

public class BeneficiarioCanceladoException extends Exception {
	
	public BeneficiarioCanceladoException() {
		super(MensagemErroEnum.SEGURADO_INATIVO_NO_SISTEMA.getMessage());
	}

}
