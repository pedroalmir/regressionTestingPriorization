package br.com.infoway.ecare.services.tarefasCorrecao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;


public class CorrecaoVinculoGuiasGHM {

	private static Transaction transaction = HibernateUtil.currentSession().getTransaction();
	
	public static Map<GuiaSimples, Set<GuiaSimples>> buscarGHMs() throws Exception {
		String query =  "Select new list(ghm, guia) from " +
							"GuiaHonorarioMedico as ghm, " +
							"Procedimento as procedimento, " +
							"HonorarioExterno as honorario, " +
							"GuiaSimples as guia " +
						"where " +
							"procedimento.guia = guia and " +
							"honorario.guiaHonorario = ghm and " +
							"procedimento = honorario.procedimento and " +
							"ghm.guiaOrigem <> guia";
		
		Query hql = HibernateUtil.currentSession().createQuery(query);
		Map<GuiaSimples, Set<GuiaSimples>> map = new HashMap<GuiaSimples, Set<GuiaSimples>>();
		List<List<GuiaSimples>> ghms = hql.list();
		for (List<GuiaSimples> guias : ghms) {
			if (map.get(guias.get(0)) == null) {
				map.put(guias.get(0), new HashSet<GuiaSimples>());
			}
		}
		
		for (List<GuiaSimples> guias : ghms) {
			Set<GuiaSimples> guiasValor = map.get(guias.get(0));
			guiasValor.add(guias.get(1));
			map.put(guias.get(0), guiasValor);
		}

		return map;
	}
		
		
	public static void corrigeGHMs(Map<GuiaSimples, Set<GuiaSimples>> map) throws Exception {
		for (Entry<GuiaSimples, Set<GuiaSimples>> entry : map.entrySet()) {
			if (entry.getValue().size() == 1) {
				System.out.println("Corrigindo a guia: " + entry.getKey().getAutorizacao());
				GuiaSimples<ProcedimentoInterface> guiaOrigemIncorreta = entry.getKey().getGuiaOrigem();
				GuiaSimples<ProcedimentoInterface> guiaOrigemCorreta = null;
				for (GuiaSimples guia : entry.getValue()) {
					guiaOrigemCorreta = guia;
				}
				System.out.println("Mudando a referencia de " + guiaOrigemIncorreta.getAutorizacao() + " para " + guiaOrigemCorreta.getAutorizacao());
				/** Removendo referencias incorretas **/
				Set<GuiaSimples> guiasFilhas = new HashSet<GuiaSimples>();
				for (GuiaSimples guiaFilha : guiaOrigemIncorreta.getGuiasFilhas()) {
					if (!guiaFilha.getAutorizacao().equals(entry.getKey().getAutorizacao())) {
						guiasFilhas.add(guiaFilha);
					} else {
						guiaFilha.setGuiaOrigem(guiaOrigemCorreta);
						guiaOrigemCorreta.getGuiasFilhas().add(guiaFilha);
						ImplDAO.save(guiaFilha);
						ImplDAO.save(guiaOrigemCorreta);
					}
				}
				guiaOrigemIncorreta.setGuiasFilhas(guiasFilhas);
				ImplDAO.save(guiaOrigemIncorreta);
			} else {
				System.out.println("A seguinte GHM não foi alterada por conter mais de um vínculo: " + entry.getKey().getAutorizacao());
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			transaction.begin();
			corrigeGHMs(buscarGHMs());
//			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
