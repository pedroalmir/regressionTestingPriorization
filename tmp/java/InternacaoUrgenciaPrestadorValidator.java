package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoUrgencia;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para valida��o de Prestador em guias de internacao de urg�ncia
 * @author Danilo Nogueira Portela
 */
public class InternacaoUrgenciaPrestadorValidator extends AbstractGuiaValidator<GuiaInternacaoUrgencia> {
 
	public boolean templateValidator(GuiaInternacaoUrgencia guia) throws ValidateException {
		Prestador prestador = guia.getPrestador();
		if (prestador != null && !prestador.isFazInternacaoUrgencia())
			throw new ValidateException(MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE
					.getMessage("INTERNA��ES DE URG�NCIA"));
		return true;
	}

}
