package br.com.infowaypi.ecarebc.associados.validators;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SexoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação de especialidades
 * @author Danilo Nogueira Portela
 */
public class EspecialidadeValidator extends AbstractValidator<Especialidade> {
 
	public boolean templateValidator(Especialidade esp) throws ValidateException {
		if(Utils.isStringVazia(esp.getDescricao()))
			throw new ValidateException(MensagemErroEnum.DESCRICAO_INVALIDA.getMessage());
		
		if (SexoEnum.getEnum(esp.getSexo()) == null)
			throw new ValidateException(MensagemErroEnum.SEXO_INVALIDO.getMessage());
		return true;
	}

}
