package br.com.infowaypi.ecarebc.exceptions;

/**
 * Exceção que poderá ocorrer no factory caso não seja encontrada a classe
 * com a referência informada.
 * @author Mário Sérgio Coelho Marroquim
 */
public class ReferenciaNaoEncontradaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReferenciaNaoEncontradaException(String mensagem){
		super(mensagem);
	}
	
}