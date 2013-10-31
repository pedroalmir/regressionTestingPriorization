package br.com.infowaypi.ecare.exceptions;

/**
 * Exceção que poderá ocorrer quando um determinado componente estiver inativo.
 * @author Mário Sérgio Coelho Marroquim
 */
public class AtributoInativoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AtributoInativoException(String mensagem){
		super(mensagem);
	}
	
}