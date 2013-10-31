package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
/**
 * Interface respons�vel por indicar um fluxo que possua valida��es espec�ficas a serem adicionadas em uma guia.
 * @author Idelvane
 * @changes Junior
 */
public interface ServiceWithValidator {
	/**
	 * M�todo respons�vel por adicionar valida��es em uma guia. Voc� pode utiliz�-lo de duas formas:
	 * 
	 * - Adicionar as valida��es para serem executadas em conjunto com os outros validates, ou seja, somente chamar o addFlowValidator() da guia;
	 * - Adicionar as valida��es do fluxo e execut�-las somente, ou seja, chama do addFlowValidator() e depois executeFlowValidators().
	 * @param guia
	 */
	public abstract void addValidators(GuiaSimples<?> guia) throws Exception;
	
}
