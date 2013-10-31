package br.com.infowaypi.ecare.utils;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SRServletListener implements HttpSessionListener {
	
	static final ContadorSessoes contador = new ContadorSessoes();
	
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
		contador.incrementa();
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) {
		contador.decrementa();
	}

}
