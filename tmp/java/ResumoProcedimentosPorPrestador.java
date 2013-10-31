/**
 * 
 */
package br.com.infowaypi.ecare.resumos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
public class ResumoProcedimentosPorPrestador {
	
	public static final String[] SITUACOES_PROCEDIMENTOS_GUIAS_CONS_E_EXAMES = {SituacaoEnum.FATURADA.descricao(), SituacaoEnum.FECHADO.descricao(), SituacaoEnum.CONFIRMADO.descricao(), SituacaoEnum.AUTORIZADO.descricao()};
	public static final String[] SITUACOES_GUIAS = {SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(), SituacaoEnum.FATURADA.descricao(), SituacaoEnum.AUDITADO.descricao(),
																SituacaoEnum.PRORROGADO.descricao(), SituacaoEnum.ABERTO.descricao(), SituacaoEnum.FECHADO.descricao(), SituacaoEnum.NAO_PRORROGADO.descricao(), SituacaoEnum.CONFIRMADO.descricao()};
	public static final String[] SITUACOES_PROCEIDMENTOS_GUIAS_INTERNACOES = {SituacaoEnum.AUTORIZADO.descricao(), SituacaoEnum.FATURADA.descricao(), SituacaoEnum.FECHADO.descricao()};
	
	private List<Prestador> prestadores;
	private TabelaCBHPM procedimento;
	private Date dataFinal;
	private Date dataInicial;
	private List<GuiaSimples> guias;
	private Map<Prestador, Set<Procedimento>> mapaProcedimentosPorPrestador;
	private Map<Prestador, Set<GuiaSimples<ProcedimentoInterface>>> mapaGuiasPorPrestador;
	private Set<ItemResumo> itens;
	
	
	public List<Prestador> getPrestadores() {
		return prestadores;
	}

	public void setPrestadores(List<Prestador> prestadores) {
		this.prestadores = prestadores;
	}

	public TabelaCBHPM getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(TabelaCBHPM procedimento) {
		this.procedimento = procedimento;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}


	public Map<Prestador, Set<Procedimento>> getMapaProcedimentosPorPrestador() {
		return mapaProcedimentosPorPrestador;
	}

	public void setMapaProcedimentosPorPrestador(
			Map<Prestador, Set<Procedimento>> mapaProcedimentosPorPrestador) {
		this.mapaProcedimentosPorPrestador = mapaProcedimentosPorPrestador;
	}

	public Set<ItemResumo> getItens() {
		return itens;
	}

	public void setItens(Set<ItemResumo> itens) {
		this.itens = itens;
	}

	public ResumoProcedimentosPorPrestador(Prestador prestador, TabelaCBHPM procedimento, Date dataInicial, Date dataFinal) throws Exception {
		this.prestadores = new ArrayList<Prestador>();
		this.prestadores.add(prestador);
		this.procedimento = procedimento;
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
		this.guias = new ArrayList<GuiaSimples>();;
		this.mapaProcedimentosPorPrestador = new HashMap<Prestador, Set<Procedimento>>();
		this.mapaGuiasPorPrestador = new HashMap<Prestador, Set<GuiaSimples<ProcedimentoInterface>>>();
		this.itens = new HashSet<ItemResumo>();
		
		if(this.procedimento == null) {
			throw new Exception("O parâmetro procedimento é requerido.");
		}
		
		if(this.dataInicial == null) {
			throw new Exception("O parâmetro Data Inicial é requerido.");
		}
		
		if(this.dataFinal == null) {
			dataFinal = new Date();
		}
		
		if(prestador == null) {
			Criteria c = HibernateUtil.currentSession().createCriteria(Prestador.class);
			c.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
			c.setFetchMode("usuarios", FetchMode.SELECT);
			c.setFetchMode("profissionais", FetchMode.SELECT);
			c.setFetchMode("especialidades", FetchMode.SELECT);
			c.setFetchMode("faturamentos", FetchMode.SELECT);
			c.setFetchMode("acordosPorte", FetchMode.SELECT);
			c.setFetchMode("acordosCBHPM", FetchMode.SELECT);
			c.setFetchMode("procedimentos", FetchMode.SELECT);
			c.setFetchMode("acordosMatmed", FetchMode.SELECT);
			c.setFetchMode("acordosDiaria", FetchMode.SELECT);
			c.setFetchMode("acordosTaxa", FetchMode.SELECT);
			c.setFetchMode("acordosGasoterapia", FetchMode.SELECT);
			c.setFetchMode("acordosPacote", FetchMode.SELECT);
			
			this.prestadores = c.list();
		}
		
	}
	
	
	
	public void buscarGuias() {
		List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
		Criteria c = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
		c.add(Expression.in("prestador", prestadores));
		c.add(Expression.in("situacao.descricao", SITUACOES_GUIAS));
		c.add(Expression.ge("dataAtendimento", this.dataInicial));
		c.add(Expression.le("dataAtendimento", this.dataFinal));
		c.setFetchMode("guiasFilhas", FetchMode.SELECT);
		c.setFetchMode("guiaOrigem", FetchMode.SELECT);
		c.setFetchMode("fluxoFinanceiro", FetchMode.SELECT);
		guias = c.list();
		
		this.guias= guias;
		System.out.println("NUMERO DE GUIAS: "+ this.guias.size());

	}
	
	
	public void alimentarMapaGuiasPorPrestador() {
		
		for (Prestador prestador: this.prestadores) {
			if(!mapaGuiasPorPrestador.keySet().contains(prestador)) {
				Set<GuiaSimples<ProcedimentoInterface>> guias = new HashSet<GuiaSimples<ProcedimentoInterface>>();
				for (GuiaSimples guia : this.guias) {
					if(guia.getPrestador().equals(prestador)) {
						guias.add(guia);
					}
				}
				mapaGuiasPorPrestador.put(prestador, guias);
			}
		}	
	}
	
	public void alimentarMapaProcedimentosPorPrestador() {
		String[] situacoes = {SituacaoEnum.FATURADA.descricao(),SituacaoEnum.FECHADO.descricao(),SituacaoEnum.CONFIRMADO.descricao(),SituacaoEnum.AUTORIZADO.descricao()};
		
		for (Prestador prestador : this.prestadores) {

			System.out.println("NUMEROS DE GUIAS FILTRADAS: " + mapaGuiasPorPrestador.get(prestador).size());
			
			Set<Procedimento> procedimentosFiltrados = new HashSet<Procedimento>();
			
			if(!mapaGuiasPorPrestador.get(prestador).isEmpty()){
				Criteria c = HibernateUtil.currentSession().createCriteria(Procedimento.class);
				c.add(Expression.in("situacao.descricao", situacoes));
				c.add(Expression.in("guia", mapaGuiasPorPrestador.get(prestador)));
				c.add(Expression.eq("procedimentoDaTabelaCBHPM", this.procedimento));
				procedimentosFiltrados.addAll(c.list());
			}
			mapaProcedimentosPorPrestador.put(prestador, procedimentosFiltrados);
			
		}
	}
	
	public void processarMapa() {
		for (Prestador prestador : this.mapaProcedimentosPorPrestador.keySet()) {
			ItemResumo item = new ItemResumo();
			item.setProcedimento(this.getProcedimento());
			item.setPrestador(prestador);
			
			int count = 0;
			BigDecimal valorTotal = BigDecimal.ZERO;
			BigDecimal valorUnitario = BigDecimal.ZERO;
			for (Procedimento procedimento : this.mapaProcedimentosPorPrestador.get(prestador)) {
				count += procedimento.getQuantidade();
				valorTotal = valorTotal.add(procedimento.getValorTotal());
				valorUnitario = procedimento.getValorAtualDoProcedimento();
			}
			item.setValorUnitario(valorUnitario);
			item.setNumeroDeProcedimentos(count);
			item.setValorTotal(valorTotal);
			this.itens.add(item);
		}
	}

	public List<GuiaSimples> getGuias() {
		return guias;
	}

	public void setGuias(List<GuiaSimples> guias) {
		this.guias = guias;
	}
	
}
