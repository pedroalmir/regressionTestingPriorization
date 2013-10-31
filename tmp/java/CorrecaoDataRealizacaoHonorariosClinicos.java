package br.com.infowaypi.ecare.correcaomanual;

import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.IsNull;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe de corre��o dos procedimentos quanto � data de realiza��o, pois os procedimentos honor�rios n�o 
 * tinham data de realiza��o antes da altera��o na sist�mica de glosa dos honor�rios cl�nicos.
 * @author Luciano Rocha
 * @since 16/01/2013
 */
public class CorrecaoDataRealizacaoHonorariosClinicos {

	public static void main(String[] args) {
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		Usuario usuario = ImplDAO.findById(1L, Usuario.class);

		SearchAgent sa = new SearchAgent();

		sa.addParameter(new IsNull("dataRealizacao"));
		sa.addParameter(new GreaterEquals("situacao.dataSituacao", Utils.parse("01/05/2012")));
		
		List<ProcedimentoOutros> procedimentos = sa.list(ProcedimentoOutros.class);
		
		System.out.println("Qnt proc: " + procedimentos.size());
		for (ProcedimentoOutros proc : procedimentos) {
			System.out.println("Procedimento: " + proc.getIdProcedimento());
			System.out.println("Data atual: " + proc.getDataRealizacao());
			if (proc.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null) {
				proc.setDataRealizacao(proc.getSituacao(SituacaoEnum.AUTORIZADO.descricao()).getDataSituacao());
			}
			else if (proc.getSituacao(SituacaoEnum.SOLICITADO.descricao()) != null) {
				proc.setDataRealizacao(proc.getSituacao(SituacaoEnum.SOLICITADO.descricao()).getDataSituacao());
			}
			else if (proc.getSituacao().getDataSituacao() != null){
				proc.setDataRealizacao(proc.getSituacao().getDataSituacao());
			}
			System.out.println("Data depois: " + proc.getDataRealizacao());
			try {
				ImplDAO.save(proc);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		tx.commit();
	}
}
