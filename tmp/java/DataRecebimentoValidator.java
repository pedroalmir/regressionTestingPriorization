package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecare.resumos.ResumoParametro;
import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.utils.GuiaUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class DataRecebimentoValidator extends AbstractValidator<ResumoParametro> {

	/**
	 * A classe DataRecebimentoValidator verifica uma guia quanto a sua data de recebimento.
	 * Primeiro é verificado se tanto a guia como a entrada do usuário não estão vazias (objetos nulos). 
	 * Caso estejam o validator lança uma exceção para o usuário. Em segundo, o validator checar se há 
	 * uma incoerência entre a data de recebimento e a data de término de atendimento.
	 * Caso exista algo de errado, é lançada uma exceção.
	 * @author jefferson
	 *
	 */
	@Override
	protected boolean templateValidator(ResumoParametro parametro) throws ValidateException {

		boolean situacaoAPartirDeRecebido = GuiaUtils.getSituacoesAPartirRecebido().contains(parametro.getSituacao());
		boolean situacaoAPartirDefechado = GuiaUtils.getSituacoesAPartirConfirmado().contains(parametro.getSituacao());
		if (situacaoAPartirDeRecebido|| situacaoAPartirDefechado){
			boolean dataRecebimentoGuiaNula = parametro.getGuia().getDataRecebimento() == null;
			boolean dataRecebimentoNula  = parametro.getDataRecebimento() == null;
			if (dataRecebimentoGuiaNula) {
				if (dataRecebimentoNula) {
					throw new ValidateException(MensagemErroEnum.DATA_RECEBIMENTO_NAO_PREENCHIDA_PARA_MUDANCA_SITUACAO.getMessage());
				} else {
					GuiaUtils.verificarErroNasDatasDaGuia(parametro.getDataRecebimento(),parametro.getGuia().getDataTerminoAtendimento(),MensagemErroEnum.DATA_DE_RECEBIMENTO_INFERIOR_A_DATA_DE_TERMINO_ATENDIMENTO.getMessage());
					parametro.getGuia().setDataRecebimento(parametro.getDataRecebimento());
					return true;
				}
			}
		}
		
		return false;
	}

}
