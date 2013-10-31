package br.com.infowaypi.ecarebc.procedimentos.validators;

import static br.com.infowaypi.ecarebc.enums.SexoEnum.AMBOS;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validação de sexo de um procedimento para um segurado no e-Care.
 * <b>Sexo</b> de um procedimento é uma regulação imposta pelo e-Care que restringe a utilização de alguns procedimentos
 * da tabela CBHPM ao sexo do segurado. Os procedimentos podem ter sexo: <b>Masculino</b>/<b>Feminino</b>/<b>Ambos</b>
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class ProcedimentoSexoValidator<P extends ProcedimentoInterface, G extends GuiaSimples> extends AbstractProcedimentoValidator<P, G>{

	public boolean templateValidator(P proc, G guia) throws ValidateException{
		TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
		Boolean isSexoAmbos = (cbhpm.getSexo() == AMBOS.value()) ? true : false;
		
		if (isSexoAmbos) return true;
		
		Boolean isMesmoSexo = guia.getSegurado().getPessoaFisica().getSexo() == proc.getProcedimentoDaTabelaCBHPM().getSexo();
		Assert.isTrue(isMesmoSexo, MensagemErroEnum.SEGURADO_COM_SEXO_INVALIDO_PARA_PROCEDIMENTO.getMessage(cbhpm.getCodigo()));
		return true;
	}
	
}
