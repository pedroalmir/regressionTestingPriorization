package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.Usuario;

/**
 * Classe de correção de um honorário gerado para procedimento cancelado..
 * @author Luciano Rocha
 * @since 14/02/2013
 */
public class CorrecaoAuxiliarProcedimento {

	public static void main(String[] args) {
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new Equals("autorizacao", "1894193"));
		
		GuiaSimples guia = sa.uniqueResult(GuiaSimples.class);
		
		List<Honorario> honorarios = guia.getHonorarios();
		
		for (Honorario honorario : honorarios) {
			if (honorario.getProcedimento().getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao())) {
				System.out.println("Id do honorário alterado: " + honorario.getIdHonorario());
				honorario.mudarSituacao(ImplDAO.findById(1L, Usuario.class), SituacaoEnum.GLOSADO.descricao(), "Conformização de honorário gerado de procedimento cancelado.", new Date());
				try {
					ImplDAO.save(honorario);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		tx.commit();
	}
}
