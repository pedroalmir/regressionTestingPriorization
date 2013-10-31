package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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

public class RelatorioInternacoesPorMorbidade {
	public static final String TIPO_INTERNACAO_ELETIVA = "IEL";
	public static final String TIPO_INTERNACAO_URGENCIA = "IUR";
	
	public static final String TIPO_CID = "CID";
	public static final String TIPO_PROCEDIMENTO = "PROC";
	
	private List<ResumoInternacoesPorMorbidade> resumoInternacoesPorMorbidade;

	public RelatorioInternacoesPorMorbidade() {
		this.resumoInternacoesPorMorbidade = new ArrayList<ResumoInternacoesPorMorbidade>();
	}
	
	public RelatorioInternacoesPorMorbidade gerarRelatorio(Date competencia, String tipoInternacao, Integer tipoTratamento, String tipoMorbidade) throws ValidateException{
		this.validarParametros(competencia, tipoInternacao, tipoTratamento, tipoMorbidade);
		
		List<GuiaCompleta> internacoes = this.buscarInternacoes(competencia, tipoInternacao, tipoTratamento);
		
		Map<Object, Map<Prestador, Set<GuiaCompleta>>> mapaInternacoes = this.agruparInternacoes(tipoMorbidade, internacoes);
		
		List<ResumoInternacoesPorMorbidade> resumoTemporario = new ArrayList<ResumoInternacoesPorMorbidade>();
		for (Object morbidade : this.getMorbidades(mapaInternacoes)) {
			List<ResumoPrestadorInternacoes> prestadoresInternacoes = new ArrayList<ResumoPrestadorInternacoes>();
			
			for (Prestador prestador : this.getPrestadoresMorbidade(morbidade, mapaInternacoes)) {
				prestadoresInternacoes.add(new ResumoPrestadorInternacoes(prestador, this.getInternacoesPrestadorMorbidade(morbidade, prestador, mapaInternacoes).size()));
			}
			
			resumoTemporario.add(new ResumoInternacoesPorMorbidade(morbidade, this.getInternacoesMorbidade(morbidade, mapaInternacoes).size(), prestadoresInternacoes));
		}
		
		if (resumoTemporario.size() >= 20){
			this.resumoInternacoesPorMorbidade = this.ordenarResumo(resumoTemporario).subList(0, 20);
		} else {
			this.resumoInternacoesPorMorbidade = this.ordenarResumo(resumoTemporario);
		}
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	private List<GuiaCompleta> buscarInternacoes(Date competencia, String tipoInternacao, int tipoTratamento){
		GregorianCalendar dataInicial = new GregorianCalendar();
		dataInicial.setTime(competencia);
		
		GregorianCalendar dataFinal = new GregorianCalendar();
		dataFinal.setTime(competencia);
		dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaCompleta.class);
		criteria.add(Expression.eq("tipoDeGuia", tipoInternacao));
		criteria.add(Expression.eq("tipoTratamento", tipoTratamento));
		criteria.add(Expression.between("dataAtendimento", dataInicial.getTime(), dataFinal.getTime()));
		criteria.add(Expression.or(Expression.eq("situacao.descricao", SituacaoEnum.FECHADO.descricao()), Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao())));
		
		return criteria.list();
	}
	
	private Map<Object, Map<Prestador, Set<GuiaCompleta>>> agruparInternacoes(String tipoMorbidade, List<GuiaCompleta> internacoes){
		Map<Object, Map<Prestador, Set<GuiaCompleta>>> morbidadePrestadorInternacao = new HashMap<Object, Map<Prestador,Set<GuiaCompleta>>>();

		if (tipoMorbidade.equals(TIPO_CID)){
			for (GuiaCompleta internacao : internacoes) {
				Collection morbidades = internacao.getCids();
				this.povoarMapa(morbidades, internacao, morbidadePrestadorInternacao);
			}
		}
		
		if (tipoMorbidade.equals(TIPO_PROCEDIMENTO)){
			for (GuiaCompleta internacao : internacoes) {
				Collection morbidades = internacao.getProcedimentosDaTabelaCBHPM();
				this.povoarMapa(morbidades, internacao, morbidadePrestadorInternacao);
			}
		}
		
		return morbidadePrestadorInternacao;
	}
		
	private void povoarMapa(Collection morbidades, GuiaCompleta internacao, Map<Object, Map<Prestador, Set<GuiaCompleta>>> morbidadePrestadorInternacao){
		for (Object morbidade : morbidades) {
			if (morbidadePrestadorInternacao.containsKey(morbidade)) {
				Map<Prestador, Set<GuiaCompleta>> mapaInterno = povoarMapaInterno(internacao, morbidadePrestadorInternacao.get(morbidade));
				morbidadePrestadorInternacao.put(morbidade, mapaInterno);
			} else {
				Map<Prestador, Set<GuiaCompleta>> mapaInterno = povoarMapaInterno(internacao, new HashMap<Prestador, Set<GuiaCompleta>>());
				morbidadePrestadorInternacao.put(morbidade, mapaInterno);
			}
		}
	}
	
	private Map<Prestador, Set<GuiaCompleta>> povoarMapaInterno(GuiaCompleta internacao, Map<Prestador, Set<GuiaCompleta>> mapaInterno){
		Prestador prestador = internacao.getPrestador();
		if (mapaInterno.containsKey(prestador)){
			Set<GuiaCompleta> internacaoInterna = mapaInterno.get(prestador);
			internacaoInterna.add(internacao);
			mapaInterno.put(prestador, internacaoInterna);
		} else {
			Set<GuiaCompleta> internacaoInterna = new HashSet<GuiaCompleta>();
			internacaoInterna.add(internacao);
			mapaInterno.put(prestador, internacaoInterna);
		}

		return mapaInterno;
	}
	
	private Set<Object> getMorbidades(Map<Object, Map<Prestador, Set<GuiaCompleta>>> morbidadePrestadorInternacao){
		return morbidadePrestadorInternacao.keySet();
	}
	
	private Set<Prestador> getPrestadoresMorbidade(Object morbidade, Map<Object, Map<Prestador, Set<GuiaCompleta>>> morbidadePrestadorInternacao){
		Set<Prestador> prestadores = new HashSet<Prestador>();
		
		if (morbidadePrestadorInternacao.containsKey(morbidade)){
			Map<Prestador, Set<GuiaCompleta>> prestadorInternacao = morbidadePrestadorInternacao.get(morbidade);
			prestadores = prestadorInternacao.keySet();
		}
		
		return prestadores;
	}
	
	private Set<GuiaCompleta> getInternacoesMorbidade(Object morbidade, Map<Object, Map<Prestador, Set<GuiaCompleta>>> morbidadePrestadorInternacao){
		Set<GuiaCompleta> internacoes = new HashSet<GuiaCompleta>();
		
		for (Prestador prestador : this.getPrestadoresMorbidade(morbidade, morbidadePrestadorInternacao)) {
			internacoes.addAll(this.getInternacoesPrestadorMorbidade(morbidade, prestador, morbidadePrestadorInternacao));
		}
		
		return internacoes;
	}
	
	private Set<GuiaCompleta> getInternacoesPrestadorMorbidade(Object morbidade, Prestador prestador, Map<Object, Map<Prestador, Set<GuiaCompleta>>> morbidadePrestadorInternacao){
		Set<GuiaCompleta> internacoes = new HashSet<GuiaCompleta>();
		
		if (morbidadePrestadorInternacao.containsKey(morbidade)){
			Map<Prestador, Set<GuiaCompleta>> prestadorInternacao = morbidadePrestadorInternacao.get(morbidade);
			if (prestadorInternacao.containsKey(prestador)){
				internacoes = prestadorInternacao.get(prestador);
			}
		}
		
		return internacoes;
	}
	
	private void validarParametros(Date competencia, String tipoInternacao, int tipoTratamento, String tipoMorbidade) throws ValidateException{
		if (competencia == null){
			throw new ValidateException("Competência Nula.");
		}
		
		if (!tipoInternacao.equals(TIPO_INTERNACAO_ELETIVA) && !tipoInternacao.equals(TIPO_INTERNACAO_URGENCIA)){
			throw new ValidateException("Tipo de Internação Inválido.");
		}
		
		if (tipoTratamento != GuiaCompleta.TRATAMENTO_CLINICO && tipoTratamento != GuiaCompleta.TRATAMENTO_CIRURGICO){
			throw new ValidateException("Tipo de Tratamento Inválido.");
		}
		
		if (!tipoMorbidade.equals(TIPO_CID) && !tipoMorbidade.equals(TIPO_PROCEDIMENTO)){
			throw new ValidateException("Tipo de Morbidade Inválido.");
		}
	}
	
	private List<ResumoInternacoesPorMorbidade> ordenarResumo(List<ResumoInternacoesPorMorbidade> resumoInternacoesPorMorbidade){
		Collections.sort(resumoInternacoesPorMorbidade, new Comparator<ResumoInternacoesPorMorbidade>(){
			public int compare(ResumoInternacoesPorMorbidade a, ResumoInternacoesPorMorbidade b) {
				ResumoInternacoesPorMorbidade obja = a;
				ResumoInternacoesPorMorbidade objb = b;
				return objb.getQtdeInternacoes().compareTo(obja.getQtdeInternacoes());
			}
		});
		
		return resumoInternacoesPorMorbidade;
	}

	public List<ResumoInternacoesPorMorbidade> getResumoInternacoesPorMorbidade() {
		return resumoInternacoesPorMorbidade;
	}

	public void setResumoInternacoesPorMorbidade(
			List<ResumoInternacoesPorMorbidade> resumoInternacoesPorMorbidade) {
		this.resumoInternacoesPorMorbidade = resumoInternacoesPorMorbidade;
	}

}
