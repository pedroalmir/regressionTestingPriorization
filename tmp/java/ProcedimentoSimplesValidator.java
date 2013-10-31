package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validação básicas de procedimentos simples de uma guia
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class ProcedimentoSimplesValidator<P extends ProcedimentoInterface, G extends GuiaSimples> extends AbstractProcedimentoValidator<P, G>{
 
	public boolean templateValidator(P proc, G guia) throws ValidateException{
		TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
		Assert.isTrue(cbhpm.isSituacaoAtual(SituacaoEnum.ATIVO.descricao()), MensagemErroEnum.PROCEDIMENTO_INATIVO_NO_SISTEMA.getMessage(cbhpm.getCodigo()));
		return true;
	}
	
}
