package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validação de idade mínima e idade máxima de um procedimento para um segurado no e-Care.
 * <b>Idade</b> é uma regulação do e-Care que regula se um procedimento pode ou não ser realizado por um segurado
 * verificando sua idade. Se o segurado tiver uma idade menor que a idade mínima ou maior que a idade máxima do 
 * procedimento ele não poderá realizá-lo.
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class ProcedimentoIdadeValidator<P extends ProcedimentoInterface, G extends GuiaSimples> extends AbstractProcedimentoValidator<P, G>{
 
	public boolean templateValidator(P proc, G guia) throws ValidateException{
		TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
		Integer idadeDoSegurado = guia.getSegurado().getPessoaFisica().getIdade();
		Integer idadeMinimaDoProcedimento = cbhpm.getIdadeMinima();
		Integer idadeMaximaDoProcedimento = cbhpm.getIdadeMaxima();
		String descricaoIdade;
		
		if(idadeMinimaDoProcedimento != null){
			descricaoIdade = idadeMinimaDoProcedimento.toString();
			Assert.isTrue(idadeDoSegurado >= idadeMinimaDoProcedimento, MensagemErroEnum.SEGURADO_COM_IDADE_INFERIOR_A_MINIMA_PARA_O_PROCEDIMENTO.
					getMessage(cbhpm.getCodigo(),descricaoIdade));
		}
		
		if(idadeMaximaDoProcedimento != null){
			descricaoIdade = idadeMaximaDoProcedimento.toString();
			Assert.isTrue(idadeDoSegurado <= idadeMaximaDoProcedimento, MensagemErroEnum.SEGURADO_COM_IDADE_SUPERIOR_A_MAXIMA_PARA_O_PROCEDIMENTO.
					getMessage(cbhpm.getCodigo(),descricaoIdade));
		}
		return true;
	}
}
