/**
 * 
 */
package br.com.infowaypi.ecare.scheduller;

import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;

/**
 * Task Quartz responsavel por mudar a situação das consultas promocionais vencidas.
 * As consultas promocionais do tipo ELETIVA vencem em 30 dias após sua liberação.
 * As consultas promocionais do tipo URGENCIA vencem em 24 horas após sua liberação.
 * 
 * @author Marcus Quixabeira
 *
 */
public class TaskAtualizarConsultasPromocionais implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			Transaction tm = HibernateUtil.currentSession().beginTransaction();
			
			List<PromocaoConsulta> consultasPromocionais = HibernateUtil.currentSession().createCriteria(PromocaoConsulta.class)
				.add(Expression.eq("situacao.descricao", PromocaoConsulta.LIBERADO))
				.list();
			
			for (PromocaoConsulta promocaoConsulta : consultasPromocionais) {
				promocaoConsulta.isVencido();
				ImplDAO.save(promocaoConsulta);
			}
			
			tm.commit();
			HibernateUtil.currentSession().close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new JobExecutionException(e.getMessage());
		}
		
	}
	
	public static void main(String[] args) throws JobExecutionException {
		new TaskAtualizarConsultasPromocionais().execute(null);
	}

}
