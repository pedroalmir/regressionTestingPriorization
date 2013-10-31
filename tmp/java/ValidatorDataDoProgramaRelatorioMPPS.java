package br.com.infowaypi.ecare.validacao.services;

import java.util.Date;

import br.com.infowaypi.ecare.programaPrevencao.ProgramaDePrevencao;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class ValidatorDataDoProgramaRelatorioMPPS {
	
	public void verificaDataDoProgramaRelatorioMPPS(Date dtInicial, Date dtFinal, String dataFinal, ProgramaDePrevencao programa) throws ValidateException {
		
		boolean acimaDataInicialDoPrograma = Utils.compareData(dtInicial, programa.getInicio()) < 0;
		if (acimaDataInicialDoPrograma) {
			throw new ValidateException("A data de atendimento inicial é anterior a data de início do programa informado.");
		}
		
		if(programa.getInicio()!=null) {
			if (!Utils.isStringVazia(Utils.format(programa.getInicio()))) {
				if (programa.getFim()!=null) {
					if (!Utils.isStringVazia(dataFinal)) {
						boolean acimaDataFinalDoPrograma = Utils.compareData(dtFinal, programa.getFim()) > 0;
						boolean abaixoDataFinalDoPrograma = Utils.compareData(dtFinal, programa.getInicio()) < 0;
						if (acimaDataFinalDoPrograma||abaixoDataFinalDoPrograma){
							throw new ValidateException("A data de atendimento final é posterior a data de encerramento do programa informado.");
						}
					}
				}
			}
		} else {
			throw new ValidateException("A data de inicio não foi informado no cadastro do programa selecionado.");
		}
	}
}
