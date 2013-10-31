package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validação básica de prestadores nas guias 
 * @author root
 * @changes Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class BasicPrestadorValidator extends AbstractGuiaValidator<GuiaSimples>{
 
	public boolean templateValidator(GuiaSimples guia) throws ValidateException {
		if(guia.getPrestador() == null )
			return true;
		
		boolean isEspecialidadeNula = guia.getEspecialidade() == null;
		boolean prestadorContemEspecialidadeDaGuia = guia.getPrestador().getEspecialidades().contains(guia.getEspecialidade());
		
		if(!isEspecialidadeNula && !prestadorContemEspecialidadeDaGuia){
			throw new ValidateException(MensagemErroEnum.PRESTADOR_NAO_ATENDE_ESPECIALIDADE.
					getMessage(guia.getEspecialidade().getDescricao()));
		}
		
		Boolean isPrestadorAtivo = guia.getPrestador().isSituacaoAtual(SituacaoEnum.ATIVO.descricao());
		Assert.isTrue(isPrestadorAtivo, MensagemErroEnum.PRESTADOR_INATIVO_NO_SISTEMA.getMessage());
		return true;
	}

}
