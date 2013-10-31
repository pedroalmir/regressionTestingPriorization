package br.com.infowaypi.ecarebc.associados.validators;

import static br.com.infowaypi.msr.utils.Utils.*;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação de prestadores
 * @author Danilo Nogueira Portela
 */
public class PrestadorValidator extends AbstractValidator<Prestador> {
 
	public boolean templateValidator(Prestador prest) throws ValidateException {
		if(Utils.isStringVazia(prest.getPessoaJuridica().getFantasia()))
			throw new ValidateException(MensagemErroEnum.NOME_FANTASIA_INVALIDO.getMessage());
		
		if(Utils.isStringVazia(prest.getPessoaJuridica().getCnpj()))
			throw new ValidateException(MensagemErroEnum.PESSOA_JURIDICA_COM_CNPJ_INVALIDO.getMessage());
		
		boolean isPessoaFisica = (prest.getPessoaJuridica().getCnpj().length() == 11);
		boolean isPessoaJuridica = (prest.getPessoaJuridica().getCnpj().length() == 14);
		if(isPessoaFisica){
			if(!Utils.isCpfValido(prest.getPessoaJuridica().getCnpj()))
				throw new ValidateException(MensagemErroEnum.PESSOA_FISICA_COM_CPF_INVALIDO.getMessage());
		}
		else if(isPessoaJuridica) {
			if(!Utils.isCnpjValido(prest.getPessoaJuridica().getCnpj()))
				throw new ValidateException(MensagemErroEnum.PESSOA_JURIDICA_COM_CNPJ_INVALIDO.getMessage());
		}
		else 
			throw new ValidateException(MensagemErroEnum.DOCUMENTO_INVALIDO.getMessage());

		prest.setPessoaFisica(isPessoaFisica);
		
		float tetoConsulta = prest.getTetoFinanceiroParaConsultas().floatValue();
		float tetoExame = prest.getTetoFinanceiroParaExames().floatValue();
		float tetoInternacao = prest.getTetoFinanceiroParaInternacoes().floatValue();
		
		if((tetoConsulta < 1f) && (tetoExame < 1f) && (tetoInternacao < 1f))
			throw new ValidateException(MensagemErroEnum.PRESTADOR_COM_TETOS_FINANCEIROS_INVALIDOS.getMessage());

		
		for (Profissional prof : prest.getProfissionais()) {
			Boolean isProfissionalValido = false;
			forEsp : for (Especialidade esp : prof.getEspecialidades()) {
				if(prest.getEspecialidades().contains(esp)){
					isProfissionalValido = true;
					break forEsp;
				}
			}
			if(!isProfissionalValido)
				throw new ValidateException(MensagemErroEnum.PROFISSIONAL_NAO_POSSUI_UMA_ESPECIALIDADE_DO_PRESTADOR.
						getMessage(prof.getPessoaFisica().getNome()));
		}
		
		if(Utils.isCampoDuplicado(prest, "usuario.login", prest.getUsuario().getLogin())){
			throw new ValidateException(MensagemErroEnum.PRESTADOR_COM_LOGIN_INVALIDO.getMessage());
		}
		
		prest.atualizaUsuario();
		
		prest.alteraSenha();
		
		return true;
	}
}
