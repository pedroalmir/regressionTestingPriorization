package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;


/**
 * Classe responsável por verificar se uma guia de internação será fechada parcialmente.
 * 
 * @author Eduardo
 *
 */
public class FechamentoParcialInternacaoValidator implements FechamentoGuiaInternacaoValidator {

	@SuppressWarnings("unchecked")
	public void execute(GuiaInternacao guia, Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		parcial = parcial == null? false : parcial;
		System.out.println("");
		validaFechamentoParcialInternacao(guia, parcial, dataFinal, usuario);
	}

	public void validaFechamentoParcialInternacao(GuiaInternacao guia, Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		guia.setFechamentoParcial(parcial);
		boolean isGuiaFechada = guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
		boolean isGuiaParcial = guia.isGuiaParcial();
		if (parcial) {//n vai entrar aqi pq guia ja tem alta (adm) e so pod ser fechada totalmente; a nao ser q se possa gerar1parcial sem fechar a original
			if (!guia.isUltimaGuiaDaInternacao()) {
				throw new ValidateException(MensagemErroEnum.GUIA_NAO_PODE_SER_FECHADA_PARCIALMENTE.getMessage());
			}
		} else {
			if (isGuiaFechada && isGuiaParcial) {
				this.verificaMesmoDia(guia, dataFinal);
			}
		}

	}

	private void verificaMesmoDia(GuiaInternacao guia, Date dataFinal) {
		boolean dataFinalNaoENula = dataFinal != null;//dt final sempre vai ser nula pq ja tem alta e n pod mudar a dt final
		if (dataFinalNaoENula) {
			boolean naoEMesmoDia = !(Utils.getDiferencaEmDias(dataFinal, guia.getDataTerminoAtendimento()) == 0);
			if (naoEMesmoDia)
				throw new RuntimeException(MensagemErroEnum.NAO_E_POSSIVEL_ALTERAR_DATA_FECHAMENTO_DA_GUIA.getMessage());
		}
	}

}
