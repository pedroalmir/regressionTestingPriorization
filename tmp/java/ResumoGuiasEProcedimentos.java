/**
 * 
 */
package br.com.infowaypi.ecare.resumos;

import java.math.BigDecimal;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;

/**
 * @author Marcus bOolean
 * Resumo responsavel por encapsular uma coleçao de Guias confirmadas e 
 * uma coleçao de procedimentos confirmados.
 *
 */
public class ResumoGuiasEProcedimentos {
	
	private List<GuiaSimples<Procedimento>> guiasConfirmadas;
	private List<Procedimento> procedimentosFechados;
	
	
	public ResumoGuiasEProcedimentos(List<GuiaSimples<Procedimento>> guias, List<Procedimento> procedimentos) {
		this.guiasConfirmadas = guias;
		this.procedimentosFechados = procedimentos;
	}


	public List<GuiaSimples<Procedimento>> getGuiasConfirmadas() {
		return guiasConfirmadas;
	}


	public void setGuiasConfirmadas(List<GuiaSimples<Procedimento>> guiasConfirmadas) {
		this.guiasConfirmadas = guiasConfirmadas;
	}


	public List<Procedimento> getProcedimentosFechados() {
		return procedimentosFechados;
	}


	public void setProcedimentosFechados(List<Procedimento> procedimentosFechados) {
		this.procedimentosFechados = procedimentosFechados;
	}
	
	public int getNumeroDeGuiasConfrimadas() {
		return this.guiasConfirmadas.size();
	}
	
	public int getNumeroDeProcedimentosFechados() {
		return this.procedimentosFechados.size();
	}
	
	public BigDecimal getValorTotalDasGuiasConfirmadas() {
		BigDecimal resultadoTotal = BigDecimal.ZERO;
		for (GuiaSimples<Procedimento> guia : this.guiasConfirmadas) {
			resultadoTotal = resultadoTotal.add(guia.getValorTotal());
		}
		return resultadoTotal;
	}

	public BigDecimal getValorTotalDosProcedimentosFechados() {
		BigDecimal resultadoTotal = BigDecimal.ZERO;
		for (Procedimento procedimento : this.procedimentosFechados) {
			resultadoTotal = resultadoTotal.add(procedimento.getValorAnestesista());
		}
		return resultadoTotal;
	}
}
