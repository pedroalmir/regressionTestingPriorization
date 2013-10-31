package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para validação de procedimentos da Tabela CBHPM
 * @author Danilo Nogueira Portela
 */
public class TabelaCBHPMSimplesValidator extends AbstractTabelaCBHPMValidator<TabelaCBHPM>{
 
	public boolean templateValidator(TabelaCBHPM cbhpm) throws ValidateException{
		
//		cbhpm.setValorModerado(cbhpm.getValor());
		
		if(Utils.isStringVazia(cbhpm.getCodigo()))
			throw new ValidateException(MensagemErroEnum.CODIGO_INVALIDO.getMessage());
		
		if(Utils.isStringVazia(cbhpm.getDescricao()))
			throw new ValidateException(MensagemErroEnum.DESCRICAO_INVALIDA.getMessage());
		
		if((cbhpm.getSexo() > 3) || (cbhpm.getSexo() < 1))
			throw new ValidateException(MensagemErroEnum.SEXO_INVALIDO.getMessage());
		
		cbhpm.setCodigoEDescricao(cbhpm.getCodigo()+ " - " + cbhpm.getDescricao());
		
		return true;
	}
	
}
