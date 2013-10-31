package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;

public class ResumoLotes {

	private int numeroDeLotes;
	private BigDecimal valorTotal;
	private BigDecimal valorTotalApresentado;
	private Date competencia;
	private Prestador prestador;
	private List<LoteDeGuias> lotes;

	public ResumoLotes() {
		this.valorTotalApresentado = BigDecimal.ZERO;
		this.valorTotal = BigDecimal.ZERO;
		this.lotes = new ArrayList<LoteDeGuias>();
		this.numeroDeLotes = 0;
	}
	
	public ResumoLotes(Date competencia, Prestador prestador, List<LoteDeGuias> lotes) {
		this();
		this.competencia = competencia;
		this.prestador = prestador;
		this.lotes = lotes;
		atualizar();
	}
	
	private void atualizar() {
		for (LoteDeGuias lote : this.lotes) {
			numeroDeLotes++;
			lote.getPrestador().tocarObjetos();
			
			//atualizar o valor total e valor total apresentado
			lote.atualizaValorTotal();
			lote.atualizaValorTotalApresentado();
			
			this.valorTotal = this.valorTotal.add(lote.getValorTotal());
			this.valorTotalApresentado = this.valorTotalApresentado.add(lote.getValorTotalApresentado());
		}
	}

	public List<LoteDeGuias> getLotes() {
		return lotes;
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public void setLotes(List<LoteDeGuias> lotes) {
		this.lotes = lotes;
	}

	public int getNumeroDeLotes() {
		return numeroDeLotes;
	}

	public void setNumeroDeLotes(int numeroDeLotes) {
		this.numeroDeLotes = numeroDeLotes;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	
	public String getNomePrestador() {
		if (prestador != null)
			return prestador.getPessoaJuridica().getFantasia();
		return "Todos";
	}

	public BigDecimal getValorTotalApresentado() {
		return valorTotalApresentado;
	}

	public void setValorTotalApresentado(BigDecimal valorTotalApresentado) {
		this.valorTotalApresentado = valorTotalApresentado;
	}

}
