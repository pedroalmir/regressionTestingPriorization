package br.com.infowaypi.ecarebc.atendimentos.validators;
/**
 * Interface utilizada para marcar entidades critic�veis.
 * @author wislanildo
 *
 */
public interface Criticavel {
	
	public boolean autorizado();
	
	public boolean solicitado();
}
