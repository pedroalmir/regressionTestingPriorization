package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Calendar;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação de datas em guias simples
 * @author Danilo Nogueira Portela
 * @changes Idelvane Santana, Marcus bOolean
 */
@SuppressWarnings("rawtypes")
public class DataGuiaSimplesValidator<S extends GuiaSimples> extends AbstractGuiaValidator<GuiaSimples>{
 
	public boolean templateValidator(GuiaSimples guia)throws ValidateException{
		Date dataMarcacao = guia.getDataMarcacao();
		Date dataAtendimento = guia.getDataAtendimento();
		
		Calendar dataVencimentoGuia = Calendar.getInstance();
		dataVencimentoGuia.setTime(guia.getDataMarcacao());
		
		Integer incremento = 0;
		
		if (!guia.isConsultaOdonto()){
			incremento = (guia.isSimples()) ? EnumValidadeGuia.VALIDADE_GUIA_SIMPLES.getNumeroDias() : (guia.isCompleta()) ? EnumValidadeGuia.VALIDADE_GUIA_COMPLETA.getNumeroDias() : 0;
		} else{
			incremento = EnumValidadeGuia.VALIDADE_DEMAIS_GUIAS.getNumeroDias();
		}
		
		dataVencimentoGuia.add(Calendar.DAY_OF_MONTH, incremento);
			
		if(dataAtendimento != null) {
			Boolean isPrazoValido = Utils.compareData(dataAtendimento, dataVencimentoGuia.getTime()) <= 0;
			Assert.isTrue(isPrazoValido, MensagemErroEnum.DATA_DE_ATENDIMENTO_COM_PRAZO_ULTRAPASSADO.getMessage());
			
		/*  if[INFORMAR_DATA_NO_ATENDIMENTO_URGENCIA]
			if (!guia.isInternacaoUrgencia() && !guia.isConsultaUrgencia()) {
    	else[INFORMAR_DATA_NO_ATENDIMENTO_URGENCIA]*/
			if (!guia.isInternacaoUrgencia()) {
	    /* end[INFORMAR_DATA_NO_ATENDIMENTO_URGENCIA]*/
				Boolean isDataInvalida = Utils.compareData(dataAtendimento, dataMarcacao) < 0;
				Assert.isFalse(isDataInvalida, MensagemErroEnum.DATA_DE_ATENDIMENTO_INFERIOR_A_MARCACAO.getMessage());
			}
		}
		
		return true;
	}
}