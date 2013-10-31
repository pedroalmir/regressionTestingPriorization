package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.exceptions.BeneficiarioCanceladoException;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validações primárias de guias simples do sistema.
 * @author Danilo Nogueira Portela
 * @changes Idelvane Santana
 */
@SuppressWarnings("unchecked")
public class GuiaSimplesValidator<S extends GuiaSimples> extends AbstractGuiaValidator<GuiaSimples>{
 
	public boolean templateValidator(GuiaSimples guia)throws ValidateException, BeneficiarioCanceladoException{
		boolean isSeguradoCancelado = guia.getSegurado().isSituacaoAtual(SituacaoEnum.CANCELADO.descricao());
		
		if(isSeguradoCancelado) {
			throw new BeneficiarioCanceladoException();
		}
		
		if (!guia.isCompleta()){
			Boolean isSemProcedimentos = guia.getProcedimentos() == null || guia.getProcedimentos().isEmpty();
			Assert.isFalse(isSemProcedimentos, MensagemErroEnum.GUIA_SEM_PROCEDIMENTOS.getMessage());
		}
		return true;
	}
}