package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaAtendimentoUrgencia;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de Prestador em guias de atendimento de urgência
 * @author Danilo Nogueira Portela
 */
public class AtendimentoUrgenciaPrestadorValidator extends AbstractGuiaValidator<GuiaAtendimentoUrgencia> {
 
	public boolean templateValidator(GuiaAtendimentoUrgencia guia) throws ValidateException {
		Prestador prestador = guia.getPrestador();

		if (prestador != null && !prestador.isFazAtendimentoUrgencia())
			throw new ValidateException(MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE.
					getMessage("ATENDIMENTOS DE URGÊNCIA"));
		return true;
	}

}
