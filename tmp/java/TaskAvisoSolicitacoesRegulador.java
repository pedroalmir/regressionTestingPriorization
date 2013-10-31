package br.com.infowaypi.ecare.scheduller;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.DailyCalendar;

import br.com.infowaypi.config.Config;
import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecare.scheduller.sms.IntervaloDeTempo;
import br.com.infowaypi.ecare.scheduller.sms.MailSender;
import br.com.infowaypi.ecare.scheduller.sms.MensagemAvisoRegulador;
import br.com.infowaypi.ecare.scheduller.sms.SMSSender;
import br.com.infowaypi.ecare.scheduller.sms.TipoDeMensagem;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Envia mensagem sms para regulador caso existam guias/procedimentos/exames/pacotes em 
 * situação SOLICITADO/SOLICITADO_PRORROGACAO/SOLICITADO_INTERNACAO
 * 
 * Exemplo de uso:
 * <pre>
 *    @see {@link RunTasks.java}
 * </pre>
 *
 *
 * @author Leonardo Sampaio
 * @since 20/08/2012
 */

public class TaskAvisoSolicitacoesRegulador implements Job {
	
		public static String IDENTIFICADOR_TASK_SMS = "reguladorSMS";
		
    	//dias para buscar guias/procedimentos passíveis de autorização
    	public static int INTERVALO_DIAS = -2;
    	//número máximo de guias a serem retornadas, este valor influencia diretamente na performance
    	//(numero de mensagens enviadas, memória, tempo de resposta)
    	public static int MAX_RESULTS = 50;
    	
    	StringBuffer mensagem;
    	boolean test = false;
    
	/**
	 * painel de controle
	 * 
	 * reguladores para avisar
	 * horario de funcionamento da task
	 * intervalo de busca 
	 * 
	 * */
	@Override	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Session sessao = HibernateUtil.currentSession();
		Transaction transacao = sessao.beginTransaction();
		sessao.clear();
		sessao.setFlushMode(FlushMode.COMMIT);
		TipoDeMensagem tipoDeMensagem = (TipoDeMensagem) context.getJobDetail().getJobDataMap().get("tipo");
		
		try{
	    	System.out.println("[TaskAvisoSolicitacoesRegulador] Enviando "+tipoDeMensagem.descricao()+" para reguladores");
			enviarMensagens(tipoDeMensagem);
			transacao.commit();
			System.out.println("[TaskAvisoSolicitacoesRegulador] Fim");
		} catch (Exception e) {
			transacao.rollback();
			e.printStackTrace();
			throw new JobExecutionException(e.getMessage());
		}

	}

	/**
	 * Busca guias passíveis de autorização.
	 * 
	 * @return guias 
	 */
	@SuppressWarnings("unchecked")
	public List<GuiaSimples<ProcedimentoInterface>> buscarGuiasPassiveisDeAutorizacao(){
	    
	    String[] tiposDeGuia = {
		    TipoGuiaEnum.INTERNACAO_URGENCIA.tipo(), 
		    TipoGuiaEnum.ATENDIMENTO_URGENCIA.tipo(),
		    TipoGuiaEnum.CONSULTA_URGENCIA.tipo(),
		    TipoGuiaEnum.INTERNACAO_ELETIVA.tipo(),
		    TipoGuiaEnum.EXAME.tipo()
	    };
	    
	    String[] situacoesDasGuias = {
		    SituacaoEnum.SOLICITADO.descricao(), 
		    SituacaoEnum.SOLICITADO_INTERNACAO.descricao(),
		    SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(),
	    };
	    
	    String[] situacoesDasGuiasValidasSolicitacaoDeProcedimento = {
		    SituacaoEnum.SOLICITADO.descricao(), 
		    SituacaoEnum.SOLICITADO_INTERNACAO.descricao(),
		    SituacaoEnum.SOLICITADO_PRORROGACAO.descricao(),
		    SituacaoEnum.ABERTO.descricao(),
		    SituacaoEnum.PRORROGADO.descricao()
	    };
	    
	    
	    String[] situacoesDosProcedimentos = {
		    SituacaoEnum.SOLICITADO.descricao() 
	    };
	    
        	
	    
	    //FIXME otimizar essa busca, solução temporária. alias "procedimentos.procedimento" faz com que a o sql gerado
	    //pelo hibernate nao seja o correto para a primeira parte do Restrictions.or 
	    
		List<GuiaSimples<ProcedimentoInterface>> guias = null;

		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaSimples.class).
			setMaxResults(MAX_RESULTS).
			add(Expression.in("tipoDeGuia", tiposDeGuia)).
//			createAlias("procedimentos", "procedimento").
			add(
//			Restrictions.or(
				Restrictions.and(
					Restrictions.in("this.situacao.descricao", situacoesDasGuias),
					Restrictions.between("this.situacao.dataSituacao", Utils.incrementaDias(new Date(), INTERVALO_DIAS), new Date())
					)//,
//				Restrictions.and(
//        				Restrictions.and(
//        					Restrictions.in("procedimento.situacao.descricao", situacoesDosProcedimentos),
//        					Restrictions.between("procedimento.situacao.dataSituacao", Utils.incrementaDias(new Date(), INTERVALO_DIAS), new Date())
//        					),
//        					Restrictions.in("this.situacao.descricao", situacoesDasGuiasValidasSolicitacaoDeProcedimento)
//        					)
//        				)
			);
		
		Criteria criteria2 = HibernateUtil.currentSession().createCriteria(GuiaSimples.class).
			setMaxResults(MAX_RESULTS).
			add(Expression.in("tipoDeGuia", tiposDeGuia)).
			createAlias("procedimentos", "procedimento").
			add(
				Restrictions.and(
        				Restrictions.and(
        					Restrictions.in("procedimento.situacao.descricao", situacoesDosProcedimentos),
        					Restrictions.between("procedimento.situacao.dataSituacao", Utils.incrementaDias(new Date(), INTERVALO_DIAS), new Date())
        				),
        				Restrictions.in("this.situacao.descricao", situacoesDasGuiasValidasSolicitacaoDeProcedimento)
        				)
        		);
		
		
		List<GuiaSimples<ProcedimentoInterface>> result1 = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		List<GuiaSimples<ProcedimentoInterface>> result2 = criteria2.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
		
		for (GuiaSimples<ProcedimentoInterface> guia : result1) {
		    if (!result2.contains(guia)) {
			result2.add(guia);
		    }
		}
		
		guias = result2;
		
		return guias;
	}
	

	/**
	 * Envia uma mensagem SMS ou e-mail.
	 * 
	 * @param tipo tipo de mensagem
	 * @throws Exception 
	 */
	public void enviarMensagens(TipoDeMensagem tipo) throws Exception {
	    
	    List<GuiaSimples<ProcedimentoInterface>> guias = buscarGuiasPassiveisDeAutorizacao();
	    System.out.println("Quantidade de guias: " + guias.size());
	    if (guias!=null) {
		
		    for (GuiaSimples<ProcedimentoInterface> guiaSimples : guias) {
		    	mensagem = new StringBuffer();
				mensagem.append("Guia pendente de regulacao: n.");
				mensagem.append(guiaSimples.getAutorizacao());
				guiaSimples.getColecaoSituacoes().getSituacoes().size();
				if(guiaSimples.getPrestador() != null){
					mensagem.append(", prestador: ");
					mensagem.append(guiaSimples.getPrestador().getNome());
				}
				
				mensagem.append(".");
		
				if (tipo == TipoDeMensagem.SMS) {
					String numero = PainelDeControle.getPainel().getSmsNumeroEmHorarioNaoComercial();
					String mensagemAEnviar = reduzirParaCaber(mensagem.toString());
					enviarSMS(guiaSimples, numero, mensagemAEnviar);
				} else if (tipo == TipoDeMensagem.EMAIL) {
				    String email = PainelDeControle.getPainel().getEmailRegulador();
				    String mensagemAEnviar = mensagem.toString();
				    String assunto = "Aviso de pendência de regulação - Guia n."+guiaSimples.getAutorizacao();
				    enviarEmail(guiaSimples, email, assunto, mensagemAEnviar);
			    }
			}
	    }
	}
		
	/**
	 * Enviar email (MSR)
	 * 
	 * @param assunto assunto da mensagem
	 * @param mensagem mensagem
	 */
	private void enviarEmail(GuiaSimples<ProcedimentoInterface> guia, String email, String assunto, String mensagem) {
	    
	    Usuario usuarioDesenvolvedoresUN = new Usuario();
		usuarioDesenvolvedoresUN.setEmail(email);
		usuarioDesenvolvedoresUN.setCelular(Config.TELEFONE_UNIPLAM);
		
		if (!this.test) {
		    try {
		    	MailSender.mandarEmail(guia, usuarioDesenvolvedoresUN, assunto, mensagem);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
	}
	
	/**
	 * Trunca a mensagem caso o tamanho seja superior a 140 caracteres (limite para uma mensagem SMS) 
	 * 
	 * @param mensagem mensagem inicial
	 * 
	 * @return mensagem truncada
	 */
	private String reduzirParaCaber(String mensagem) {
	    
	    if (mensagem.length()>=140) {
	    	return mensagem.substring(0,139);
	    }
	    
	    return mensagem;
	}
	
	/**
	 * Envia sms 
	 * 
	 * @param numero numero de celular
	 * @param conteudo mensagem
	 */
	private void enviarSMS(GuiaSimples<ProcedimentoInterface> guia, String numero, String conteudo) {
	    
	    SMSSender sender = SMSSender.getInstance(test);

	    MensagemAvisoRegulador sms = new MensagemAvisoRegulador();

	    sms.setDestino(numero);
	    sms.setDataEnvio(Calendar.getInstance().getTime());
	    sms.setConteudo(conteudo);
	    sms.setGuia(guia);

	    sender.addSMS(sms);

	    if (!this.test) {
	    	sender.sendAll();
	    } else {
	    	sender.sendAllWithouThreads();
	    }
	}

	public String getUltimaMensagem() {
	    return mensagem.toString();
	}
	
	public void setTest(boolean test) {
	    this.test = test;
	    
	}
	
	
	/**
	 * Agenda uma nova task de envio de mensagens.
	 * 
	 * @param horario horario de agendamento
	 * 
	 * @throws SchedulerException,ParseException 
	 * 
	 */
	public static void agendarTaskAvisoRegulador(IntervaloDeTempo horario) throws SchedulerException,ParseException {
	    
	    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

	    boolean entreDoisDias = horario.entreDoisDias();

	    if (entreDoisDias) {
	    	horario.inverter();
	    }

	    String id = IDENTIFICADOR_TASK_SMS+horario.getIdIntervaloDeTempo();

	    CronTrigger triggerEnviarAvisosUrgencias = new CronTrigger(
		    id, Scheduler.DEFAULT_GROUP, "0 0/"+PainelDeControle.getPainel().getSmsIntervaloVerificacaoEmMinutos()+" * * * ?"
	    );

	    JobDetail job = new JobDetail(id, Scheduler.DEFAULT_GROUP, TaskAvisoSolicitacoesRegulador.class);
	    
	    JobDataMap jobDataMap = new JobDataMap();
	    
	    jobDataMap.put("tipo", horario.getTipoDeMensagem());
	    
	    job.setJobDataMap(jobDataMap);

	    triggerEnviarAvisosUrgencias.setJobName(id);
	    triggerEnviarAvisosUrgencias.setJobGroup(Scheduler.DEFAULT_GROUP);


	    DailyCalendar horarioCalendar = new DailyCalendar(
		    horario.getInicioDoIntervalo(), 
		    horario.getFinalDoIntervalo());

	    horarioCalendar.setInvertTimeRange(!entreDoisDias); //excluir todos os intervalos menos o informado
	    horarioCalendar.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));

	    scheduler.addCalendar(id, horarioCalendar, true, true);

	    triggerEnviarAvisosUrgencias.setCalendarName(id);

	    scheduler.scheduleJob(job, triggerEnviarAvisosUrgencias);
	    scheduler.start();
	}

}
