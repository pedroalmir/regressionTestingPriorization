package br.com.infowaypi.ecarebc.exceptions;

/**
 * Exce��o que poder� ocorrer no factory caso n�o seja encontrada a classe
 * com a refer�ncia informada.
 * @author M�rio S�rgio Coelho Marroquim
 */
public class ReferenciaNaoEncontradaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ReferenciaNaoEncontradaException(String mensagem){
		super(mensagem);
	}
	
}