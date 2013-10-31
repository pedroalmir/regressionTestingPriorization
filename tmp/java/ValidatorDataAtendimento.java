package br.com.infowaypi.ecare.validacao.services;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class ValidatorDataAtendimento {

	public void validaDataAtendimento(Date dataAtendimento, GuiaCompleta consultaUrgencia) {
		if (dataAtendimento != null) {
		
			Date hoje = new Date();
			
			boolean dataAtendimentoAntesHoje =  Utils.compareData(dataAtendimento, hoje) <= 0;
			Assert.isTrue(dataAtendimentoAntesHoje, "A data de atendimento não deve ser superior a hoje.");
			
			boolean dataAtendimentoAposConsultaUrgencia =  Utils.compareData(dataAtendimento, consultaUrgencia.getDataAtendimento()) >= 0;
			Assert.isTrue(dataAtendimentoAposConsultaUrgencia, "A DATA DE ATENDMENTO não deve ser inferior à data de atendimento da CONSULTA DE URGÊNCIA.");
			
		}
	}
	
}
