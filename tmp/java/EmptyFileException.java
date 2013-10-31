package br.com.infowaypi.ecarebc.exceptions;

@SuppressWarnings("serial")
public class EmptyFileException extends RuntimeException{
	public EmptyFileException(String msg){
		super(msg);
	}
}