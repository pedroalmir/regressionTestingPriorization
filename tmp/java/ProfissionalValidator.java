package br.com.infowaypi.ecarebc.associados.validators;

import static br.com.infowaypi.msr.utils.Utils.*;

import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação de profissionais assosciados ao sistema.
 * @author Danilo Nogueira Portela
 */
public class ProfissionalValidator extends AbstractValidator<Profissional> {
 
	public boolean templateValidator(Profissional prof) throws ValidateException {
		if(Utils.isStringVazia(prof.getPessoaFisica().getNome()))
			throw new ValidateException(MensagemErroEnum.NOME_INVALIDO.getMessage());

		if(!Utils.isCpfValido(prof.getPessoaFisica().getCpf()))
			throw new ValidateException(MensagemErroEnum.PESSOA_FISICA_COM_CPF_INVALIDO.getMessage());

		String cpf = prof.getPessoaFisica().getCpf();
		if(!(cpf.equals("00000000000") || cpf.equals("11111111111"))){
			if (Utils.isCampoDuplicado(prof, "pessoaFisica.cpf", prof.getPessoaFisica().getCpf()))
				throw new ValidateException(MensagemErroEnum.CAMPO_CPF_JA_CADASTRADO.getMessage());
		}
		
		if (prof.hasSalvoComMesmoConselhoECrm())
			throw new ValidateException(MensagemErroEnum.CAMPO_CRM_JA_CADASTRADO.getMessage());		
		
		return true;
	}

}
