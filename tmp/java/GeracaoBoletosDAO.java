package br.com.infowaypi.ecare.financeiro.boletos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.constantes.Constantes;
import br.com.infowaypi.ecare.enums.SituacaoCartaoEnum;
import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoSegurado;
import br.com.infowaypi.ecare.segurados.Cartao;
import br.com.infowaypi.ecare.segurados.DependenteSuplementar;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("unchecked")
public class GeracaoBoletosDAO {

	/**
	 * Fornece uma coleção com todos os titulares na situação "Ativo(a)"
	 * bem como os na situação "Cancelado(a)" que possuem coparticipação (dele e/ou de seus dependentes) a ser cobrada.
	 * @return Set<TitularFinanceiroSR>
	 */
	public static Set<TitularFinanceiroSR> buscarTitulares() {
		Set<TitularFinanceiroSR> titulares = new HashSet<TitularFinanceiroSR>();
		Criteria criteria = HibernateUtil.currentSession().createCriteria(TitularFinanceiroSR.class);
		criteria.createAlias("matriculas", "mats");			
		criteria.add(Expression.eq("mats.tipoPagamento", Constantes.BOLETO));
		criteria.add(Expression.eq("mats.ativa", Boolean.TRUE));
		criteria.add(Expression.eq("situacao.descricao",SituacaoEnum.ATIVO.descricao()));
		criteria.setFetchMode("titularOrigem", FetchMode.SELECT);
		criteria.setFetchMode("titular.titular.consultasPromocionais", FetchMode.JOIN);
		criteria.setFetchMode("titular.consultasPromocionais", FetchMode.JOIN);

//		TitularFinanceiroSR titular = ImplDAO.findById(533867L, TitularFinanceiroSR.class);
//		titulares.add(titular);
		
		titulares.addAll(criteria.list());
		
		return titulares;
	}
	
	/**
	 * Fornece uma coleção com todos os dependentes suplementares na situação "Ativo(a)"
	 * bem como os na situação "Cancelado(a)" que possuem coparticipação a ser cobrada.
	 * @return Set<DependenteSuplementar>
	 */
	public static Set<DependenteSuplementar> buscarDependentesSuplementares() {
		Set<DependenteSuplementar> dependentesSuplementaresSet = new HashSet<DependenteSuplementar>();
		
		@SuppressWarnings("unused")
		List<DependenteSuplementar> dependentesSuplementaresInList = new ArrayList<DependenteSuplementar>();
		
		Criteria criteriaAtivos = HibernateUtil.currentSession().createCriteria(DependenteSuplementar.class);
		criteriaAtivos.add(Expression.eq("situacao.descricao",SituacaoEnum.ATIVO.descricao()));
		criteriaAtivos.setFetchMode("titular.titular.consultasPromocionais", FetchMode.JOIN);
		criteriaAtivos.setFetchMode("titular.consultasPromocionais", FetchMode.JOIN);
		
		dependentesSuplementaresSet.addAll(criteriaAtivos.list());
		return dependentesSuplementaresSet;
	}
	
	/**
	 * Fornece os cartões que deverão ser cobrados.
	 * @return List<Cartao>
	 */
	public static List<Cartao> getCartoesParaCobranca() {
		List<Cartao> cartoesParaCobranca = HibernateUtil.currentSession().createCriteria(Cartao.class)
		.add(Expression.eq("situacao", SituacaoCartaoEnum.GERADO.getDescricao()))
		.add(Expression.gt("viaDoCartao", 1))
		.setFetchMode("segurado", FetchMode.JOIN)
		.list();
		return cartoesParaCobranca;
	}
	
	/**
	 * Busca-se guias para a geraçao de boletos considerando sempre a competencia anterior à competencia passada como parametro e os segurados.
	 * Ex: para geraçao de boletos da competencia 04/2010, busca-se guias da competencia 03/2010 (21/02/2010 à 20/03/2010) 
	 * @param segurados
	 * @param competencia competencia de geração dos boletos
	 * @return
	 */
		
	public static List<GuiaSimples> getGuiasParaCobranca(Set<Segurado> segurados, Date dataInicial, Date dataFinal) {

		String[] situacoes = {SituacaoEnum.CONFIRMADO.descricao(),
				SituacaoEnum.AUDITADO.descricao(),
				SituacaoEnum.FATURADA.descricao(), SituacaoEnum.PAGO.descricao()};
		
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);

		criteria.add(Expression.ge("dataAtendimento", dataInicial))
				.add(Expression.lt("dataAtendimento", dataFinal))
			.add(Expression.in("segurado",segurados))
			.add(Expression.isNull("fluxoFinanceiro"))
			.add(Expression.in("situacao.descricao",situacoes))
			.add(Expression.ne("valorCoParticipacao",BigDecimal.ZERO))
			.setFetchMode("prestador", FetchMode.SELECT)
			.setFetchMode("segurado", FetchMode.JOIN)
			.setFetchMode("segurado.consultasPromocionais", FetchMode.SELECT)
			.setFetchMode("segurado.titularOrigem", FetchMode.SELECT)
			.setFetchMode("segurado.pessoaFisica.endereco.municipio", FetchMode.SELECT)
			.setFetchMode("segurado.titular", FetchMode.SELECT);
		
		
		return criteria.list();
	}
	
	/**
	 * Fornece as cobranças dos segurados. Usada para verificar se o segurado em questão já possui uma cobrança gerada.
	 * @param competencia
	 * @param segurados
	 * @return List<Cobranca>
	 */
	public static List<Cobranca> getCobrancasNaoCanceladasDaCompetenciaInformada(Date competencia,Set<TitularFinanceiroSR> segurados) {
		
		List<String> situacoes = Arrays.asList(SituacaoEnum.ABERTO.descricao(),
												SituacaoEnum.PAGO.descricao());
		
		List<Cobranca> cobrancasDoMes = HibernateUtil.currentSession().createCriteria(Cobranca.class)
		.add(Expression.in("titular", segurados))
		.add(Expression.eq("competencia", competencia))
											.add(Expression.in("situacao.descricao", situacoes))
		.setFetchMode("titular", FetchMode.JOIN)
		.setFetchMode("titular.titular", FetchMode.JOIN)
		.list();
		
		return cobrancasDoMes;
	}
	
	/**
	 * Fornece as cobranças dos segurados. Usada para verificar se o segurado em questão já possui uma cobrança gerada.
	 * @param competencia
	 * @param segurados
	 * @return List<Cobranca>
	 */
	public static List<ConsignacaoSegurado> getConsignacoesDaCompetencia(Date competencia,Set<TitularFinanceiroSR> segurados) {
		
		List<ConsignacaoSegurado> consignacoesDoMes = HibernateUtil.currentSession().createCriteria(ConsignacaoSegurado.class)
		.add(Expression.in("titular", segurados))
		.add(Expression.eq("competencia", competencia))
		.add(Expression.in("statusConsignacao", Arrays.asList('A','P')))
		.list();
		
		return consignacoesDoMes;
	}
	
	/**
	 * Fornece os segurados que possuem cobranca paga na competencia anterior a informada, bem como os dependentes
	 * suplementares que não possuem nenhuma cobrança.
	 * @param competencia 
	 * @return List<TitularFinanceiroSR>
	 */
	public static List<TitularFinanceiroSR> getTitularesComCobrancasPagasDaCompentenciaAnterior(Date competencia) {
		
		Calendar competenciaAtual = Calendar.getInstance();
		competenciaAtual.setTime(competencia);
		Date competenciaAnterior = Utils.incrementaMes(competenciaAtual, -1);
		
		String queryTitularesComCobrancaNaCompetenciaAnterior = "select cobranca.titular from Cobranca cobranca where cobranca.situacao.descricao = 'Pago(a)' and competencia = :competencia";
		
		Query hqlQueryTitularesComCobrancaNaCompetenciaAnterior = HibernateUtil.currentSession().createQuery(queryTitularesComCobrancaNaCompetenciaAnterior);
		
		hqlQueryTitularesComCobrancaNaCompetenciaAnterior.setDate("competencia", competenciaAnterior);
		
		List<TitularFinanceiroSR> titulares = hqlQueryTitularesComCobrancaNaCompetenciaAnterior.list();
		
		String queryDependentesSuplementaresSemCobranca = "select s from DependenteSuplementar s where s.situacao.descricao = 'Ativo(a)'" +
		" and s.cobrancas is empty";
		Query hqlQueryDependentesSuplementaresSemCobranca = HibernateUtil.currentSession().createQuery(queryDependentesSuplementaresSemCobranca);
		titulares.addAll(hqlQueryDependentesSuplementaresSemCobranca.list());
		
		return titulares;
	}
	
	public static Date getUltimaCompetenciaComBoletoPago(TitularFinanceiroSR titular) {
		String hqlQuery = "select max(competencia) from Cobranca where descricao = 'Pago(a)' and titular = :titular";
		Query query = HibernateUtil.currentSession().createQuery(hqlQuery);
		query.setParameter("titular", titular);
		
		return (Date) query.uniqueResult();
	}
	
	public static Date getDataGeracaoUltimaRemessaProcessada() {
		String hqlQuery = "SELECT max(dataGeracao) FROM RemessaDeBoletos";
		Query query = HibernateUtil.currentSession().createQuery(hqlQuery);
		
		return (Date) query.uniqueResult();
	}
}
