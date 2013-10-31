package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de guias consulta
 * @author Danilo Nogueira Portela
 * @changes Idelvane
 */
public class GuiaConsultaValidator<C extends GuiaConsulta> extends AbstractGuiaValidator<GuiaConsulta> {
 
	public boolean templateValidator(GuiaConsulta guia)throws ValidateException{
		Boolean isProfissionalNulo = guia.getProfissional() == null;
		if(isProfissionalNulo)
			throw new ValidateException(MensagemErroEnum.PROFISSIONAL_NAO_INFORMADO.getMessage());
		
		Boolean isEspecialidadeNula = guia.getEspecialidade() == null;
		if(isEspecialidadeNula) 
			throw new ValidateException(MensagemErroEnum.ESPECIALIDADE_NAO_INFORMADA.getMessage());
		
		return true;
	}

}
