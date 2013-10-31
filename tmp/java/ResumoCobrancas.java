package br.com.infowaypi.ecare.financeiro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResumoCobrancas {

	private List<Cobranca> cobrancas;
	private String motivo;
	private Date competencia;
	

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public ResumoCobrancas(){
		cobrancas = new ArrayList<Cobranca>();
	}
	
	public List<Cobranca> getCobrancas() {
		return cobrancas;
	}
	
	public void setCobrancas(List<Cobranca> cobrancas) {
		this.cobrancas = cobrancas;
	}
	
	public void addCobranca(Cobranca cobranca) {
		this.cobrancas.add(cobranca);
	}
	
	public Date getCompetencia() {
		return competencia;
	}
	
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
}
