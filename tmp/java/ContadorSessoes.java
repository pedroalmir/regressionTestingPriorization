package br.com.infowaypi.ecare.utils;

public class ContadorSessoes {

	static int contador = 0;

	public synchronized void incrementa() {
		contador++;
	}

	public synchronized void decrementa() {
		contador--;
	}

}
