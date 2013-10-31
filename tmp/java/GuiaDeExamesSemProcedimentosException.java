package br.com.infowaypi.ecare.exceptions;

/**
 * Exce��o que poder� ocorrer no factory caso n�o seja encontrada a classe
 * com a refer�ncia informada.
 * @author M�rio S�rgio Coelho Marroquim
 */
public class GuiaDeExamesSemProcedimentosException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GuiaDeExamesSemProcedimentosException(String mensagem){
		super(mensagem);
	}
	
}