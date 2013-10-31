package br.com.infowaypi.ecare.correcaomanual;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.TransactionManagerHibernate;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.OR;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings({"unchecked"})
public class TaskCancelarGuiasAnterioresAData implements Job {

	private static SearchAgent sa  = new SearchAgent();
	private static Date dataMax = Utils.createData("31/07/2012");
	private static Date dataMin;
	private static Usuario defaultUser;
	
	private static void reconfiguraIntervalo() {
		if (dataMin != null) {
			dataMax = dataMin;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataMax);
		dataMin = Utils.incrementaMes(calendar, -1);
	}
	
	private static void reconfiguraSearchAgent() {
		sa.clearAllParameters();
		sa.addParameter(new Between("dataMarcacao", dataMin, dataMax));
		sa.addParameter(new OR(new Equals("situacao.descricao", SituacaoEnum.AUTORIZADO.descricao()), new Equals("situacao.descricao", SituacaoEnum.SOLICITADO.descricao())));
	}
	
	public static void cancelaGuias() throws Exception {
		SearchAgent sag = new SearchAgent();
		sag.addParameter(new Equals("idUsuario", 1L));
		defaultUser = sag.uniqueResult(Usuario.class);
		
		TransactionManagerHibernate tmh = new TransactionManagerHibernate();
		tmh.beginTransaction();
		
		reconfiguraIntervalo();
		reconfiguraSearchAgent();
		
		System.out.println("[ " + new Date() + " ] [TaskCancelarGuiasAnterioresAData] Buscando guias referentes ao intervalo " + new SimpleDateFormat("dd/MM/yyyy").format(dataMin) + " a " + new SimpleDateFormat("dd/MM/yyyy").format(dataMax));
		List<GuiaSimples<ProcedimentoInterface>> guias = sa.list(GuiaSimples.class);

		while (guias.size() > 0) {
			try {
				System.out.println("[ " + new Date() + " ] [TaskCancelarGuiasAnterioresAData] Mudando situacoes das guias...");
				for (GuiaSimples<ProcedimentoInterface> guia : guias) {
					guia.mudarSituacao(defaultUser, SituacaoEnum.CANCELADO.descricao(), "CANCELAMENTO SOLICITADO PELA DIRETORIA DE SAÚDE DO SR", new Date());
				}
				System.out.println("[ " + new Date() + " ] [TaskCancelarGuiasAnterioresAData] Persistindo alteracoes nas guias...");
				tmh.commit();
			} catch (Exception e) {
				System.out.println("[ " + new Date() + " ] [TaskCancelarGuiasAnterioresAData] Houve um erro no cancelamento das guias entre  " + new SimpleDateFormat("dd/MM/yyyy").format(dataMin) + " e " + new SimpleDateFormat("dd/MM/yyyy").format(dataMax));
				e.printStackTrace();
				tmh.rollback();
			}
			tmh.beginTransaction();

			reconfiguraIntervalo();
			reconfiguraSearchAgent();
			
			System.out.println("[ " + new Date() + " ] [TaskCancelarGuiasAnterioresAData] Buscando guias referentes ao intervalo " + new SimpleDateFormat("dd/MM/yyyy").format(dataMin) + " a " + new SimpleDateFormat("dd/MM/yyyy").format(dataMax));
			guias = sa.list(GuiaSimples.class);
		}
		System.out.println("[ " + new Date() + " ] [TaskCancelarGuiasAnterioresAData] Não foram encontradas mais guias para cancelar!");
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			cancelaGuias();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
