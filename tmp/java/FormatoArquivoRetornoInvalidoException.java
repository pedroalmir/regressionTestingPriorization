package br.com.infowaypi.ecare.exceptions;

@SuppressWarnings("serial")
public class FormatoArquivoRetornoInvalidoException extends RuntimeException{
	public FormatoArquivoRetornoInvalidoException(String msg){
		super(msg);
	}
}