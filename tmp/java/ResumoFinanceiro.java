/**
 * 
 */
package br.com.infowaypi.ecare.financeiro.faturamento;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador.Ordenador;

/**
 * Classe que representa um Resumo de valores resultantes do processo de geraçao do Faturamento.
 * @author <a href="mailto:mquixaba@gmail.com">Marcus Quixabeira</a>
 * @since 2010-07-29 14:30
 *
 */
public class ResumoFinanceiro {
	
	private Ordenador ordenador;
	private ResumoFaturamentos resumoNormal;
	private ResumoFaturamentos resumoPassivo;
	private Set<AbstractFaturamento> faturamentos;
	private Set<Detalhe> detalhes;
	private BigDecimal valorTotalNormal;
	private BigDecimal valorTotalPassivo;
	
	public ResumoFinanceiro(Ordenador ordenador, ResumoFaturamentos resumoNormal, ResumoFaturamentos resumoPassivo) {
		this.ordenador = ordenador;
		this.resumoNormal = resumoNormal;
		this.resumoPassivo = resumoPassivo;
		this.faturamentos = new HashSet<AbstractFaturamento>();
		this.detalhes = new HashSet<Detalhe>();
		this.valorTotalNormal = BigDecimal.ZERO;
		this.valorTotalPassivo = BigDecimal.ZERO;
		
		processar();
	}
	
	public void processar() {
		this.faturamentos.addAll(resumoNormal.getFaturamentos());
		this.faturamentos.addAll(resumoPassivo.getFaturamentos());
		
		alimentaDetalhes();
	}

	private void alimentaDetalhes() {
		Map<Prestador, Detalhe> map = new HashMap<Prestador, Detalhe>();
		
		for (AbstractFaturamento faturamento : this.faturamentos) {
			faturamento.tocarObjetos();
			faturamento.setOrdenador(ordenador);
			
			if(!map.keySet().contains(faturamento.getPrestador())) {
				Detalhe detalhe = new Detalhe();
				detalhe.setPrestador(faturamento.getPrestador());
				if(faturamento.isFaturamentoNormal()) {
					detalhe.setValorNormal(faturamento.getValorBruto());
					this.valorTotalNormal = this.valorTotalNormal.add(faturamento.getValorBruto());
				}else {
					detalhe.setValorPassivo(faturamento.getValorBruto());
					this.valorTotalPassivo = this.valorTotalPassivo.add(faturamento.getValorBruto());
				}
				
				map.put(faturamento.getPrestador(), detalhe);
			}else {
				if(faturamento.isFaturamentoNormal()) {
					map.get(faturamento.getPrestador()).setValorNormal(faturamento.getValorBruto());
					this.valorTotalNormal = this.valorTotalNormal.add(faturamento.getValorBruto());
				}else {
					map.get(faturamento.getPrestador()).setValorPassivo(faturamento.getValorBruto());
					this.valorTotalPassivo = this.valorTotalPassivo.add(faturamento.getValorBruto());
				}
			}
		}
		
		for (Prestador prestador : map.keySet()) {
			this.detalhes.add(map.get(prestador));
		}
	}
	
	public Ordenador getOrdenador() {
		return ordenador;
	}
	public void setOrdenador(Ordenador ordenador) {
		this.ordenador = ordenador;
	}
	public ResumoFaturamentos getResumoNormal() {
		return resumoNormal;
	}
	public void setResumoNormal(ResumoFaturamentos resumoNormal) {
		this.resumoNormal = resumoNormal;
	}
	public ResumoFaturamentos getResumoPassivo() {
		return resumoPassivo;
	}
	public void setResumoPassivo(ResumoFaturamentos resumoPassivo) {
		this.resumoPassivo = resumoPassivo;
	}

	public Set<AbstractFaturamento> getFaturamentos() {
		return faturamentos;
	}


	public void setFaturamentos(Set<AbstractFaturamento> faturamentos) {
		this.faturamentos = faturamentos;
	}

	public Set<Detalhe> getDetalhes() {
		return detalhes;
	}


	public void setDetalhes(Set<Detalhe> detalhes) {
		this.detalhes = detalhes;
	}


	public BigDecimal getValorTotalNormal() {
		return valorTotalNormal;
	}


	public void setValorTotalNormal(BigDecimal valorTotalNormal) {
		this.valorTotalNormal = valorTotalNormal;
	}


	public BigDecimal getValorTotalPassivo() {
		return valorTotalPassivo;
	}


	public void setValorTotalPassivo(BigDecimal valorTotalPassivo) {
		this.valorTotalPassivo = valorTotalPassivo;
	}
	
	public BigDecimal getValorTotal() {
		return this.getValorTotalNormal().add(this.getValorTotalPassivo());
	}
	
	public class Detalhe {
		Prestador prestador;
		BigDecimal valorNormal;
		BigDecimal valorPassivo;
		
		public Detalhe() {
			this.valorNormal = BigDecimal.ZERO;
			this.valorPassivo = BigDecimal.ZERO;
		}
		
		public Prestador getPrestador() {
			return prestador;
		}
		public void setPrestador(Prestador prestador) {
			this.prestador = prestador;
		}
		public BigDecimal getValorNormal() {
			return valorNormal;
		}
		public void setValorNormal(BigDecimal valorNormal) {
			this.valorNormal = valorNormal;
		}
		public BigDecimal getValorPassivo() {
			return valorPassivo;
		}
		public void setValorPassivo(BigDecimal valorPassivo) {
			this.valorPassivo = valorPassivo;
		}
		public BigDecimal getValorTotal() {
			return this.valorPassivo.add(valorNormal);
		}
	}

}
