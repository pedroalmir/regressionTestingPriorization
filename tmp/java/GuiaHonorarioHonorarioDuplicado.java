package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Set;

import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class GuiaHonorarioHonorarioDuplicado {
	
	//sss
	public static void validate(Set<ProcedimentoInterface> procedimentos, int grauDeParticipacao) throws ValidateException{
		for (ProcedimentoInterface procedimento : procedimentos) {
			if (procedimento.containsHonorario(grauDeParticipacao))
				throw new ValidateException(MensagemErroEnum.HONORARIO_DUPLICADO.getMessage(GrauDeParticipacaoEnum.getEnum(grauDeParticipacao).getDescricao(), procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
		}
	}
}
