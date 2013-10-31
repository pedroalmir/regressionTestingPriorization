/**
 * 
 */
package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;

/**
 * @author Marcus bOolean
 * Classe que encapsula um conjunto de guias com o
 * limite de exames estourado para seu segurado. 
 */
public class ResumoGuiasForaDoLimite {
	List<GuiaSimples<Procedimento>> guiasForaDoLimite;
	
	
	public ResumoGuiasForaDoLimite(List<GuiaSimples<Procedimento>> guiasForaDoLimite) {
		this.guiasForaDoLimite = guiasForaDoLimite;
	}
	
	public int getNumeroTotalDeGuias() {
		return this.guiasForaDoLimite.size();
	}
	
	public int getNumeroTotalDeExames() {
		int total = 0;
		for (GuiaSimples<Procedimento> guia : this.guiasForaDoLimite) {
			for (Procedimento procedimento : guia.getProcedimentos()) {
				total ++;
			}
		}
		
		return total;
	}
	
	public BigDecimal getValorTotalDasGuias() {
		BigDecimal valorTotal = BigDecimal.ZERO;
		for (GuiaSimples<Procedimento> guia : this.guiasForaDoLimite) {
			valorTotal = valorTotal.add(guia.getValorTotal());
		}
		
		return valorTotal;
	}

	public List<GuiaSimples<Procedimento>> getGuiasForaDoLimite() {
		return guiasForaDoLimite;
	}

	public void setGuiasForaDoLimite(
			List<GuiaSimples<Procedimento>> guiasForaDoLimite) {
		this.guiasForaDoLimite = guiasForaDoLimite;
	}
	
}
