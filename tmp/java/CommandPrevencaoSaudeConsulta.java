package br.com.infowaypi.ecare.atendimentos.promocaosaude;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.math.NumberRange;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * 
 * @author Josino Rodrigues
 * Command que verifica se deverá ser cobrado coparticipação para uma consulta
 * baseado nas regras de promoção a saude.
 */
public abstract class CommandPrevencaoSaudeConsulta {
	 
	protected BigDecimal coParticipacaoZero = MoneyCalculation.rounded(BigDecimal.ZERO);
	protected String[] situacoes = {SituacaoEnum.CONFIRMADO.descricao(),SituacaoEnum.FATURADA.descricao()};
	
	public abstract boolean geraCoparticipacao(GuiaConsulta segurado);

	protected boolean idadeValida(int idadeSegurado,int idadeInicial,int idadeFinal){
		NumberRange range = new NumberRange(idadeInicial,idadeFinal);
		if (range.containsInteger(idadeSegurado))
			return true;
		return false;
	}

	protected int numeroDeGuiasSemCoparticipacao(List<GuiaConsulta<ProcedimentoInterface>> guias) {
		int count = 0;
		for (GuiaConsulta<ProcedimentoInterface> guiaConsulta : guias) {
			if(guiaConsulta.getValorCoParticipacao().equals(coParticipacaoZero))
				count++;
		}
		return count;
	}
}
