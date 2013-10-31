package br.com.infowaypi.ecarebc.atendimentos.validators;


import static br.com.infowaypi.ecarebc.enums.MensagemErroEnum.ATENDIMENTO_URGENCIA_SEM_CONSULTA_ORIGEM;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.Situacao;

/**
 * Classe para validação de guias atendimento de urgência
 * @author Danilo Nogueira Portela
 */
public class GuiaAtendimentoUrgenciaValidator extends AbstractGuiaValidator<GuiaAtendimentoUrgencia> {
 
	@SuppressWarnings("unchecked")
	public boolean templateValidator(GuiaAtendimentoUrgencia guia)throws ValidateException{
//		GuiaSimples guiaOrigem = guia.getSegurado().getUltimaConsultaUrgencia(guia.getPrestador());
		GuiaSimples guiaOrigem = new Service().getUltimaGuia(guia.getPrestador(), guia.getSegurado(), guia.getEspecialidade(), GuiaConsultaUrgencia.class);
		
		if(guiaOrigem != null){
			guiaOrigem.addGuiaFilha(guia);
		}else
			throw new ValidateException(ATENDIMENTO_URGENCIA_SEM_CONSULTA_ORIGEM.getMessage());
		
		
		return true;
	}
	
	
}
