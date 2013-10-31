package br.com.infowaypi.ecare.exceptions;

/**
 * Exce��o que poder� ocorrer quando um determinado componente estiver inativo.
 * @author M�rio S�rgio Coelho Marroquim
 */
public class AtributoInativoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AtributoInativoException(String mensagem){
		super(mensagem);
	}
	
}