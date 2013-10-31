package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioInternacaoPorPrestador {
	public static final String TIPO_INTERNACAO_ELETIVA = "IEL";
	public static final String TIPO_INTERNACAO_URGENCIA = "IUR";

	public static final String POR_PRESTADOR = "Prestador";
	public static final String POR_INTERNACAO_ABERTA = "Internação Aberta";
	public static final String POR_INTERNACAO_FECHADA = "Internação Fechada";

	/**
	 * Quantidade de prestadores com produção.
	 */
	private int qtdePrestadores;
	
	/**
	 * Tipo de internação informado pelo usuário.
	 * Ex.: Internação Eletiva.
	 */
	private String tipoInternacao;
	
	/**
	 * Quantidade de internações autorizadas no período informado.
	 */
	private int qtdeInternacoesAutorizadas;
	
	/**
	 * Quantidade de internações abertas.
	 */
	private int qtdeInternacoesAbertas;
	
	/**
	 * Quantidade de internações fechadas.
	 */
	private int qtdeInternacoesFechadas;
	
	/**
	 * Detalhamento do relatório de internações por prestador.
	 */
	private List<ResumoInternacoesPorTratamento> resumoInternacoesPorTratamento;

	public RelatorioInternacaoPorPrestador() {
		this.resumoInternacoesPorTratamento = new ArrayList<ResumoInternacoesPorTratamento>();
	}
	
	/**
	 * Gera o relatório de internações por prestador.
	 * @param dataInicial
	 * @param dataFinal
	 * @param tipoInternacao
	 * @param ordem
	 * @return Resumo e detalhamento do relatório.
	 * @throws ValidateException
	 */
	public RelatorioInternacaoPorPrestador gerarRelatorio(Date dataInicial, Date dataFinal, String tipoInternacao, String ordem) throws ValidateException {
		this.validarIntervalo(dataInicial, dataFinal);
		this.validarTipoInternacao(tipoInternacao);
		
        if (tipoInternacao.equals(TIPO_INTERNACAO_ELETIVA))
        	this.tipoInternacao = "Internação Eletiva";
        if (tipoInternacao.equals(TIPO_INTERNACAO_URGENCIA))
        	this.tipoInternacao = "Internação de Urgência";
		
		List<GuiaCompleta> internacoesAutorizadas = this.buscarInternacoesAutorizadas(dataInicial, dataFinal, tipoInternacao);
		List<GuiaCompleta> internacoesAbertas = this.getInternacoes(internacoesAutorizadas, SituacaoEnum.ABERTO.descricao());
		List<GuiaCompleta> internacoesFechadas = this.getInternacoes(internacoesAbertas, SituacaoEnum.FECHADO.descricao());
		
		Map<Prestador, Map<Integer, Set<GuiaCompleta>>> mapaInternacoesAbertas = this.agruparInternacoes(internacoesAbertas);
		Map<Prestador, Map<Integer, Set<GuiaCompleta>>> mapaInternacoesFechadas = this.agruparInternacoes(internacoesFechadas);
		
		Set<Prestador> prestadores = new HashSet<Prestador>();
		prestadores.addAll(this.getPrestadores(mapaInternacoesAbertas));
		prestadores.addAll(this.getPrestadores(mapaInternacoesFechadas));
		
		this.qtdePrestadores = prestadores.size();
		
		for (Prestador prestador : prestadores) {
			this.resumoInternacoesPorTratamento.add(new ResumoInternacoesPorTratamento(
					prestador.getPessoaJuridica().getFantasia(),
					this.getInternacoesPrestadorTratamento(prestador, GuiaCompleta.TRATAMENTO_CLINICO, mapaInternacoesAbertas).size(),
					this.getInternacoesPrestadorTratamento(prestador, GuiaCompleta.TRATAMENTO_CIRURGICO, mapaInternacoesAbertas).size(),
					this.getInternacoesPrestadorTratamento(prestador, GuiaCompleta.TRATAMENTO_CLINICO, mapaInternacoesFechadas).size(),
					this.getInternacoesPrestadorTratamento(prestador, GuiaCompleta.TRATAMENTO_CIRURGICO, mapaInternacoesFechadas).size()
					));
		}
		
		this.ordenarDetalhamneto(this.resumoInternacoesPorTratamento, ordem);

		this.resumoInternacoesPorTratamento.add(new ResumoInternacoesPorTratamento(
				"TOTAL",
				this.getInternacoesTratamento(GuiaCompleta.TRATAMENTO_CLINICO, mapaInternacoesAbertas).size(),
				this.getInternacoesTratamento(GuiaCompleta.TRATAMENTO_CIRURGICO, mapaInternacoesAbertas).size(),
				this.getInternacoesTratamento(GuiaCompleta.TRATAMENTO_CLINICO, mapaInternacoesFechadas).size(),
				this.getInternacoesTratamento(GuiaCompleta.TRATAMENTO_CIRURGICO, mapaInternacoesFechadas).size()
				));

		this.qtdeInternacoesAutorizadas = internacoesAutorizadas.size();
		this.qtdeInternacoesAbertas = this.getInternacoesTratamento(GuiaCompleta.TRATAMENTO_CLINICO, mapaInternacoesAbertas).size() + this.getInternacoesTratamento(GuiaCompleta.TRATAMENTO_CIRURGICO, mapaInternacoesAbertas).size();
		this.qtdeInternacoesFechadas = this.getInternacoesTratamento(GuiaCompleta.TRATAMENTO_CLINICO, mapaInternacoesFechadas).size() + this.getInternacoesTratamento(GuiaCompleta.TRATAMENTO_CIRURGICO, mapaInternacoesFechadas).size();
		
		return this;
	}
	
	/**
	 * Busca no banco de dados todas as internações autorizadas no intervalo informado.
	 * <h2>Observação:</h2>
	 * <ul>Internação Eletiva - Autorizada.</ul>
	 * <ul>Internação Urgência - Aberta.</ul>
	 * @param dataInicial
	 * @param dataFinal
	 * @param tipoInternacao
	 * @return Lista de guias.
	 */
	@SuppressWarnings("unchecked")
	private List<GuiaCompleta> buscarInternacoesAutorizadas(Date dataInicial, Date dataFinal, String tipoInternacao){
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaCompleta.class);
		criteria.add(Expression.eq("tipoDeGuia", tipoInternacao));
		criteria.createAlias("colecaoSituacoes", "col");
		criteria.createAlias("col.situacoes", "sit");

		if (tipoInternacao.equals(TIPO_INTERNACAO_ELETIVA)){
			criteria.add(Expression.eq("sit.descricao", SituacaoEnum.AUTORIZADO.descricao()));
		}
		if (tipoInternacao.equals(TIPO_INTERNACAO_URGENCIA)){
			// TODO Substituir a situação ABERTO por AUTORIZADO quando a situação das guias estiverem bem definidas.
			criteria.add(Expression.eq("sit.descricao", SituacaoEnum.ABERTO.descricao()));
		}

		Calendar dataAuxiliar = new GregorianCalendar();
		dataAuxiliar.setTime(dataFinal);
		dataAuxiliar.add(Calendar.DAY_OF_MONTH, 1);
//		A dataAuxiliar é necessária, pois dataSituacao é um Timestamp.
		criteria.add(Expression.between("sit.dataSituacao", dataInicial, dataAuxiliar.getTime()));

		return criteria.list();
	}
	
	/**
	 * Retorna um subgrupo de internações de acordo com a situação desejada.
	 * @param internacoesPesquisadas
	 * @param situacao
	 * @return Lista de guias.
	 */
	@SuppressWarnings("unchecked")
	private List<GuiaCompleta> getInternacoes(List<GuiaCompleta> internacoesPesquisadas, String situacao){
		List<GuiaCompleta> internacoes = new ArrayList<GuiaCompleta>();
		
		for (GuiaCompleta internacao : internacoesPesquisadas) {
			if (internacao.isExisteSituacao(situacao)){
				internacoes.add(internacao);
			}
		}
		
		return internacoes;
	}

	/**
	 * Agrupa as internações por prestador e tipo de tratamento clínico.
	 * @param internacoes
	 * @return Mapa de prestadores (chave externa), tipo de tratamento (chave interna) e guias (valores).
	 */
	private Map<Prestador, Map<Integer, Set<GuiaCompleta>>> agruparInternacoes(List<GuiaCompleta> internacoes){
		Map<Prestador, Map<Integer, Set<GuiaCompleta>>> prestadorTratamentoInternacao = new HashMap<Prestador, Map<Integer,Set<GuiaCompleta>>>();
		
		for (GuiaCompleta internacao : internacoes) {
			if (internacao.getTipoTratamento() == GuiaCompleta.TRATAMENTO_CLINICO || internacao.getTipoTratamento() == GuiaCompleta.TRATAMENTO_CIRURGICO){
//			Definir o tipo de tratamento é necessário, pois existe e pode existir outros tipos de tratamento. 
				Prestador prestador = internacao.getPrestador();
				if (prestadorTratamentoInternacao.containsKey(prestador)) {
					Map<Integer, Set<GuiaCompleta>> mapaInterno = povoarMapaInterno(internacao, prestadorTratamentoInternacao.get(prestador));
					prestadorTratamentoInternacao.put(prestador, mapaInterno);
				} else {
					Map<Integer, Set<GuiaCompleta>> mapaInterno = povoarMapaInterno(internacao, new HashMap<Integer, Set<GuiaCompleta>>());
					prestadorTratamentoInternacao.put(prestador, mapaInterno);
				}
			}
		}
		
		return prestadorTratamentoInternacao;
	}
	
	/**
	 * Agrupa as internações por tipo de tratamento.
	 * @param internacao
	 * @param mapaInterno
	 * @return Mapa de tipos de tratamento (chave) e guias (valores).
	 */
	private Map<Integer, Set<GuiaCompleta>> povoarMapaInterno(GuiaCompleta internacao, Map<Integer, Set<GuiaCompleta>> mapaInterno){
		Integer tipoTratamento = internacao.getTipoTratamento();
		if (mapaInterno.containsKey(tipoTratamento)){
			Set<GuiaCompleta> internacaoInterna = mapaInterno.get(tipoTratamento);
			internacaoInterna.add(internacao);
			mapaInterno.put(tipoTratamento, internacaoInterna);
		} else {
			Set<GuiaCompleta> internacaoInterna = new HashSet<GuiaCompleta>();
			internacaoInterna.add(internacao);
			mapaInterno.put(tipoTratamento, internacaoInterna);
		}

		return mapaInterno;
	}
	
	/**
	 * Retorna todos os prestadores com produção, ou seja, aqueles que possuem pelo menos uma guia.
	 * @param prestadorTratamentoInternacao
	 * @return Conjunto de prestadores.
	 */
	private Set<Prestador> getPrestadores(Map<Prestador, Map<Integer, Set<GuiaCompleta>>> prestadorTratamentoInternacao){
		return prestadorTratamentoInternacao.keySet();
	}
	
	/**
	 * Retorna todas as internações que possuem um determinado tipo de tratamento.
	 * @param tratamento
	 * @param prestadorTratamentoInternacao
	 * @return Conjunto de guias.
	 */
	private Set<GuiaCompleta> getInternacoesTratamento(Integer tratamento, Map<Prestador, Map<Integer, Set<GuiaCompleta>>> prestadorTratamentoInternacao){
		Set<GuiaCompleta> internacoes = new HashSet<GuiaCompleta>();
		
		for (Prestador prestador : this.getPrestadores(prestadorTratamentoInternacao)) {
			internacoes.addAll(this.getInternacoesPrestadorTratamento(prestador, tratamento, prestadorTratamentoInternacao));
		}
		
		return internacoes;
	}
	
	/**
	 * Retorna as internações de um determinado prestador para um dado tipo de tratamento.
	 * @param prestador
	 * @param tratamento
	 * @param prestadorTratamentoInternacao
	 * @return Conjunto de guias.
	 */
	private Set<GuiaCompleta> getInternacoesPrestadorTratamento(Prestador prestador, Integer tratamento, Map<Prestador, Map<Integer, Set<GuiaCompleta>>> prestadorTratamentoInternacao){
		Set<GuiaCompleta> internacoes = new HashSet<GuiaCompleta>();
		
		if (prestadorTratamentoInternacao.containsKey(prestador)){
			Map<Integer, Set<GuiaCompleta>> tratamentoInternacao = prestadorTratamentoInternacao.get(prestador);
			if (tratamentoInternacao.containsKey(tratamento)){
				internacoes = tratamentoInternacao.get(tratamento);
			}
		}
		
		return internacoes;
	}
	
	/**
	 * Ordena o detalhamento do relatório por prestador ou por internação aberta ou por internação fechada.
	 * @param resumoInternacoesPorTipoTratamento
	 * @param ordem
	 */
	private void ordenarDetalhamneto(List<ResumoInternacoesPorTratamento> resumoInternacoesPorTipoTratamento, String ordem){
		if (ordem.equals(POR_PRESTADOR)){
			Collections.sort(resumoInternacoesPorTipoTratamento, new Comparator<ResumoInternacoesPorTratamento>(){
				public int compare(ResumoInternacoesPorTratamento a, ResumoInternacoesPorTratamento b) {
					ResumoInternacoesPorTratamento obja = a;
					ResumoInternacoesPorTratamento objb = b;
					return obja.getPrestador().compareTo(objb.getPrestador());
				}
			});
		}
		
		if (ordem.equals(POR_INTERNACAO_ABERTA)){
			Collections.sort(resumoInternacoesPorTipoTratamento, new Comparator<ResumoInternacoesPorTratamento>(){
				public int compare(ResumoInternacoesPorTratamento a, ResumoInternacoesPorTratamento b) {
					ResumoInternacoesPorTratamento obja = a;
					ResumoInternacoesPorTratamento objb = b;
					return objb.getTotalAberto().compareTo(obja.getTotalAberto());
				}
			});
		}
		
		if (ordem.equals(POR_INTERNACAO_FECHADA)){
			Collections.sort(resumoInternacoesPorTipoTratamento, new Comparator<ResumoInternacoesPorTratamento>(){
				public int compare(ResumoInternacoesPorTratamento a, ResumoInternacoesPorTratamento b) {
					ResumoInternacoesPorTratamento obja = a;
					ResumoInternacoesPorTratamento objb = b;
					return objb.getTotalFechado().compareTo(obja.getTotalFechado());
				}
			});
		}
	}
	
	/**
	 * Valida o intervalo para geração do relatório de internações por prestador.
	 * O intervalo máximo de pesquisa é 01(um) mês.
	 * @param dataInicial
	 * @param dataFinal
	 * @throws ValidateException
	 */
	private void validarIntervalo(Date dataInicial, Date dataFinal) throws ValidateException{
		Calendar inicio = new GregorianCalendar();
		inicio.setTime(dataInicial);
		
		Calendar termino = new GregorianCalendar();
		termino.setTime(dataFinal);

		if(Utils.compareData(inicio.getTime(), termino.getTime()) > 0){
			throw new ValidateException("A data inicial não pode ser maior que a data final.");
		}
		
		if(Utils.diferencaEmDias(inicio, termino) > 30){
			throw new ValidateException("O intervalo entre a data inicial e a data final não pode ser maior que 30 dias.");
		}
	}
	
	/**
	 * Valida o tipo de internação selecionada pelo usuário.
	 * É permitido somente internação eletiva e de urgência.
	 * @param tipoInternacao
	 * @throws ValidateException
	 */
	private void validarTipoInternacao(String tipoInternacao) throws ValidateException{
		if (tipoInternacao == null || tipoInternacao.equals("")){
			throw new ValidateException("Tipo Nulo ou Vazio");
		}
		
		if (!tipoInternacao.equals(TIPO_INTERNACAO_ELETIVA) && !tipoInternacao.equals(TIPO_INTERNACAO_URGENCIA)){
			throw new ValidateException("Tipo de Internação Inválido");
		}
	}

	public int getQtdePrestadores() {
		return qtdePrestadores;
	}

	public void setQtdePrestadores(int qtdePrestadores) {
		this.qtdePrestadores = qtdePrestadores;
	}

	public String getTipoInternacao() {
		return tipoInternacao;
	}

	public void setTipoInternacao(String tipoInternacao) {
		this.tipoInternacao = tipoInternacao;
	}

	public int getQtdeInternacoesAutorizadas() {
		return qtdeInternacoesAutorizadas;
	}

	public void setQtdeInternacoesAutorizadas(int qtdeInternacoesAutorizadas) {
		this.qtdeInternacoesAutorizadas = qtdeInternacoesAutorizadas;
	}

	public int getQtdeInternacoesAbertas() {
		return qtdeInternacoesAbertas;
	}

	public void setQtdeInternacoesAbertas(int qtdeInternacoesAbertas) {
		this.qtdeInternacoesAbertas = qtdeInternacoesAbertas;
	}

	public int getQtdeInternacoesFechadas() {
		return qtdeInternacoesFechadas;
	}

	public void setQtdeInternacoesFechadas(int qtdeInternacoesFechadas) {
		this.qtdeInternacoesFechadas = qtdeInternacoesFechadas;
	}

	public List<ResumoInternacoesPorTratamento> getResumoInternacoesPorTratamento() {
		return resumoInternacoesPorTratamento;
	}

	public void setResumoInternacoesPorTratamento(List<ResumoInternacoesPorTratamento> resumoInternacoesPorTratamento) {
		this.resumoInternacoesPorTratamento = resumoInternacoesPorTratamento;
	}

}
