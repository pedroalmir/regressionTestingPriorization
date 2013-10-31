package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validação básica de profissionais nas guias
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class BasicProfissionalValidator extends AbstractGuiaValidator<GuiaSimples>{
 
	public boolean templateValidator(GuiaSimples guia) throws ValidateException {
		Boolean isProfissionalInativo = guia.getProfissional() != null && !guia.getProfissional().isSituacaoAtual(SituacaoEnum.ATIVO.descricao());
		
		Assert.isFalse(isProfissionalInativo, MensagemErroEnum.PROFISSIONAL_INATIVO_NO_SISTEMA.getMessage());
		return true;
	}

}
