package br.com.infowaypi.ecare.exceptions;

/**
 * Exce��o que poder� ocorrer quando um determinado objeto, que deveria existir,
 * n�o foi encontradoou est� nulo.
 * @author M�rio S�rgio Coelho Marroquim
 */
public class ObjetoNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ObjetoNaoEncontradoException(String mensagem){
		super(mensagem);
	}
	
}