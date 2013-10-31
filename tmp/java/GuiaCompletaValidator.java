package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de guias completas
 * @author Danilo
 */
public class GuiaCompletaValidator<C extends GuiaCompleta> extends AbstractGuiaValidator<GuiaCompleta>{
 
	public boolean templateValidator(GuiaCompleta guia)throws ValidateException{
		Boolean isCIDNulo = guia.getCids().isEmpty() || guia.getCids() == null;
		Boolean isQuadroClinicoNulo = guia.getQuadrosClinicos().isEmpty() || guia.getQuadrosClinicos() == null;
		Boolean isInternacaoUrgencia = guia.isInternacaoUrgencia();
		Boolean isInternacaoEletiva = guia.isInternacaoEletiva();
		
		if(isInternacaoUrgencia || isInternacaoEletiva){
			if(isCIDNulo)
				throw new ValidateException(MensagemErroEnum.GUIA_URGENCIA_SEM_CIDS.getMessage());
			if(isQuadroClinicoNulo && !guia.isAtendimentoUrgencia())
				throw new ValidateException(MensagemErroEnum.GUIA_URGENCIA_SEM_QUADRO_CLINICO.getMessage());
		}
		
		return true;
	}
	
}
