package br.com.infowaypi.ecare.services;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.LoteDeGuias;
import br.com.infowaypi.ecarebc.atendimentos.ResumoLotes;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioDeLotes {

	public ResumoLotes gerarRelatorio(Date competencia, Prestador prestador) {

		Criteria criteria = HibernateUtil.currentSession().createCriteria(
				LoteDeGuias.class);
		criteria.add(Expression.eq("competencia", competencia));
		if (prestador != null) {
			criteria.add(Expression.eq("prestador", prestador));
		}
		List<LoteDeGuias> lotes = criteria.list();
		Utils.sort(lotes, "dataEnvio");

		return new ResumoLotes(competencia, prestador, lotes);

	}

}
