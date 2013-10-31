package br.com.infowaypi.ecare.validacao.services;

import java.util.Date;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.utils.Utils;

public class DataInicialFinalValidator {

	public void validarDatas(String dataInicialString, String dataFinalString){

		Date dataInicial = Utils.parse(dataInicialString);
		Date dataFinal = Utils.parse(dataFinalString);
		
		if(dataInicial!= null && dataFinal!= null){
			if(dataInicial.compareTo(dataFinal) > 0)
				throw new RuntimeException(MensagemErroEnum.DATA_INICIAL_MAIOR_Q_A_FINAL.getMessage());
		}
	}
}
