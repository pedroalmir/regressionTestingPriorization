package br.com.infowaypi.ecare.scheduller;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

import br.com.infowaypi.ecare.scheduller.sms.IntervaloDeTempo;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
 
/**
 * Classe encarregada de processar tarefas temporais da aplicação
 * @author Danilo Nogueira Portela
 */
public class RunTasks {
  
	public static void execute() throws Exception {
		new CreateDataBase().main(null);
		
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		Trigger triggerRegulrazacaoDependente = TriggerUtils.makeDailyTrigger("Trigger Regularizacao de Dependentes", 0, 0);
		JobDetail jobRegularizacaoDepedentes = new JobDetail("Regularizacao de Dependentes", Scheduler.DEFAULT_GROUP, TaskRegularizarDependente.class);
		scheduler.scheduleJob(jobRegularizacaoDepedentes, triggerRegulrazacaoDependente);
		
		Trigger triggerSuspenderDependente = TriggerUtils.makeDailyTrigger("Trigger Suspensao de Dependentes", 0, 0);
		JobDetail jobSuspenderDepedentes = new JobDetail("Suspensao de Dependentes", Scheduler.DEFAULT_GROUP, TaskSuspendeDependentes.class);
		scheduler.scheduleJob(jobSuspenderDepedentes, triggerSuspenderDependente);
				
		Trigger triggerAtualizarConsultasPromocionais = TriggerUtils.makeDailyTrigger("Trigger Atualizar Consultas Promocionais", 1, 0);
		JobDetail jobAtualizarConsultasPromocionais = new JobDetail("Atualização de Consultas Promocionais", Scheduler.DEFAULT_GROUP, TaskAtualizarConsultasPromocionais.class);
		scheduler.scheduleJob(jobAtualizarConsultasPromocionais, triggerAtualizarConsultasPromocionais);
		
		Trigger triggerSuspenderBeneficiariosInadimplenets = TriggerUtils.makeDailyTrigger("Trigger suspender beneficiários inadimplentes", 2, 0);
		JobDetail jobSuspensaoInadimplentes = new JobDetail("Suspensao de Beneficiarios Inadimplentes - Dia 10", Scheduler.DEFAULT_GROUP, TaskSuspenderTitularesInadimplentes.class);
		scheduler.scheduleJob(jobSuspensaoInadimplentes, triggerSuspenderBeneficiariosInadimplenets);
		
		Trigger triggerEnviarContagemAcessos = TriggerUtils.makeWeeklyTrigger("Trigger enviar quantidade de acessos.", TriggerUtils.MONDAY, 0, 0);
		JobDetail jobEnviarContagemAcessos = new JobDetail("enviar quantidade de acessos", Scheduler.DEFAULT_GROUP, TaskEnviarContagemAcessos.class);
		scheduler.scheduleJob(jobEnviarContagemAcessos, triggerEnviarContagemAcessos);

		/* IF[TASK_CANCELAR_GUIAS_ODONTO_AUTORIZADA_A_MAIS_DE_60_DIAS]
		Trigger triggerCancelarGuiasOdonto = TriggerUtils.makeWeeklyTrigger("Trigger Cancelar Guias Odonto", TriggerUtils.MONDAY, 0, 0);
		JobDetail jobCancelarGuiasOdonto = new JobDetail("cancelar guias odonto", Scheduler.DEFAULT_GROUP, TaskCancelamentoGuiasOD.class);
		scheduler.scheduleJob(jobCancelarGuiasOdonto, triggerCancelarGuiasOdonto);
		/* END[TASK_CANCELAR_GUIAS_ODONTO_AUTORIZADA_A_MAIS_DE_60_DIAS]*/
		
//		Calendar calendar = new GregorianCalendar();
//		calendar.set(2012, Calendar.NOVEMBER, 10, 21, 1);
//		Trigger triggerCancelamentoGuiasAntigasSR  = TriggerUtils.makeImmediateTrigger("Trigger cancelamento guias antigas do SR", 0, 1);
//		JobDetail jobCancelamentoGuiasAntigasSR = new JobDetail("Cancelar guias antigas SR", Scheduler.DEFAULT_GROUP, TaskCancelarGuiasAnterioresAData.class);
//		triggerCancelamentoGuiasAntigasSR.setStartTime(calendar.getTime());
//		scheduler.scheduleJob(jobCancelamentoGuiasAntigasSR, triggerCancelamentoGuiasAntigasSR);
		
//		Trigger triggerRelatorioCoParticipacoes = TriggerUtils.makeMonthlyTrigger("Trigger gerar arquivos de relatórios de co-participações", 26, 17, 45);
//		JobDetail jobRelatorioCoParticipacoes = new JobDetail("gerar arquivos de relatórios de co-participações", Scheduler.DEFAULT_GROUP, TaskResumoDeBeneficiariosDeGuiasComCoParticipacoesErradas.class);
//		scheduler.scheduleJob(jobRelatorioCoParticipacoes, triggerRelatorioCoParticipacoes);
		
//		Trigger triggerCorrigirValoresProcedimentosGuiaCTCI = TriggerUtils.makeMonthlyTrigger("Trigger para corrigir valores procedimentos guias CTCI", 20, 23, 0);
//		JobDetail jobCorrigirValoresProcedimentosGuiaCTCI = new JobDetail("gerar arquivos de relatórios de co-participações", Scheduler.DEFAULT_GROUP, CorrigirValoresProcedimentosGuiasCTCI.class);
//		scheduler.scheduleJob(jobCorrigirValoresProcedimentosGuiaCTCI, triggerCorrigirValoresProcedimentosGuiaCTCI);
		
		/**
		 * Trigger de envio de aviso de pendências de autorizações de guias de urgência aos reguladores.
		 */
		for (IntervaloDeTempo horario : PainelDeControle.getPainel().getSmsHorarios()) {
			TaskAvisoSolicitacoesRegulador.agendarTaskAvisoRegulador(horario);
		}
		
//		Trigger triggerNotificarVencimentoContrato = TriggerUtils.makeWeeklyTrigger("Trigger informar SR sobre Prestadores com Contrato a vencer/vencidos.", TriggerUtils.MONDAY, 2, 0);
		Trigger triggerNotificarVencimentoContrato = TriggerUtils.makeDailyTrigger("Trigger informar SR sobre Prestadores com Contrato a vencer/vencidos.", 1, 0);
//		Trigger triggerNotificarVencimentoContrato = TriggerUtils.makeImmediateTrigger("Trigger informar SR sobre Prestadores com Contrato a vencer/vencidos.", 1, 1);

		JobDetail jobNotificarVencimentoContrato = new JobDetail("informar SR sobre Prestadores com Contrato a vencer/vencidos.", Scheduler.DEFAULT_GROUP, TaskNotificarVencimentoContrato.class);
		scheduler.scheduleJob(jobNotificarVencimentoContrato, triggerNotificarVencimentoContrato);
		
		scheduler.start();
	}	
}