package br.com.infowaypi.ecare.exceptions;

/**
 * Exceção que poderá ocorrer no factory caso não seja encontrada a classe
 * com a referência informada.
 * @author Mário Sérgio Coelho Marroquim
 */
public class ProcedimentoInvalidoParaOSeguradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProcedimentoInvalidoParaOSeguradoException(String mensagem){
		super(mensagem);
	}
	
}