package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Collection;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecarebc.enums.CapituloProcedimentoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class ProcedimentoAptoASolicitacaoDeAcompanhamentoAnestesicoValidator {
	public static void validate(Collection<Procedimento> procedimentos) throws ValidateException{
		boolean temProcedimentoAptoASolicitarAcompanhamento = false;
		
		for (Procedimento procedimento : procedimentos) {
			String codigo = procedimento.getProcedimentoDaTabelaCBHPM().getCodigo();
			
			if (CapituloProcedimentoEnum.isAptoASolicitacaoDeAcompanhamentoAnesteciso(codigo)){
				temProcedimentoAptoASolicitarAcompanhamento = true;
				break;
			}
		}
		
		if (!temProcedimentoAptoASolicitarAcompanhamento)
			throw new ValidateException(MensagemErroEnumSR.NENHUM_PROCEDIMENTO_APTO_A_SOLICITACAO_DE_ACOMPANHAMENTO_ANESTESICO.getMessage());
	}
}
