package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de Prestador em guias de internacao eletivas
 * @author Danilo Nogueira Portela
 */
public class InternacaoEletivaPrestadorValidator extends AbstractGuiaValidator<GuiaInternacaoEletiva> {
 
	public boolean templateValidator(GuiaInternacaoEletiva guia) throws ValidateException {
		Prestador prestador = guia.getPrestador();
		if (prestador != null && !prestador.isFazInternacaoEletiva())
			throw new ValidateException(MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE
					.getMessage("INTERNAÇÕES ELETIVAS"));
		return true;
	}

}
