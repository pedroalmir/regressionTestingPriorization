package br.com.infoway.ecare.services.tarefasCorrecao;

import java.util.List;
import java.util.Set;

import org.hibernate.FlushMode;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.honorario.GuiaHonorarioMedico;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.TransactionManagerHibernate;
import br.com.infowaypi.molecular.parameter.Equals;

public class CorrecaoGuiaDeHonorariosParaParcialCorreta {

	private static SearchAgent sa;
	
	public static void corrigeGuia(String autorizacao) throws Exception {
		sa = new SearchAgent();
		sa.addParameter(new Equals("idGuia", Long.parseLong(autorizacao)));
		GuiaSimples guia = sa.uniqueResult(GuiaSimples.class);
		System.out.println("Autorização:" + guia.getAutorizacao());
		for (Procedimento procedimento : (Set<Procedimento>) guia.getProcedimentos()) {
			for (HonorarioExterno honorario : procedimento.getHonorariosGuiaHonorarioMedico()) {
				GuiaSimples guiaCorreta = procedimento.getGuia();
				GuiaHonorarioMedico ghm = honorario.getGuiaHonorario();
				guia.getGuiasFilhas().remove(ghm);
				guiaCorreta.getGuiasFilhas().add(ghm);
				ghm.setGuiaOrigem(guiaCorreta);
				ImplDAO.save(guia);
				ImplDAO.save(guiaCorreta);
				ImplDAO.save(ghm);
				if (ghm.getGuiaOrigem().equals(guiaCorreta)) {
					System.out.println("funfou!");
				}
				
				System.out.println("\tGuia do Honorario: " + ghm.getGuiaOrigem().getAutorizacao());
				System.out.println("\tSituação da GHM: " + ghm.getSituacao().getDescricao());
				ImplDAO.save(guia);
			}
//			System.out.println(procedimento.getHonorariosGuiaHonorarioMedico().size());
		}
	}
	
	public static void main(String[] args) throws Exception {
		List guiasComProblemas = BuscaDeGuiasComProblemasNoFechamentoParcial.getGuiasComProblemasNoFechamentoParcial();
		TransactionManagerHibernate tmh = new TransactionManagerHibernate();
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		HibernateUtil.currentSession().clear();
		tmh.beginTransaction();
		
		for (Object autorizacao : guiasComProblemas) {
			corrigeGuia(autorizacao.toString());
			HibernateUtil.currentSession().flush();
			HibernateUtil.currentSession().clear();
		}
		tmh.commit();
	}
	
}
