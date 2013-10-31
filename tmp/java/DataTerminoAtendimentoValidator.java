package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecare.resumos.ResumoParametro;
import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.utils.GuiaUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;
/**
 * A classe DataTerminoAtendimentoValidator verifica uma guia quanto a sua data de término de atendimento.
 * Primeiro é verificado se tanto a guia como a entrada do usuário não estão vazias (objetos nulos).
 * Caso estejam o validator  lança uma exceção para o usuário. Em segundo, o validator checar se há 
 * uma incoerência entre a data de termino e a data de atendimento.
 * Caso exista algo de errado, é lançada uma exeção.
 * @author jefferson
 *
 */

public class DataTerminoAtendimentoValidator  extends AbstractValidator<ResumoParametro>{

	@Override
	protected boolean templateValidator(ResumoParametro parametro) throws ValidateException {

		boolean situacaoAPartirDeFechado = GuiaUtils.getSituacoesAPartirFechado().contains(parametro.getSituacao());
		boolean situacaoAPartirDeConfirmado = GuiaUtils.getSituacoesAPartirConfirmado().contains(parametro.getSituacao());
		
		if (situacaoAPartirDeFechado ||situacaoAPartirDeConfirmado){
			boolean dataTerminoAtendimentoGuiaNula = parametro.getGuia().getDataTerminoAtendimento() == null;
			boolean dataTerminoAtendimentoNula =parametro.getDataTerminoAtendimento() == null;
			if (dataTerminoAtendimentoGuiaNula){
				if (dataTerminoAtendimentoNula) {
					throw new ValidateException(MensagemErroEnum.DATA_TERMINO_ATENDIMENTO_NAO_PREENCHIDA_PARA_MUDANCA_SITUACAO.getMessage());
				}else{
					GuiaUtils.verificarErroNasDatasDaGuia(parametro.getDataTerminoAtendimento(),parametro.getGuia().getDataAtendimento(),MensagemErroEnum.DATA_DE_TERMINO_ATENDIMENTO_INFERIOR_A_DATA_ATENDIMENTO.getMessage());
					parametro.getGuia().setDataTerminoAtendimento(parametro.getDataTerminoAtendimento());
					return true;
				}
			}
		}
		return false;
	}


}
