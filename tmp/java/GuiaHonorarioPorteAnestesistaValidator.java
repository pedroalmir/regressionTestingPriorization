package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Collection;

import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class GuiaHonorarioPorteAnestesistaValidator {
	
	public static void execute(GuiaHonorarioMedico guia) throws Exception{
		validate(guia.getProcedimentos(), guia.getGrauDeParticipacao());
	}

	public static boolean validate(Collection<ProcedimentoInterface> procedimentos, Integer grauDeParticipacao) throws Exception {

		boolean isAuxiliarAnestesista 	= grauDeParticipacao == GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA.getCodigo();
		boolean isAnestesistaPrincipal 	= grauDeParticipacao == GrauDeParticipacaoEnum.ANESTESISTA.getCodigo();

		for (ProcedimentoInterface procedimento : procedimentos) {
			
			validaProcedimento(isAuxiliarAnestesista, isAnestesistaPrincipal, procedimento);
		}
		return true;
	}

	/**
	 * @param isAuxiliarAnestesista
	 * @param isAnestesistaPrincipal
	 * @param procedimento
	 * @throws ValidateException
	 */
	public static void validaProcedimento(boolean isAuxiliarAnestesista, boolean isAnestesistaPrincipal, ProcedimentoInterface procedimento)
											throws ValidateException {
		
		TabelaCBHPM procedimentoDaTabelaCBHPM 	= procedimento.getProcedimentoDaTabelaCBHPM();
		Integer porteAnestesico 				= procedimentoDaTabelaCBHPM.getPorteAnestesicoFormatado();
		
		boolean isPermiteAnestesista = porteAnestesico > 0;
		if (!isPermiteAnestesista) {
			if (isAuxiliarAnestesista || isAnestesistaPrincipal) {
				throw new ValidateException(MensagemErroEnum.NAO_GERAR_HONORARIO_PARA_PORTE_ANESTESICO_ZERO.getMessage());
			}
		}
		
		boolean isPermiteAuxiliarAnestesista = porteAnestesico.equals(7) || porteAnestesico.equals(8);
		if (!isPermiteAuxiliarAnestesista && isAuxiliarAnestesista) {
			throw new ValidateException(MensagemErroEnum.GERAR_HONORARIO_ANESTESISTA_AUXILIAR_APENAS_PARA_PORTE_ANESTESICO_SETE_OU_OITO.getMessage());
		}
	}
}