package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaFechavel;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;


/**
 * Validator que verificar se uma guia não estourou o prazo de fechamento, que atualmente é de 
 * 90 dias após o início de atendimento.
 * @author Eduardo
 *
 */
public class FechamentoDataLimiteValidator implements FechamentoGuiaCompletaValidator {

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(GuiaCompleta guia, Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		execute(guia);
	}
	
	public static void execute(GuiaFechavel guia) throws ValidateException {
		Calendar atendimento = Utils.createCalendarFromDate(guia.getDataInicioPrazoRecebimento());
		
		int diferenca = Utils.diferencaEmDias(atendimento, new GregorianCalendar());
		int limiteDiasFechamento = getPrazoMaximofechamento(guia);
		if (diferenca > limiteDiasFechamento){
			throw new ValidateException(MensagemErroEnum.GUIA_NAO_PODE_SER_FECHADA.getMessage(guia.getAutorizacao(), String.valueOf(limiteDiasFechamento)));
		}
	}
	
	private static int getPrazoMaximofechamento(GuiaFechavel guia){
		int resultado = 0;
		if((Utils.compareData(guia.getDataInicioPrazoRecebimento(), PainelDeControle.getPainel().getDataVigenciaPrazoFinalEntregaDeLote()) < 0)){
			resultado = GuiaCompleta.LIMITE_DIAS_FECHAMENTO; 
		} else {
			resultado = PainelDeControle.getPainel().getPrazoFinalParaEntregaDeLote();
		}
		
		return resultado;
	}
}
