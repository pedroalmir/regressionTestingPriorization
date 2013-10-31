package br.com.infowaypi.ecarebc.service.financeiro;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.financeiro.Banco;
import edu.emory.mathcs.backport.java.util.Arrays;

@SuppressWarnings("unchecked")
public class FaturamentoDAO {

	public static List<GuiaFaturavel> buscarGuias(Date competenciaBase, Date dataRecebimento, Set<Prestador> prestadoresIgnorados) {
		Session session = HibernateUtil.currentSession();
		Transaction t = session.beginTransaction();
		session.setFlushMode(FlushMode.COMMIT);
		List<GuiaFaturavel> auditadas = buscarGuiasAuditadas(competenciaBase, dataRecebimento, prestadoresIgnorados, session);
		List<GuiaFaturavel> confirmadas = buscarGuiasConfirmadas(competenciaBase, dataRecebimento, prestadoresIgnorados, session);
		List<GuiaFaturavel> guiasDeRecursoDeGlosa = buscarGuiasDeRecursoDeGlosa(prestadoresIgnorados, session);
		
		List<GuiaFaturavel> todasAsGuias = new ArrayList<GuiaFaturavel>();
		todasAsGuias.addAll(confirmadas);
		todasAsGuias.addAll(auditadas);
		todasAsGuias.addAll(guiasDeRecursoDeGlosa);
		
//		t.rollback();
		
		return todasAsGuias;
	}	
		
	private static List<GuiaFaturavel> buscarGuiasAuditadas(Date competenciaBase, Date dataRecebimento,
															Set<Prestador> prestadoresIgnorados, Session session) {		
		
		Criteria criteria = createCriteriaGuias(SituacaoEnum.AUDITADO.descricao(), competenciaBase, prestadoresIgnorados, session);

		criteria.add(Expression.ge("dataTerminoAtendimento", CompetenciaUtils.getInicioCompetencia(competenciaBase)));
		criteria.add(Expression.le("dataTerminoAtendimento", CompetenciaUtils.getFimCompetencia(competenciaBase)));
		criteria.add(Expression.le("dataRecebimento", dataRecebimento));
		
		return criteria.list();
	}
	
	private static List<GuiaFaturavel> buscarGuiasDeRecursoDeGlosa(Set<Prestador> prestadoresIgnorados, Session session) {

		List<String> situacoes = Arrays.asList(new String[]{SituacaoEnum.DEFERIDO.descricao(), SituacaoEnum.INDEFERIDO.descricao()});
		
		Criteria criteria = session.createCriteria(GuiaRecursoGlosa.class);
		criteria.add(Expression.in("situacao.descricao", situacoes));
		
		return criteria.list();
	}
	
	private static List<GuiaFaturavel> buscarGuiasConfirmadas(
			Date competenciaBase, Date dataRecebimento,
			Set<Prestador> prestadoresIgnorados, Session session) {

		Criteria criteria = createCriteriaGuias(SituacaoEnum.CONFIRMADO.descricao(), competenciaBase, prestadoresIgnorados, session);
		
		criteria.add(Expression.ge("dataTerminoAtendimento", CompetenciaUtils.getInicioCompetencia(competenciaBase)));
		criteria.add(Expression.le("dataTerminoAtendimento", CompetenciaUtils.getFimCompetencia(competenciaBase)));
		criteria.add(Expression.le("situacao.dataSituacao", dataRecebimento));

		return criteria.list();
	}
	
	private static Criteria createCriteriaGuias(String descricao, Date competenciaBase, Set<Prestador> prestadoresIgnorados, Session session){
		Criteria criteria = session.createCriteria(GuiaFaturavel.class);
		criteria.setLockMode(LockMode.WRITE);

		criteria.add(Expression.isNull("faturamento"));
		
		criteria.add(Expression.eq("situacao.descricao", descricao));
		
		if (prestadoresIgnorados != null && !prestadoresIgnorados.isEmpty()) {
			criteria.add(Expression.not(Expression.in("prestador", prestadoresIgnorados)));
		}

		criteria.setFetchMode("itensGuiaFaturamento", FetchMode.JOIN);

		criteria.addOrder(Order.asc("dataTerminoAtendimento"));
		
		return criteria;
	}
	
	public static List<Faturamento> buscarFaturamentos(Date competencia, Banco banco, boolean incluirFechados) throws ValidateException {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("competencia", competencia));
		
		SearchAgent saPrest = new SearchAgent(); 
		if (banco != null){
			saPrest.addParameter(new Equals("informacaoFinanceira.banco", banco));
		}
		
		List<Prestador> prestadores = saPrest.list(Prestador.class);
		sa.addParameter(new In("prestador", prestadores));
		sa.addParameter(new NotEquals("status", Constantes.FATURAMENTO_CANCELADO));
		
		if (!incluirFechados){
			sa.addParameter(new NotEquals("status", Constantes.FATURAMENTO_FECHADO));
		}

		List<Faturamento> faturasGeradas = sa.list(AbstractFaturamento.class);
		return faturasGeradas;
	}

	public static List<GuiaFaturavel> buscarGuiasFaturamentoPassivo(Date competencia, Date dataGeracaoPlanilha,
			Set<Prestador> prestadoresIgnorados) {
		
		Session session = HibernateUtil.currentSession();
		Transaction t = session.beginTransaction();
		session.setFlushMode(FlushMode.COMMIT);
		
		List<GuiaFaturavel> auditadas = buscarGuiasAuditadasPassivo(competencia, dataGeracaoPlanilha, prestadoresIgnorados, session);
		List<GuiaFaturavel> confirmadas = buscarGuiasConfirmadasPassivo(competencia, dataGeracaoPlanilha, prestadoresIgnorados, session);
		List<GuiaFaturavel> deferidas = buscarGuiasDeferidasPassivo(prestadoresIgnorados, session);
		
		List<GuiaFaturavel> todasAsGuias = new ArrayList<GuiaFaturavel>();
		todasAsGuias.addAll(confirmadas);
		todasAsGuias.addAll(auditadas);
		todasAsGuias.addAll(deferidas);
		
//		t.rollback();
		
		return todasAsGuias;
	}
	
	private static List<GuiaFaturavel> buscarGuiasConfirmadasPassivo(
			Date competenciaBase, Date dataRecebimento,
			Set<Prestador> prestadoresIgnorados, Session session) {

		Criteria criteria = createCriteriaGuias(SituacaoEnum.CONFIRMADO.descricao(), competenciaBase, prestadoresIgnorados, session);
		
		criteria.add(Expression.lt("dataTerminoAtendimento", CompetenciaUtils.getInicioCompetencia(competenciaBase)));
		criteria.add(Expression.le("situacao.dataSituacao", dataRecebimento));

		return criteria.list();
	}
	
	private static List<GuiaFaturavel> buscarGuiasAuditadasPassivo(
			Date competenciaBase, Date dataRecebimento,
			Set<Prestador> prestadoresIgnorados, Session session) {

		Criteria criteria = createCriteriaGuias(SituacaoEnum.AUDITADO.descricao(), competenciaBase, prestadoresIgnorados, session);
		
		criteria.add(Expression.lt("dataTerminoAtendimento", CompetenciaUtils.getInicioCompetencia(competenciaBase)));
		criteria.add(Expression.le("dataRecebimento", dataRecebimento));

		return criteria.list();
	}
	
	private static List<GuiaFaturavel> buscarGuiasDeferidasPassivo(Set<Prestador> prestadoresIgnorados, Session session) {

		List<String> situacoes = Arrays.asList(new String[]{SituacaoEnum.DEFERIDO.descricao(), SituacaoEnum.INDEFERIDO.descricao()});
		
		Criteria criteria = session.createCriteria(GuiaRecursoGlosa.class);
		criteria.add(Expression.in("situacao.descricao", situacoes));

		return criteria.list();
	}
}
