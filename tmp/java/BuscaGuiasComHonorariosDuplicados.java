package br.com.infoway.ecare.services.tarefasCorrecao;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

public class BuscaGuiasComHonorariosDuplicados {

//	private static Transaction transaction = HibernateUtil.currentSession().getTransaction();
	private static Set<GuiaCompleta<ProcedimentoInterface>> guias = new HashSet<GuiaCompleta<ProcedimentoInterface>>();
	private static List<ItemGuiaFaturamento> igfs = null;

	public static List<ItemGuiaFaturamento> buscarIGF() {
		System.out.println("[BuscaGuiasComHonorariosDuplicados][" + new Date() + "] Buscando IGFs...");
		String query = "Select igf from " +
		"GuiaSimples as g, " +
		"Procedimento as p, " +
		"ItemGuiaFaturamento as igf " +
		"where " +
		"g = p.guia and " +
		"g = igf.guia and " +
		"p = igf.procedimento and " +
		"igf.grauParticipacao = 0 and " +
		"(p.situacao.descricao = 'Honorário Gerado' or " +
		"p.situacao.descricao = 'Aguardando cobrança') and " +
		"g.situacao.descricao = 'Auditado(a)' ";
		
		Query hql = HibernateUtil.currentSession().createQuery(query);
		List<ItemGuiaFaturamento> igf = hql.list();
		SearchAgent sa = new SearchAgent();
		for (ItemGuiaFaturamento item : igf ) {
			sa.addParameter(new Equals("autorizacao", item.getGuia().getAutorizacao()));
			guias.add(sa.uniqueResult(GuiaCompleta.class));
			sa.clearAllParameters();
		}
		System.out.println("[BuscaGuiasComHonorariosDuplicados][" + new Date() + "] Foram encontradas " + guias.size() + " guias com problemas. (" + igf.size() + " IGFs)");
		return igf;
	}
	
	public static void corrigeGuias() throws Exception {
		igfs = buscarIGF();
		System.out.println("[BuscaGuiasComHonorariosDuplicados][" + new Date() + "] Removendo IGFs e recalculando guias...");
		
		for (GuiaCompleta<ProcedimentoInterface> guia : guias) {
			System.out.println("[BuscaGuiasComHonorariosDuplicados][" + new Date() + "] Verificando guia " + guia.getAutorizacao());
			Iterator<ItemGuiaFaturamento> itensGuiaFaturamento = guia.getItensGuiaFaturamento().iterator();
			while (itensGuiaFaturamento.hasNext()) {
				ItemGuiaFaturamento igf = itensGuiaFaturamento.next();
				if (igfs.contains(igf)) {
					System.out.println("[BuscaGuiasComHonorariosDuplicados][" + new Date() + "] Removendo IGF " + igf.getIdItemGuiaFaturamento());
//					transaction.begin();
						itensGuiaFaturamento.remove();
						ImplDAO.delete(igf);
						guia.recalcularValores();
						guia.tocarObjetos();
//						ImplDAO.save(guia);
//					transaction.commit();
				}
			}
		}
		
	}
	
	
	public static void main(String[] args) {
//		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		try {
			corrigeGuias();
			System.out.println("[BuscaGuiasComHonorariosDuplicados][" + new Date() + "] IGFs removidos: ");
			for (ItemGuiaFaturamento igf : igfs) {
				System.out.println(igf.getIdItemGuiaFaturamento());
			}
			System.out.println("[BuscaGuiasComHonorariosDuplicados][" + new Date() + "] Guias recalculadas: ");
			for (GuiaFaturavel guia : guias) {
				System.out.println(guia.getAutorizacao());
			}
			
		} catch (Exception e) {
			System.out.println("[BuscaGuiasComHonorariosDuplicados][" + new Date() + "] Um erro ocorreu. Rolling back... ");
//			transaction.rollback();
			e.printStackTrace();
		}
	}
	
}
