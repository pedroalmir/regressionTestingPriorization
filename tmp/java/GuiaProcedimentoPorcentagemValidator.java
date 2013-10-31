package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.utils.CollectionUtils;
import br.com.infowaypi.msr.utils.Utils;

public class GuiaProcedimentoPorcentagemValidator {

	/**
	 * Verifica se nos procedimentos passados como parâmetro existe mais de 1 procedimento a 100% na mesma data.
	 * É necessário informar se já existe algum procedimento a 100% na guia. Para os casos em que só parte dos
	 * procedimentos da guia são passados como parâmetro. 
	 * Apesar de aceitar ProcedimentoInterface, só considera os procedimentos cirurgicos, independente da situacao
	 * 
	 * @param procedimentosAutorizados
	 * @param isGuiaPossuiProcedimentoAutorizadoA100Porcento
	 * @throws Exception
	 */
	public static void verificaPercentualDeReducao(
			Set<ProcedimentoInterface> procedimentosAutorizados, 
			boolean isGuiaPossuiProcedimentoAutorizadoA100Porcento) throws Exception {
		int quantidadeProcedimentosAutorizadosPorcentagem100 = 0;
		Map<Date, Set<ProcedimentoInterface>> procedimentosAgrupadosPorData = CollectionUtils.groupBy(procedimentosAutorizados, "dataRealizacao", Date.class);
			
		for (Entry<Date, Set<ProcedimentoInterface>> entry : procedimentosAgrupadosPorData.entrySet()) {
			Set<ProcedimentoInterface> procedimentos = entry.getValue();
			quantidadeProcedimentosAutorizadosPorcentagem100 += validarPorcentagem(procedimentos);
		}
		
		if (!procedimentosAutorizados.isEmpty() 
				&& quantidadeProcedimentosAutorizadosPorcentagem100 == 0 
				&& !isGuiaPossuiProcedimentoAutorizadoA100Porcento) {
			throw new RuntimeException(MensagemErroEnum.NENHUM_PROCEDIMENTO_A_100_PORCENTO_NA_GUIA.getMessage());
		}
	}

	private static int validarPorcentagem(Set<ProcedimentoInterface> procedimentos) {
		boolean porcentagem100;
		boolean passouPelaAutorizacao;
		boolean isProcedimentosCirurgico;
		int quantidadeProcedimentosAutorizadosPorcentagem100 = 0;
		for (ProcedimentoInterface procedimento : procedimentos) {
			porcentagem100 = procedimento.getPorcentagem().compareTo(Constantes.PORCENTAGEM_100) == 0;
			passouPelaAutorizacao = procedimento.isPassouPelaAutorizacao();
			isProcedimentosCirurgico = (procedimento.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO);
			
			if (passouPelaAutorizacao && porcentagem100) {
				quantidadeProcedimentosAutorizadosPorcentagem100 ++;
				if (isProcedimentosCirurgico && quantidadeProcedimentosAutorizadosPorcentagem100 > 1) {
					throw new RuntimeException(
							MensagemErroEnum.MAIS_DE_UM_PROCEDIMENTO_A_100_PORCENTO_NA_MESMA_DATA
									.getMessage(Utils.format(((ProcedimentoCirurgico)procedimento).getDataRealizacao())));
				}
			}
		}
		return quantidadeProcedimentosAutorizadosPorcentagem100;
	}


}
