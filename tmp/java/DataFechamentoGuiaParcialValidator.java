package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class DataFechamentoGuiaParcialValidator implements FechamentoGuiaInternacaoValidator {

	@Override
	public void execute(GuiaInternacao guia, Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException {
	
		validaDataFechamento(guia);
	}

	private void validaDataFechamento(GuiaInternacao guia) throws ValidateException {
		if (guia.isGuiaParcial()) {
			
			if(Utils.compareData(guia.getDataAtendimento(), new Date()) == 0){
				throw new ValidateException(MensagemErroEnum.GUIA_NAO_PODE_SER_FECHADA_NO_DIA_QUE_FOI_GERADA.getMessage());
			}
		}
	}
}
