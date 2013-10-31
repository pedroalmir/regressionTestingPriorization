package br.com.infowaypi.ecare.scheduller;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.infowaypi.ecare.manager.ContadorAcessoManager;

/**
 * Task quartz que chama a rotina para enviar a aquantidade de acessos
 * e cria novo contador
 * @author Emanuel
 *
 */
public class TaskEnviarContagemAcessos implements Job{
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			System.out.println("vai enviar dados");
			ContadorAcessoManager.enviarDadosAcesso();
		}catch (Exception e) {
			e.printStackTrace();
			throw new JobExecutionException(e.getMessage());
		}
	}
}
