package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação de guias consulta de urgência
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("rawtypes")
public class GuiaConsultaUrgenciaValidator extends AbstractGuiaValidator<GuiaCompleta> {
 
	public boolean templateValidator(GuiaCompleta guia)throws ValidateException{	
		GuiaSimples ultimaGuia = new Service().getUltimaGuia(guia.getPrestador(), guia.getSegurado(), guia.getEspecialidade(), GuiaConsultaUrgencia.class);
		
		if(ultimaGuia != null && ultimaGuia != guia){
			Calendar dataGuia = new GregorianCalendar();
			dataGuia.setTime(ultimaGuia.getDataAtendimento());
			if ((Utils.diferencaEmDias(dataGuia,new GregorianCalendar()) <= guia.getPrestador().getPeriodoParaVoltaNaUrgencia()) && (guia.getEspecialidade().equals(ultimaGuia.getEspecialidade()))) {
				Calendar diaAtual = Calendar.getInstance();
				diaAtual.setTime(ultimaGuia.getDataAtendimento());
				Date dataProximaConsulta = Utils.incrementaDias(diaAtual, guia.getPrestador().getPeriodoParaVoltaNaUrgencia());
				throw new ValidateException(MensagemErroEnum.CONSULTA_URGENCIA_NAO_CUMPRIU_PERIODICIDADE.getMessage(guia.getSegurado().getPessoaFisica().getNome(),Utils.format(ultimaGuia.getDataAtendimento()), Utils.format(dataProximaConsulta)));
			} else {
				return true;
			}
		}
		
		return true;
	}
}
