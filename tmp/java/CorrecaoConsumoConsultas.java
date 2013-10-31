package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.consumo.Consumo;
import br.com.infowaypi.ecarebc.consumo.ConsumoInterface;
import br.com.infowaypi.ecarebc.consumo.periodos.Ano;
import br.com.infowaypi.ecarebc.consumo.periodos.Periodo;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;

@SuppressWarnings({"rawtypes","unchecked"})
public class CorrecaoConsumoConsultas {

	private static final Session CURRENT_SESSION = HibernateUtil.currentSession();
	private static final int MAX_RESULT = 9000;
	private static final int RESULTADOS_POR_BUSCA = 1000;

	public static void main(String[] args) throws Exception {

		Transaction tx = CURRENT_SESSION.beginTransaction();
		try {
			Date inicioDoAno = Ano.CORRENTE.getDataInicial();
			Date fimDoAno = Ano.CORRENTE.getDataFinal();
			int i = 0;
			while (i <= MAX_RESULT) {
				processarCorrecao(inicioDoAno, fimDoAno, i);
				i += RESULTADOS_POR_BUSCA;
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		}
	}

	private static void processarCorrecao(Date inicioDoAno, Date fimDoAno, int inicioDaBusca)
			throws Exception {
		List<GuiaSimples> guiasInaptas = buscarGuiasInaptas(inicioDoAno,
				fimDoAno, inicioDaBusca);
		System.out.println("Busca "+inicioDaBusca+": "+guiasInaptas.size()+" guias");
		Set<AbstractSegurado> segurados = new HashSet<AbstractSegurado>();;
		for (GuiaSimples guia : guiasInaptas) {
			segurados.add(guia.getSegurado());
		}
		System.out.println("Segurados: "+segurados.size());
		int i = 1;
		for (AbstractSegurado segurado: segurados) {
			System.out.println(i++); 
			List<ConsumoInterface> consumos = buscarConsumos(segurado);
			for (ConsumoInterface consumo : consumos) {
				Date dataInicial = consumo.getDataInicial();
				Date dataFinal = consumo.getDataFinal();
	
				Map<String, Integer> guiasPorSituacao = quantidadeDeGuias(segurado, dataInicial, dataFinal); 
				
				Integer agendadas = guiasPorSituacao.get(SituacaoEnum.AGENDADA.descricao());
				Integer confirmadas = guiasPorSituacao.get(SituacaoEnum.CONFIRMADO.descricao());
				Integer faturadas = guiasPorSituacao.get(SituacaoEnum.FATURADA.descricao());
				Integer pagas = guiasPorSituacao.get(SituacaoEnum.PAGO.descricao());
				Integer canceladas = guiasPorSituacao.get(SituacaoEnum.CANCELADO.descricao());
				Integer inaptas = guiasPorSituacao.get(SituacaoEnum.INAPTO.descricao());
				
				int consultasAgendadas = agendadas==null?0:agendadas;
				
				int consultasConfirmadas = confirmadas==null?0:confirmadas;
				consultasConfirmadas += faturadas==null?0:faturadas;
				consultasConfirmadas += pagas==null?0:pagas;
				
				int consultasCanceladas = canceladas==null?0:canceladas;
				consultasCanceladas += inaptas==null?0:inaptas;

				atualizarConsumo(consumo, consultasAgendadas,
						consultasConfirmadas, consultasCanceladas);
			}
			ImplDAO.save(segurado);
		}
	}

	private static List<GuiaSimples> buscarGuiasInaptas(Date inicioDoAno,
			Date fimDoAno, int inicioDaBusca) {
		List<GuiaSimples> guiasInaptas = CURRENT_SESSION
			.createCriteria(GuiaSimples.class)
			.add(Expression.eq("situacao.descricao", SituacaoEnum.INAPTO.descricao()))
			.add(Expression.between("situacao.dataSituacao", inicioDoAno, fimDoAno))
			.add(Expression.eq("tipoDeGuia", "GCS"))
			.setFetchMode("segurado", FetchMode.JOIN)
			.setFetchMode("profissional", FetchMode.SELECT)
			.setFetchMode("solicitante", FetchMode.SELECT)
			.setFetchMode("prestador", FetchMode.SELECT)
			.setFetchMode("especialidade", FetchMode.SELECT)
			.setFetchMode("fluxoFinanceiro", FetchMode.SELECT)
			.setFetchMode("ordenamento", FetchMode.SELECT)
			.setFetchMode("criticas", FetchMode.SELECT)
			.setFetchMode("faturamento", FetchMode.SELECT)
			.setFirstResult(inicioDaBusca)
			.setMaxResults(RESULTADOS_POR_BUSCA)
			.list();
		return guiasInaptas;
	}

	private static List<ConsumoInterface> buscarConsumos(
			AbstractSegurado segurado) {
		return CURRENT_SESSION.createCriteria(Consumo.class)
			.add(Expression.eq("colecaoConsumos", segurado.getConsumoIndividual()))
			.add(Expression.eq("tipoPeriodo", Periodo.ANUAL.getValor()))
			.add(Expression.between("dataInicial", Ano.CORRENTE.getDataInicial(), Ano.CORRENTE.getDataFinal()))
			.list();
	}
	
	private static Map<String, Integer> quantidadeDeGuias(AbstractSegurado segurado,
			Date dataInicial, Date dataFinal) {
		
		String hql = "SELECT situacao.descricao, count(*) FROM GuiaSimples WHERE liberadaForaDoLimite = 0 " +
				"AND tipoDeGuia = 'GCS' AND segurado = :segurado AND dataAtendimento " +
				"BETWEEN :dataInicial AND :dataFinal GROUP BY situacao.descricao";
		Query query = CURRENT_SESSION.createQuery(hql);
		query.setEntity("segurado", segurado);
		query.setDate("dataInicial", dataInicial);
		query.setDate("dataFinal", dataFinal);
		List<Object[]> list = query.list();

		Map<String, Integer> quantidadePorSituacao = new HashMap<String, Integer>();
		for (Object[] object : list) {
			String descricao = (String) object[0];
			Integer quantidade = Integer.valueOf(((Long) object[1]).toString());
			quantidadePorSituacao.put(descricao, quantidade);
		}
		
		return quantidadePorSituacao;
	}

	private static void atualizarConsumo(ConsumoInterface consumo,
			Integer consultasAgendadas, Integer consultasConfirmadas,
			int consultasCanceladas) throws Exception {

		consumo.setNumeroConsultasAgendadas(consultasAgendadas);
		consumo.setNumeroConsultasConfirmadas(consultasConfirmadas);
		consumo.setNumeroConsultasCanceladas(consultasCanceladas);
		
	}

}
