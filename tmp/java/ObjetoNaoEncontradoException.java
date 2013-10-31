package br.com.infowaypi.ecare.exceptions;

/**
 * Exceção que poderá ocorrer quando um determinado objeto, que deveria existir,
 * não foi encontradoou está nulo.
 * @author Mário Sérgio Coelho Marroquim
 */
public class ObjetoNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ObjetoNaoEncontradoException(String mensagem){
		super(mensagem);
	}
	
}