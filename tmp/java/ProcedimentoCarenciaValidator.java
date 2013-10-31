package br.com.infowaypi.ecarebc.procedimentos.validators;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para valida��o de car�ncia de um procedimento para um segurado no e-Care.
 * <b>Car�ncia</b> � uma valida��o realizada pelo e-Care que obriga o segurado a esperar um certo per�odo (a partir da
 * sua data de inclus�o no plano) para poder realizar um determinado procedimento. Cada procedimento da tabela CBHPM 
 * possui o seu per�odo de car�ncia.
 * @author Danilo Nogueira Portela
 */
public class ProcedimentoCarenciaValidator<P extends ProcedimentoInterface, G extends GuiaSimples> extends AbstractProcedimentoValidator<P, G>{
 
	public boolean templateValidator(P proc, G guia) throws ValidateException{
		TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
		AbstractSegurado segurado = guia.getSegurado();
		
		if (!segurado.isCumpriuCarencia(proc.getProcedimentoDaTabelaCBHPM().getCarencia())){
			Date dataFimCarencia = Utils.incrementaDias(segurado.getInicioDaCarencia(), proc.getProcedimentoDaTabelaCBHPM().getCarencia());

			throw new ValidateException(MensagemErroEnum.BENEFICIARIO_NAO_CUMPRIU_CARENCIA_PROCEDIMENTO
							.getMessage(cbhpm.getCodigoEDescricao(), Utils.format(dataFimCarencia)));
		}
		
		return true;
	}
	
}
