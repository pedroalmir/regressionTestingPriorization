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
 * Classe para validação periodicidade de guias consulta de urgência
 * @author Danilo Nogueira Portela
 */
public class GuiaConsultaUrgenciaPeriodicidadeValidator extends AbstractGuiaValidator<GuiaCompleta> {
 
	public boolean templateValidator(GuiaCompleta guia)throws ValidateException{	
//		GuiaSimples ultimaGuia = guia.getSegurado().getUltimaConsultaUrgencia(guia.getPrestador());
		GuiaSimples ultimaGuia = new Service().getUltimaGuia(guia.getPrestador(), guia.getSegurado(), null, GuiaConsultaUrgencia.class);
		if(ultimaGuia != null && !ultimaGuia.equals(guia)){
			Calendar dataGuia = new GregorianCalendar();
			dataGuia.setTime(ultimaGuia.getDataAtendimento());
			Calendar hoje = Calendar.getInstance();
			if(Utils.diferencaEmDias(dataGuia, hoje) <= 15)
				throw new ValidateException(MensagemErroEnum.CONSULTA_URGENCIA_NAO_CUMPRIU_PERIODICIDADE.getMessage(guia.getSegurado().getPessoaFisica().getNome(),Utils.format(ultimaGuia.getDataAtendimento())));
			else
				return true;
		}
		
		return true;
	}
}