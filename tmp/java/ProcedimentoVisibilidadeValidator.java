package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.Visibilidade;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para valida��o de visibilidade de um procedimento para um segurado no e-Care.
 * <b>Visibilidade</b> de um procedimento � uma regula��o imposta pelo e-Care que restrige a marca��o de um procedimento
 * apenas pelo m�dulo do sistema a que pertence. Os procedimentos podem ter visibilidade: <b>M�dico</b> / <b>Ambos</b> / <b>Odontol�gico</b>
 * @author Danilo Nogueira Portela
 */
public class ProcedimentoVisibilidadeValidator<P extends ProcedimentoInterface, G extends GuiaSimples> extends AbstractProcedimentoValidator<P, G>{

	public boolean templateValidator(P proc, G guia) throws ValidateException{
		Boolean isOdontologico = guia.isConsultaOdonto() || guia.isExameOdonto();
		
		Assert.isNotNull(proc.getProcedimentoDaTabelaCBHPM().getVisibilidade(),
				MensagemErroEnum.PROCEDIMENTO_VISIBILIDADE_INVALIDA.getMessage(proc.getProcedimentoDaTabelaCBHPM().getCodigo()));
		
		if(isOdontologico)
			Assert.isNotEquals(proc.getProcedimentoDaTabelaCBHPM().getVisibilidade(), Visibilidade.MEDICO.getValor(), 
				 MensagemErroEnum.PROCEDIMENTO_SOLICITADO_OUTRO_MODULO.getMessage(proc.getProcedimentoDaTabelaCBHPM().getCodigo(), "EXAMES"));
		else
			Assert.isNotEquals(proc.getProcedimentoDaTabelaCBHPM().getVisibilidade(), Visibilidade.ODONTOLOGICO.getValor(), 
				MensagemErroEnum.PROCEDIMENTO_SOLICITADO_OUTRO_MODULO.getMessage(proc.getProcedimentoDaTabelaCBHPM().getCodigo(), "ODONTOL�GICO"));
		
		return true;
	}
	
}
