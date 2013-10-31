package br.com.infowaypi.ecare.utils;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import br.com.infowaypi.ecare.scheduller.TaskAvisoSolicitacoesRegulador;
import br.com.infowaypi.ecare.scheduller.sms.IntervaloDeTempo;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;

public class FlowPainelDeControle{

    public PainelDeControle carregarPainel(PainelDeControle painel){
    	return PainelDeControle.getPainel();
    }

    public PainelDeControle alterarPainel(PainelDeControle painel) throws ValidateException{
		painel.validate();
		return painel;
    }

    public PainelDeControle salvarPainel(PainelDeControle painel) throws Exception {
		PainelDeControle painelAnterior = ImplDAO.findById(painel.getIdPainelDeControle(), PainelDeControle.class);
		renovarSessaoHibernate();
		ImplDAO.save(painel);
		atualizarTasksQuartz(painel, painelAnterior);
		return painel;
    }

    /**
     * Atualiza jobs agendados de acordo com os horários inseridos.
     * 
     * @param painel
     * @throws SchedulerException
     * @throws ParseException
     */
    private void atualizarTasksQuartz(PainelDeControle painel, PainelDeControle painelAnterior) throws SchedulerException, ParseException {

		Set<IntervaloDeTempo> horariosAnteriores = painelAnterior.getSmsHorarios();
		Set<IntervaloDeTempo> horariosAtuais = painel.getSmsHorarios();
		Set<IntervaloDeTempo> horariosRemovidos = new HashSet<IntervaloDeTempo>();
		Set<IntervaloDeTempo> horariosNovos = new HashSet<IntervaloDeTempo>();
	
		for (IntervaloDeTempo horarioAnterior : horariosAnteriores) {
	
		    if (!naLista(horarioAnterior,horariosAtuais)) {
			horariosRemovidos.add(horarioAnterior);
		    }
		}
	
		if (horariosRemovidos.size()>0) {
		    removerHorariosQuartz(horariosRemovidos);
		}
		
		if (painelAnterior.getSmsIntervaloVerificacaoEmMinutos()!=painel.getSmsIntervaloVerificacaoEmMinutos()) {
		    reagendarTasksAntigasComNovoIntervalo(horariosAnteriores,horariosRemovidos);
		}
	
		for (IntervaloDeTempo horarioAtual : horariosAtuais) {
	
		    if (!naLista(horarioAtual,horariosAnteriores)) {
			horariosNovos.add(horarioAtual);
		    }
	
		}
	
		if (horariosNovos.size()>0) {
		    adicionarHorariosQuartz(horariosNovos);
		}
	
    }
    
    /**
     * Reagenda tasks existentes para novo intervalo.
     * @throws SchedulerException 
     * @throws ParseException 
     * */
    private void reagendarTasksAntigasComNovoIntervalo(Set<IntervaloDeTempo> horariosAnteriores, Set<IntervaloDeTempo> horariosRemovidos) throws SchedulerException, ParseException {
	
		Set<IntervaloDeTempo> aReagendar = new HashSet<IntervaloDeTempo>();
		
		for (IntervaloDeTempo horarioAnterior : horariosAnteriores) {
		    if (!naLista(horarioAnterior,horariosRemovidos)){
			aReagendar.add(horarioAnterior);
		    }
		}
		
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
	
		for (IntervaloDeTempo intervaloDeTempo : aReagendar) {
	
		    String identificador = TaskAvisoSolicitacoesRegulador.IDENTIFICADOR_TASK_SMS+intervaloDeTempo.getIdIntervaloDeTempo();
	
		    CronTrigger trigger = (CronTrigger)scheduler.getTrigger(identificador,Scheduler.DEFAULT_GROUP);
		    
		    trigger.setCronExpression("0 0/"+PainelDeControle.getPainel().getSmsIntervaloVerificacaoEmMinutos()+" * * * ?");
		    
		    scheduler.rescheduleJob(identificador,Scheduler.DEFAULT_GROUP, trigger);
		    
		}
		
    }

    /**
     * Contains desconsiderando IntervaloDeTempo.painel
     * 
     * @param horario
     * @param lista
     * @return
     */
    private boolean naLista(IntervaloDeTempo horario, Set<IntervaloDeTempo> lista) {
	
		if (horario==null){
		    return false;
		} else {
        	for (IntervaloDeTempo intervaloDeTempo : lista) {
        	    if (horario.getIdIntervaloDeTempo().equals(intervaloDeTempo.getIdIntervaloDeTempo()))
        		return true;
        	}
		}	
	
		return false;
    }

    /**
     * Remove tasks quartz cujos horários foram removidos no PainelDeControle.
     * 
     * Id do IntervaloDeTempo deve ser nao nulo.
     * 
     * @param horariosRemovidos
     * @throws SchedulerException
     */
    private void removerHorariosQuartz(
	    Set<IntervaloDeTempo> horariosRemovidos) throws SchedulerException {

		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
	
		for (IntervaloDeTempo intervaloDeTempo : horariosRemovidos) {
	
		    String identificador = TaskAvisoSolicitacoesRegulador.IDENTIFICADOR_TASK_SMS+intervaloDeTempo.getIdIntervaloDeTempo();
	
		    scheduler.unscheduleJob(identificador,Scheduler.DEFAULT_GROUP);
		    scheduler.deleteJob(identificador,Scheduler.DEFAULT_GROUP);
		}

    }
    
    /**
     * Evita NonUniqueObjectException ao carregar o painel via id e salvar em seguida a versao da sessao.
     */
    private void renovarSessaoHibernate() {
		Session currentSession = HibernateUtil.currentSession();
		currentSession.flush();
		currentSession.clear();
    }

    /**
     * Agenda tasks para novos horários.
     * 
     * @param horariosNovos
     * @throws SchedulerException
     * @throws ParseException 
     */
    private void adicionarHorariosQuartz(Set<IntervaloDeTempo> horariosNovos) throws SchedulerException, ParseException {

		for (IntervaloDeTempo intervaloNovo : horariosNovos) {
			TaskAvisoSolicitacoesRegulador.agendarTaskAvisoRegulador(intervaloNovo);
		}
    }
}
