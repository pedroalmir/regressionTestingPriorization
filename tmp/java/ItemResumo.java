/**
 * 
 */
package br.com.infowaypi.ecare.resumos;

import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;

/**
 * @author Marcus bOolean
 *
 */
 public class ItemResumo {
	private Prestador prestador;
	private TabelaCBHPM procedimento;
	private int numeroDeProcedimentos;
	private BigDecimal valorTotal;
	private BigDecimal valorUnitario;
	
	
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	public int getNumeroDeProcedimentos() {
		return numeroDeProcedimentos;
	}
	public void setNumeroDeProcedimentos(int numeroDeProcedimentos) {
		this.numeroDeProcedimentos = numeroDeProcedimentos;
	}
	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	public TabelaCBHPM getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(TabelaCBHPM procedimento) {
		this.procedimento = procedimento;
	}
	public Prestador getPrestador() {
		return prestador;
	}
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	
	
}
