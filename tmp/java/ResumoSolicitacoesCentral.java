package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
public class ResumoSolicitacoesCentral {
	
	private List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
	private Set<Inner> inners = new HashSet<Inner>();
	private Date dataInicial;
	private Date dataFinal;
	private UsuarioInterface usuario;
	private Integer tipoResultado;

	public ResumoSolicitacoesCentral(List<GuiaSimples> guias, Date dataInicial, Date dataFinal, UsuarioInterface usuario, Integer tipo) {
		this.guias = guias;
		this.dataFinal = dataFinal;
		this.dataInicial = dataInicial;
		this.usuario = usuario;
		this.tipoResultado = tipo;
		computar();
	}

	private void computar() {
		Map<AbstractSegurado, Set<Procedimento>> procedimentosPorSegurado = new HashMap<AbstractSegurado, Set<Procedimento>>();
		
		if (this.tipoResultado.equals(RelatorioSolicitacoesExamesCentralService.TIPO_RELATORIO_EXAMES)) {
			for (GuiaSimples guia : guias) {
				if(!procedimentosPorSegurado.keySet().contains(guia.getSegurado())) {
					Set<Procedimento> procedimentos = new HashSet<Procedimento>();
					procedimentos.addAll(guia.getProcedimentos());
					procedimentosPorSegurado.put(guia.getSegurado(), procedimentos);
				}else {
					procedimentosPorSegurado.get(guia.getSegurado()).addAll(guia.getProcedimentos());
				}
			}
			
			int ordem = 0;
			for (AbstractSegurado seg : procedimentosPorSegurado.keySet()) {
				Inner inner = new Inner();
				inner.segurado = seg;
				inner.procedimentos = procedimentosPorSegurado.get(seg);
				ordem++;
				inner.setOrdem(ordem);
				inners.add(inner);
				
			}
		}else {
			int ordem = 0;
			for (GuiaSimples guia : this.guias) {
				Inner inner = new Inner();
				inner.segurado = guia.getSegurado();
				inner.setGuia(guia);
				ordem++;
				inner.setOrdem(ordem);
				inners.add(inner);
			}
		}
			
		
	}
	
	public List<GuiaSimples> getGuias() {
		return guias;
	}

	public void setGuias(List<GuiaSimples> guias) {
		this.guias = guias;
	}

	public Set<Inner> getInners() {
		return inners;
	}

	public void setInners(Set<Inner> inners) {
		this.inners = inners;
	}
	
	
	public class Inner {
		private AbstractSegurado segurado;
		private Set<Procedimento> procedimentos;
		private GuiaSimples guia;
		private int ordem;
		
		public int getOrdem() {
			return ordem;
		}
		public void setOrdem(int ordem) {
			this.ordem = ordem;
		}
		public AbstractSegurado getSegurado() {
			return segurado;
		}
		public void setSegurado(AbstractSegurado segurado) {
			this.segurado = segurado;
		}
		public Set<Procedimento> getProcedimentos() {
			return procedimentos;
		}
		public void setProcedimentos(Set<Procedimento> procedimentos) {
			this.procedimentos = procedimentos;
		}
		public GuiaSimples getGuia() {
			return guia;
		}
		public void setGuia(GuiaSimples guia) {
			this.guia = guia;
		}
	}


	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public String getPeriodo() {
		String dtInicial = dataInicial == null? " - ":Utils.format(dataInicial);
		String dtFinal = dataFinal == null? " - ":Utils.format(dataFinal);
		
		return dtInicial  + " a " + dtFinal; 
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}

	public Integer getTipoResultado() {
		return tipoResultado;
	}

	public void setTipoResultado(Integer tipoResultado) {
		this.tipoResultado = tipoResultado;
	}
}
