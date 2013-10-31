package br.com.infoway.ecare.services.tarefasCorrecao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.SQLQuery;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

public class BuscaDeGuiasComProblemasNoFechamentoParcial {


	private static List<GuiaSimples<ProcedimentoInterface>> guiasProcedimentos;
	private static List<GuiaSimples<ProcedimentoInterface>> guiasOrigemDasGHM;
	
	public static List getGuiasComProblemasNoFechamentoParcial() {
		String queryGuiaProcedimentos = "select idguia from atendimento_guiasimples where idguia " +
				"in (select idguiaorigem from atendimento_guiasimples where idguia " +
					"in ( select honorarios.idguia from atendimento_honorario as honorarios, procedimentos_procedimento as " +
							"procedimentos where honorarios.idprocedimentohonorarioexterno = procedimentos.idprocedimento))" +
					" AND tipodeguia <> 'GEX';";

		String queryGuiaOrigemGHM = "select idguia from atendimento_guiasimples where idguia in (select procedimentos.idguia from atendimento_honorario as " +
				"honorarios, procedimentos_procedimento as procedimentos where honorarios.idprocedimentohonorarioexterno = procedimentos.idprocedimento) AND tipodeguia <> 'GEX';";
		
		SQLQuery sqlQueryGuiaProcedimentos =  HibernateUtil.currentSession().createSQLQuery(queryGuiaProcedimentos);
		SQLQuery sqlQueryGuiaOrigemGHM =  HibernateUtil.currentSession().createSQLQuery(queryGuiaOrigemGHM);
		
		guiasProcedimentos = sqlQueryGuiaProcedimentos.list();
		guiasOrigemDasGHM = sqlQueryGuiaOrigemGHM.list();
		
		List guiasComReferenciasErradas = (List) CollectionUtils.subtract(guiasOrigemDasGHM, guiasProcedimentos);
		
		return guiasComReferenciasErradas;
		
	}
	
	public static void main(String[] args) {

		List guiasComReferenciasErradas = getGuiasComProblemasNoFechamentoParcial();
		System.out.println("[A] Qnt Guias referenciadas pelos procedimentos: " + guiasProcedimentos.size());
		System.out.println("[B] Qnt Guias referenciadas pelas GHM: " + guiasOrigemDasGHM.size());
		System.out.println("[B - A] Guias com referencias erradas: " + guiasComReferenciasErradas.size());
		
		SearchAgent sa;
		
		System.out.println("\n\n");
		for (Object idGuia : guiasComReferenciasErradas) {
			sa = new SearchAgent();
			sa.addParameter(new Equals("idGuia", Long.parseLong(idGuia.toString())));
			GuiaSimples<ProcedimentoInterface> guia = sa.uniqueResult(GuiaSimples.class);
			System.out.println(guia.getAutorizacao());
			for (GuiaHonorarioMedico ghm : guia.getGuiasFilhasDeHonorarioMedico()) {
				System.out.println("idGHM : " + ghm.getIdGuia());
				System.out.println("Guia origem da ghm: " + ghm.getGuiaOrigem().getAutorizacao());
				for (ProcedimentoInterface procedimento : ghm.getProcedimentos()) {
					System.out.println("Guia origem procedimento: " + procedimento.getGuia().getAutorizacao());
					for (GuiaSimples<ProcedimentoInterface> g : guia.getGuiasFilhas()) {
						if (g.getAutorizacao().equals(procedimento.getGuia().getAutorizacao())) {
							System.out.println("o procedimento pertence a uma guia filha! [" + g.getAutorizacao() + "]");
							break;
						}
					}
				}
				System.out.println("Qt. honorarios : " + ghm.getHonorarios().size());
				System.out.println("\n");
			}
		}
		
	}
	
}
