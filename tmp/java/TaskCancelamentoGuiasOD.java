package br.com.infowaypi.ecare.scheduller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class TaskCancelamentoGuiasOD implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Transaction t = HibernateUtil.currentSession().beginTransaction();
		jobMethod();
		t.commit();
	}
	
	public static void main(String[] args) {
		jobMethod();
	}

	public static List<GuiaExameOdonto> buscarGuias() {
		String query = "" +
				"SELECT guia FROM GuiaExameOdonto as guia, " +
				"Procedimento as procedimento " +
				"WHERE " +
				"procedimento.guia = guia and " +
				"guia.situacao.descricao = :situacaoGuia and (" +
				"procedimento.situacao.descricao = :situacaoProcedimentoI or " +
				"procedimento.situacao.descricao = :situacaoProcedimentoII) and " +
				"guia.situacao.dataSituacao < :date";
		
		Query hql = HibernateUtil.currentSession().createQuery(query)
				.setString("situacaoGuia", SituacaoEnum.AUTORIZADO.descricao())
				.setString("situacaoProcedimentoI", SituacaoEnum.SOLICITADO.descricao())
				.setString("situacaoProcedimentoII", SituacaoEnum.AUTORIZADO.descricao())
				.setDate("date", Utils.incrementaDias(new Date(), -60));
		
		List<GuiaExameOdonto> retorno = new ArrayList<GuiaExameOdonto>();
		for (GuiaExameOdonto guia : (List<GuiaExameOdonto>) hql.list()) {
			if (guia.getProcedimentosRealizados().isEmpty()) {
				retorno.add(guia);
			}
		}
		
		System.out.println("[" + new Date() + "] Foram encontradas " + retorno.size() + " guias aptas ao cancelamento.");
		return retorno;
	}

	public static void jobMethod() {
		System.out.println("[" + new Date() + "] Buscando guias aptas ao cancelamento...");
		UsuarioInterface usuario = ImplDAO.findById(1L, Usuario.class);
		List<GuiaExameOdonto> guias = buscarGuias();
		System.out.println("[" + new Date() + "] Cancelando...");
		for (GuiaExameOdonto guia : guias) {
			guia.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), "Tempo limite para a realização do tratamento excedido.", new Date());
		}
		System.out.println("[" + new Date() + "] Persistindo guias...");
		for (GuiaExameOdonto guia : guias) {
			try {
				ImplDAO.save(guia);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("[" + new Date() + "] Concluído.");
	}
}
