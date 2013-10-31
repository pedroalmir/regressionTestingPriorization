/**
 * 
 */
package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.associados.Prestador;

/**
 * @author Marcus Vinicius
 * 
 * Representa um teto para um prestador em um faturamento.
 * Limitações: Não é persistido
 */

public class TetoPrestadorFaturamento implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long idTetoPrestadorFaturamento;
	private Prestador prestador;
	private BigDecimal teto;
	
	public TetoPrestadorFaturamento() {
	}
	
	public Long getIdTetoPrestadorFaturamento() {
		return idTetoPrestadorFaturamento;
	}

	public void setIdTetoPrestadorFaturamento(Long idTetoPrestadorFaturamento) {
		this.idTetoPrestadorFaturamento = idTetoPrestadorFaturamento;
	}
	
	public Prestador getPrestador() {
		return prestador;
	}
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	public BigDecimal getTeto() {
		return teto;
	}
	public void setTeto(BigDecimal teto) {
		this.teto = teto;
	}

}
