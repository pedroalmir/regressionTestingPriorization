package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de guias internacao para cirurgia
 * @author Danilo Nogueira Portela
 */
public class GuiaInternacaoEletivaValidator extends AbstractGuiaValidator<GuiaInternacaoEletiva> {
 
	public boolean templateValidator(GuiaInternacaoEletiva guia)throws ValidateException{
//		Boolean isSolicitanteNulo = guia.getProfissional() == null;
		Boolean isPrestadorNulo = guia.getPrestador() == null;
		Boolean isEspecialidadeNula = guia.getEspecialidade() == null;
		
//		if(isSolicitanteNulo)
//			throw new ValidateException(MensagemErroEnum.SOLICITANTE_NAO_INFORMADO.getMessage());
		
		/* if_not[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
		if(isPrestadorNulo)
			throw new ValidateException(MensagemErroEnum.PRESTADOR_NAO_INFORMADO.getMessage());
		/* end[REMOVE_OBRIGATORIEDADE_DE_INFORMAR_PRESTADOR_PARA_INTERNACAO] */
		
		if(isEspecialidadeNula)
			throw new ValidateException(MensagemErroEnum.ESPECIALIDADE_NAO_INFORMADA.getMessage());
		
		return true;
	}

}
