package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.services.recurso.ItemRecursoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.msr.utils.Utils;

/**
 * 
 * @author Diogo Vinícius
 * @changes Luciano Rocha
 *
 */
@SuppressWarnings("rawtypes")
public class DetalheValoresFaturamento implements Comparable<DetalheValoresFaturamento>{
	
	public static final String DESCRICAO_TOTAL = "TOTAL";
	private Date competencia;
	private String descricao;

	private BigDecimal valorConsultas;
	private BigDecimal valorConsultasOdonto;
	private BigDecimal valorExames;
	private BigDecimal valorExamesOdonto;
	private BigDecimal valorAtendimentosUrgencia;
	private BigDecimal valorInternacoes;
	private BigDecimal valorRecursosDeferidos;
	private BigDecimal valorGuiasHonorario;
	private BigDecimal valorAcompanhamentoAnestesico;
	
	private BigDecimal valorCoParticpacaoConsultas;
	private BigDecimal valorCoParticpacaoCoParticpacaoConsultasOdonto;
	private BigDecimal valorCoParticpacaoExames;
	private BigDecimal valorCoParticpacaoExamesOdonto;
	private BigDecimal valorCoParticpacaoAtendimentosUrgencia;
	private BigDecimal valorCoParticpacaoInternacoes;
	
	private int quantidadeConsultas;
	private int quantidadeConsultasOdonto;
	private int quantidadeExames;
	private int quantidadeExamesOdonto;
	private int quantidadeProcedimentos;
	private int quantidadeAtendimentosUrgencia;
	private int quantidadeInternacoes;
	private int quantidadeRecursosDeferidos;
	private int quantidadeGuiasHonorario;
	private int quantidadeAcompanhamentoAnestesico;

	private Set<GuiaFaturavel> guias;

	public DetalheValoresFaturamento(Date competencia){
		this.guias                     = new HashSet<GuiaFaturavel>();
		this.competencia               = competencia;
		this.valorConsultas            = BigDecimal.ZERO;
		this.valorConsultasOdonto      = BigDecimal.ZERO;
		this.valorExames               = BigDecimal.ZERO;
		this.valorExamesOdonto         = BigDecimal.ZERO;
		this.valorAtendimentosUrgencia = BigDecimal.ZERO;
		this.valorInternacoes          = BigDecimal.ZERO;
		this.valorRecursosDeferidos    = BigDecimal.ZERO;
		this.valorGuiasHonorario       = BigDecimal.ZERO;
		this.valorAcompanhamentoAnestesico = BigDecimal.ZERO;
		
		this.valorCoParticpacaoConsultas 					= BigDecimal.ZERO;
		this.valorCoParticpacaoCoParticpacaoConsultasOdonto = BigDecimal.ZERO;
		this.valorCoParticpacaoExames						= BigDecimal.ZERO;
		this.valorCoParticpacaoExamesOdonto					= BigDecimal.ZERO;
		this.valorCoParticpacaoAtendimentosUrgencia			= BigDecimal.ZERO;
		this.valorCoParticpacaoInternacoes					= BigDecimal.ZERO;

		if(competencia == null){
			descricao = DESCRICAO_TOTAL;
		} else {
			this.descricao = Utils.format(competencia).substring(3, 10);
		}
	}
	
	public DetalheValoresFaturamento (List<GuiaSimples> guias) {
		this.valorConsultas            = BigDecimal.ZERO;
		this.valorExames               = BigDecimal.ZERO;
		this.valorConsultasOdonto      = BigDecimal.ZERO;
		this.valorExamesOdonto         = BigDecimal.ZERO;
		this.valorAtendimentosUrgencia = BigDecimal.ZERO;
		this.valorInternacoes          = BigDecimal.ZERO;
		this.valorRecursosDeferidos    = BigDecimal.ZERO;
		this.valorGuiasHonorario       = BigDecimal.ZERO;
		
		this.valorCoParticpacaoConsultas 					= BigDecimal.ZERO;
		this.valorCoParticpacaoCoParticpacaoConsultasOdonto = BigDecimal.ZERO;
		this.valorCoParticpacaoExames						= BigDecimal.ZERO;
		this.valorCoParticpacaoExamesOdonto					= BigDecimal.ZERO;
		this.valorCoParticpacaoAtendimentosUrgencia			= BigDecimal.ZERO;
		this.valorCoParticpacaoInternacoes					= BigDecimal.ZERO;
		
		this.valorAcompanhamentoAnestesico = BigDecimal.ZERO;
		computarGuias(guias);		
	}
	
	public int getQuantidadeTotal(){
		return this.quantidadeConsultas +
			this.quantidadeConsultasOdonto +
			this.quantidadeExames +
			this.quantidadeExamesOdonto +
			this.quantidadeAtendimentosUrgencia +
			this.quantidadeInternacoes +
			this.quantidadeRecursosDeferidos +
			this.quantidadeGuiasHonorario +
			this.quantidadeAcompanhamentoAnestesico;
	}
	
	public BigDecimal getValorTotal() {
		BigDecimal valor = BigDecimal.ZERO;
		valor = valor.add(this.valorConsultas);
		valor = valor.add(this.valorConsultasOdonto);
		valor = valor.add(this.valorExames);
		valor = valor.add(this.valorExamesOdonto);
		valor = valor.add(this.valorAtendimentosUrgencia);
		valor = valor.add(this.valorInternacoes);
		valor = valor.add(this.valorRecursosDeferidos);
		valor = valor.add(this.valorGuiasHonorario);
		valor = valor.add(this.valorAcompanhamentoAnestesico);
		return valor;
	}
	
	public BigDecimal getValorTotalCoParticipacao() {
		BigDecimal valor = BigDecimal.ZERO;
		valor = valor.add(this.valorCoParticpacaoConsultas);
		valor = valor.add(this.valorCoParticpacaoCoParticpacaoConsultasOdonto);
		valor = valor.add(this.valorCoParticpacaoExames);
		valor = valor.add(this.valorCoParticpacaoExamesOdonto);
		valor = valor.add(this.valorCoParticpacaoAtendimentosUrgencia);
		valor = valor.add(this.valorCoParticpacaoInternacoes);
		return valor;
	}
	
	private void computarGuias(List<GuiaSimples> guias) {
		for (GuiaSimples guia : guias) {
			if (guia.isConsultaOdonto()) {
				this.quantidadeConsultasOdonto++;
				this.valorConsultasOdonto = this.valorConsultasOdonto.add(guia.getValorTotal());
				this.valorCoParticpacaoCoParticpacaoConsultasOdonto = this.valorCoParticpacaoCoParticpacaoConsultasOdonto.add(guia.getValorCoParticipacao());
			}
			else if (guia.isConsulta()) {
				this.quantidadeConsultas++;
				this.valorConsultas = this.valorConsultas.add(guia.getValorTotal());
				this.valorCoParticpacaoConsultas = this.valorCoParticpacaoConsultas.add(guia.getValorCoParticipacao()); 
			}
			else if (guia.isExameOdonto()) {
				this.quantidadeExamesOdonto++;
				this.valorExamesOdonto = this.valorExamesOdonto.add(guia.getValorTotal());
				this.valorCoParticpacaoExamesOdonto = this.valorCoParticpacaoExamesOdonto.add(guia.getValorCoParticipacao());
			}
			else if (guia.isExame()) {
				this.quantidadeExames++;
				this.valorExames = this.valorExames.add(guia.getValorTotal());
				this.valorCoParticpacaoExames = this.valorCoParticpacaoExames.add(guia.getValorCoParticipacao());
			}
			else if (guia.isAtendimentoUrgencia() || guia.isConsultaUrgencia()) {
				this.quantidadeAtendimentosUrgencia++;
				this.valorAtendimentosUrgencia = this.valorAtendimentosUrgencia.add(guia.getValorTotal());
				this.valorCoParticpacaoAtendimentosUrgencia = this.valorCoParticpacaoAtendimentosUrgencia.add(guia.getValorCoParticipacao());
			}
			else if (guia.isInternacao()) {
				this.quantidadeInternacoes++;
				this.valorInternacoes = this.valorInternacoes.add(guia.getValorTotal());
				this.valorCoParticpacaoInternacoes = this.valorCoParticpacaoInternacoes.add(guia.getValorCoParticipacao());
			}
			else if (guia.isHonorarioMedico()) {
				this.quantidadeGuiasHonorario++;
				this.valorGuiasHonorario = this.valorGuiasHonorario.add(guia.getValorTotal());
			}
			else if (guia.isAcompanhamentoAnestesico()) {
				this.quantidadeAcompanhamentoAnestesico++;
				this.valorAcompanhamentoAnestesico = this.valorAcompanhamentoAnestesico.add(guia.getValorTotal());
			}
		}
	}
	
	//getters n' setters from fields
	public Date getCompetencia() {
		return competencia;
	}
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public BigDecimal getValorConsultas() {
		return valorConsultas;
	}
	public void setValorConsultas(BigDecimal valorConsultas) {
		this.valorConsultas = valorConsultas;
	}
	public BigDecimal getValorConsultasOdonto() {
		return valorConsultasOdonto;
	}
	public void setValorConsultasOdonto(BigDecimal valorConsultasOdonto) {
		this.valorConsultasOdonto = valorConsultasOdonto;
	}
	public BigDecimal getValorExames() {
		return valorExames;
	}
	public void setValorExames(BigDecimal valorExames) {
		this.valorExames = valorExames;
	}
	public BigDecimal getValorExamesOdonto() {
		return valorExamesOdonto;
	}
	public void setValorExamesOdonto(BigDecimal valorExamesOdonto) {
		this.valorExamesOdonto = valorExamesOdonto;
	}
	public BigDecimal getValorAtendimentosUrgencia() {
		return valorAtendimentosUrgencia;
	}
	public void setValorAtendimentosUrgencia(BigDecimal valorAtendimentosUrgencia) {
		this.valorAtendimentosUrgencia = valorAtendimentosUrgencia;
	}
	public BigDecimal getValorInternacoes() {
		return valorInternacoes;
	}
	public void setValorInternacoes(BigDecimal valorInternacoes) {
		this.valorInternacoes = valorInternacoes;
	}
	public BigDecimal getValorGuiasHonorario() {
		return valorGuiasHonorario;
	}
	public void setValorGuiasHonorario(BigDecimal valorGuiasHonorario) {
		this.valorGuiasHonorario = valorGuiasHonorario;
	}
	public int getQuantidadeConsultas() {
		return quantidadeConsultas;
	}
	public void setQuantidadeConsultas(int quantidadeConsultas) {
		this.quantidadeConsultas = quantidadeConsultas;
	}
	public int getQuantidadeConsultasOdonto() {
		return quantidadeConsultasOdonto;
	}
	public void setQuantidadeConsultasOdonto(int quantidadeConsultasOdonto) {
		this.quantidadeConsultasOdonto = quantidadeConsultasOdonto;
	}
	public int getQuantidadeExames() {
		return quantidadeExames;
	}
	public void setQuantidadeExames(int quantidadeExames) {
		this.quantidadeExames = quantidadeExames;
	}
	public int getQuantidadeExamesOdonto() {
		return quantidadeExamesOdonto;
	}
	public void setQuantidadeExamesOdonto(int quantidadeExamesOdonto) {
		this.quantidadeExamesOdonto = quantidadeExamesOdonto;
	}
	public int getQuantidadeProcedimentos() {
		return quantidadeProcedimentos;
	}
	public void setQuantidadeProcedimentos(int quantidadeProcedimentos) {
		this.quantidadeProcedimentos = quantidadeProcedimentos;
	}
	public int getQuantidadeAtendimentosUrgencia() {
		return quantidadeAtendimentosUrgencia;
	}
	public void setQuantidadeAtendimentosUrgencia(int quantidadeAtendimentosUrgencia) {
		this.quantidadeAtendimentosUrgencia = quantidadeAtendimentosUrgencia;
	}
	public int getQuantidadeInternacoes() {
		return quantidadeInternacoes;
	}
	public void setQuantidadeInternacoes(int quantidadeInternacoes) {
		this.quantidadeInternacoes = quantidadeInternacoes;
	}
	public int getQuantidadeGuiasHonorario() {
		return quantidadeGuiasHonorario;
	}
	public void setQuantidadeGuiasHonorario(int quantidadeGuiasHonorario) {
		this.quantidadeGuiasHonorario = quantidadeGuiasHonorario;
	}
	public Set<GuiaFaturavel> getGuias() {
		return guias;
	}
	public void setGuias(Set<GuiaFaturavel> guias) {
		this.guias = guias;
	}

	public int compareTo(DetalheValoresFaturamento detalhe) {
		if(this.getCompetencia() == null)
			return 1;
		
		if(detalhe.getCompetencia() == null)
			return -1;
		
		return (this.competencia.compareTo(detalhe.getCompetencia()) * -1);
	}

	public BigDecimal getValorAcompanhamentoAnestesico() {
		return valorAcompanhamentoAnestesico;
	}

	public void setValorAcompanhamentoAnestesico(
			BigDecimal valorAcompanhamentoAnestesico) {
		this.valorAcompanhamentoAnestesico = valorAcompanhamentoAnestesico;
	}

	public int getQuantidadeAcompanhamentoAnestesico() {
		return quantidadeAcompanhamentoAnestesico;
	}

	public void setQuantidadeAcompanhamentoAnestesico(
			int quantidadeAcompanhamentoAnestesico) {
		this.quantidadeAcompanhamentoAnestesico = quantidadeAcompanhamentoAnestesico;
	}

	public BigDecimal getValorCoParticpacaoConsultas() {
		return valorCoParticpacaoConsultas;
	}

	public void setValorCoParticpacaoConsultas(
			BigDecimal valorCoParticpacaoConsultas) {
		this.valorCoParticpacaoConsultas = valorCoParticpacaoConsultas;
	}

	public BigDecimal getValorCoParticpacaoCoParticpacaoConsultasOdonto() {
		return valorCoParticpacaoCoParticpacaoConsultasOdonto;
	}

	public void setValorCoParticpacaoCoParticpacaoConsultasOdonto(
			BigDecimal valorCoParticpacaoCoParticpacaoConsultasOdonto) {
		this.valorCoParticpacaoCoParticpacaoConsultasOdonto = valorCoParticpacaoCoParticpacaoConsultasOdonto;
	}

	public BigDecimal getValorCoParticpacaoExames() {
		return valorCoParticpacaoExames;
	}

	public void setValorCoParticpacaoExames(BigDecimal valorCoParticpacaoExames) {
		this.valorCoParticpacaoExames = valorCoParticpacaoExames;
	}

	public BigDecimal getValorCoParticpacaoExamesOdonto() {
		return valorCoParticpacaoExamesOdonto;
	}

	public void setValorCoParticpacaoExamesOdonto(
			BigDecimal valorCoParticpacaoExamesOdonto) {
		this.valorCoParticpacaoExamesOdonto = valorCoParticpacaoExamesOdonto;
	}

	public BigDecimal getValorCoParticpacaoAtendimentosUrgencia() {
		return valorCoParticpacaoAtendimentosUrgencia;
	}

	public void setValorCoParticpacaoAtendimentosUrgencia(
			BigDecimal valorCoParticpacaoAtendimentosUrgencia) {
		this.valorCoParticpacaoAtendimentosUrgencia = valorCoParticpacaoAtendimentosUrgencia;
	}

	public BigDecimal getValorCoParticpacaoInternacoes() {
		return valorCoParticpacaoInternacoes;
	}

	public void setValorCoParticpacaoInternacoes(
			BigDecimal valorCoParticpacaoInternacoes) {
		this.valorCoParticpacaoInternacoes = valorCoParticpacaoInternacoes;
	}

	public BigDecimal getValorRecursosDeferidos() {
		return valorRecursosDeferidos;
	}

	public void setValorRecursosDeferidos(BigDecimal valorRecursosDeferidos) {
		this.valorRecursosDeferidos = valorRecursosDeferidos;
	}
	
}
