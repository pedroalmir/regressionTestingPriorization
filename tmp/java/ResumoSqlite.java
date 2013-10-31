package br.com.infowaypi.sensews.client.relatorio;

import java.util.List;

import br.com.infowaypi.sensews.client.tolerancia.sqlite.EventoJSON;

/**
 * Classe que condensa as informações
 * contidas no banco sqlite
 * 
 * @author Jefferson Henrique
 */
public class ResumoSqlite {

	private String local;
	
	private List<EventoJSON> eventos;
	
	public ResumoSqlite() {
		
	}

	public List<EventoJSON> getEventos() {
		return eventos;
	}

	public void setEventos(List<EventoJSON> eventos) {
		this.eventos = eventos;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}
	
}
