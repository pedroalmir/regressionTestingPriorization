package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author SR Team - Marcos Roberto 13.08.2012
 * Faz a validação de intervalo do limite de idade permitido para utilização de atendimento por especialidade 
 * em guias simples. Quando não houver valor informado, ou seja estiver nulo, esta restrição não será executada.
 * Ex: até 14 anos para especialidade (Pediatria)
 *     acima de 60 anos (Geriatria)
 */

@SuppressWarnings("rawtypes")
public class EspecialidadeLimiteIdadeValidator extends AbstractGuiaValidator<GuiaSimples>{
 
	public boolean templateValidator(GuiaSimples guia)throws ValidateException{
		Especialidade especialidade = guia.getEspecialidade();

		if(especialidade!=null) {			
			AbstractSegurado segurado = guia.getSegurado();
			if(especialidade.getIdadeLimiteInicio()!=null || especialidade.getIdadeLimiteFim()!=null) {
				Integer idadeLimiteInicio = especialidade.getIdadeLimiteInicio();
				Integer idadeLimiteFim = especialidade.getIdadeLimiteFim();
				Integer idadeSegurado = Utils.getIdade(segurado.getPessoaFisica().getDataNascimento());
				
				boolean isIdadeSeguradoMaiorIgualIdadeLimite = idadeSegurado>=idadeLimiteInicio;
				boolean isIdadeSeguradoMenorIgualIdadeLimite = idadeSegurado<=idadeLimiteFim;
				
				if(!(isIdadeSeguradoMaiorIgualIdadeLimite && isIdadeSeguradoMenorIgualIdadeLimite)){
					throw new RuntimeException(MensagemErroEnum.LIMITE_IDADE_NAO_PERMITIDO_ESPECIALIDADE.getMessage(
							idadeSegurado.toString(), idadeLimiteInicio.toString(), idadeLimiteFim.toString()));
				}
			}	
		}
		return true;
	}
}