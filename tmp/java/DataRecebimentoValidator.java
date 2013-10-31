package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecare.resumos.ResumoParametro;
import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.utils.GuiaUtils;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class DataRecebimentoValidator extends AbstractValidator<ResumoParametro> {

	/**
	 * A classe DataRecebimentoValidator verifica uma guia quanto a sua data de recebimento.
	 * Primeiro � verificado se tanto a guia como a entrada do usu�rio n�o est�o vazias (objetos nulos). 
	 * Caso estejam o validator lan�a uma exce��o para o usu�rio. Em segundo, o validator checar se h� 
	 * uma incoer�ncia entre a data de recebimento e a data de t�rmino de atendimento.
	 * Caso exista algo de errado, � lan�ada uma exce��o.
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
