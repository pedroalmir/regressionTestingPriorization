package br.com.infowaypi.ecare.utils;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;

import br.com.infowaypi.molecular.HibernateUtil;

public class CriteriaUtils {
	
	public static Criteria getCriteriaSegurado(Class tipo){
		
		return HibernateUtil.currentSession().createCriteria(tipo)
		.setFetchMode("dependentes", FetchMode.SELECT)
		.setFetchMode("consumoIndividual", FetchMode.SELECT)
		.setFetchMode("guias", FetchMode.SELECT)
		.setFetchMode("odontograma", FetchMode.SELECT)
		.setFetchMode("informacaoFinanceira", FetchMode.SELECT)
		.setFetchMode("colecaoSituacoes", FetchMode.SELECT)
		.setFetchMode("consultasPromocionais", FetchMode.SELECT)
		.setFetchMode("consignacoes", FetchMode.SELECT)
		.setFetchMode("grupo", FetchMode.SELECT)
		.setFetchMode("situacaoCadastral", FetchMode.SELECT)
		.setFetchMode("secretaria", FetchMode.SELECT)
		.setFetchMode("grupo", FetchMode.SELECT)
		.setFetchMode("detalhePagamento", FetchMode.SELECT);
//		.setFetchMode("matriculas", FetchMode.SELECT);
	}
}
