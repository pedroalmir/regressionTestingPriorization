package br.com.infowaypi.ecare.validacao;

import br.com.infowaypi.msr.utils.Utils;

/**
 * Conjunto de validadores básicos do sistema.
 * @author Mário Sérgio Coelho Marroquim
 */
public class Validador {

	/**
	 * Verifica se o CPF é válido ou não.
	 * @param xCPF
	 * @return boolean
	 */
	public static boolean isCpfValido(String xCPF){
		return Utils.isCpfValido(xCPF);
	}
	
	/**
	 * Verifica de cnpj é válido ou não.
	 * @param xCGC
	 * @return boolean
	 */
	public static boolean isCnpjValido(String xCGC){
		return Utils.isCnpjValido(xCGC);
	}
}