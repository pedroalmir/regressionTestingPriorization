package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de Prestador em guias de consulta de urgência
 * @author Danilo Nogueira Portela
 */
public class ConsultaUrgenciaPrestadorValidator extends AbstractGuiaValidator<GuiaConsultaUrgencia> {
 
	public boolean templateValidator(GuiaConsultaUrgencia guia) throws ValidateException {
		Prestador prestador = guia.getPrestador();
		if (prestador != null && !prestador.isFazConsultaUrgencia())
			throw new ValidateException(MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE.
					getMessage("CONSULTAS DE URGÊNCIA"));
		return true;
	}

}
