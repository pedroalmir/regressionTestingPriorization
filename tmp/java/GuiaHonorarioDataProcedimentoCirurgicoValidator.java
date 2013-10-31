package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Collection;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe respons�vel pela valida��o da gera��o de uma guia honor�rio com base na data do procedimento cir�rgico.
 * S� ser� poss�vel gerar guia de honor�rio m�dico para procedimentos cir�rgicos realizados no m�ximo h� 60 dias
 *  
 * @author Junior
 */
public class GuiaHonorarioDataProcedimentoCirurgicoValidator {

	private static final int PRAZO_PARA_CRIACAO_DE_HONORARIOS = 180;
	
	public static void execute(GuiaSimples<ProcedimentoInterface> guia) throws Exception{
		validate(guia.getProcedimentos());
	}
	
	public static boolean validate(Collection<ProcedimentoInterface> procedimentos) throws Exception {

		for (ProcedimentoInterface procedimento : procedimentos) {
			ProcedimentoCirurgico pc = null;
			
			if (procedimento instanceof ProcedimentoCirurgico) {
				pc = (ProcedimentoCirurgico) procedimento;
				
				validaData(pc);
			}
		}
		
		return true;
	}

	private static void validaData(ProcedimentoCirurgico pc)
			throws ValidateException {
		Date dataRealizacao = pc.getDataRealizacao();
		
		if (dataRealizacao == null){
			throw new ValidateException("O campo Data Realiza��o do procedimento " + pc.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao() + " deve ser preenchido!");
		} else {
			int diferencaEmDias = Utils.getDiferencaEmDias(dataRealizacao, new Date());
			
			if (diferencaEmDias > PRAZO_PARA_CRIACAO_DE_HONORARIOS) {
				throw new ValidateException(MensagemErroEnum.PROCEDIMENTO_NAO_PODE_GERAR_HONORARIO_DATA_DE_REALIZACAO
											.getMessage(pc.getProcedimentoDaTabelaCBHPM().getCodigo(), ""+PRAZO_PARA_CRIACAO_DE_HONORARIOS));
			}
		}
	}
	
	public static boolean validate(ProcedimentoInterface procedimento) throws Exception {
		ProcedimentoCirurgico pc= (ProcedimentoCirurgico) procedimento;
		
		if (pc != null) {
			validaData(pc);
		}
		
		return true;
	}
}
