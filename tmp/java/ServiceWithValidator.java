package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
/**
 * Interface responsável por indicar um fluxo que possua validações específicas a serem adicionadas em uma guia.
 * @author Idelvane
 * @changes Junior
 */
public interface ServiceWithValidator {
	/**
	 * Método responsável por adicionar validações em uma guia. Você pode utilizá-lo de duas formas:
	 * 
	 * - Adicionar as validações para serem executadas em conjunto com os outros validates, ou seja, somente chamar o addFlowValidator() da guia;
	 * - Adicionar as validações do fluxo e executá-las somente, ou seja, chama do addFlowValidator() e depois executeFlowValidators().
	 * @param guia
	 */
	public abstract void addValidators(GuiaSimples<?> guia) throws Exception;
	
}
