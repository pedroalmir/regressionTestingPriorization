package br.com.infowaypi.ecare.financeiro.ordenador;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;

public class AgrupamentoGuias {

	private Date competencia;
	private Set<GuiaSimples> guias;
	
	public AgrupamentoGuias() {
		guias = new HashSet<GuiaSimples>();
	}
	
	public Date getCompetencia() {
		return competencia;
	}
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	public Set<GuiaSimples> getGuias() {
		return guias;
	}
	public void setGuias(Set<GuiaSimples> guias) {
		this.guias = guias;
	} 
	
	public int getNumeroDeGuias() {
		if(guias != null) {
			return guias.size();
		}else {
			return 0;
		}
	}
	
	public BigDecimal getValorDasGuias() {
		BigDecimal total = BigDecimal.ZERO;
		for (GuiaSimples guia : guias) {
			total = total.add(guia.getValorTotal());
		}
		return total;
	}
}
