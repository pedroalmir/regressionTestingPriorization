package br.com.infowaypi.ecare.validacao;

import br.com.infowaypi.msr.utils.Utils;

/**
 * Conjunto de validadores b�sicos do sistema.
 * @author M�rio S�rgio Coelho Marroquim
 */
public class Validador {

	/**
	 * Verifica se o CPF � v�lido ou n�o.
	 * @param xCPF
	 * @return boolean
	 */
	public static boolean isCpfValido(String xCPF){
		return Utils.isCpfValido(xCPF);
	}
	
	/**
	 * Verifica de cnpj � v�lido ou n�o.
	 * @param xCGC
	 * @return boolean
	 */
	public static boolean isCnpjValido(String xCGC){
		return Utils.isCnpjValido(xCGC);
	}
}