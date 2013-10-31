/**
 * 
 */
package br.com.infowaypi.ecare.scheduller;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.mail.ByteArrayDataSource;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.infowaypi.ecare.enums.MotivoEnumSR;
import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
public class TaskSuspendeDependentes implements Job{
	
	private static StringBuffer buffer_email_com_dependentes = new StringBuffer();
	private static StringBuffer buffer_email_sem_dependentes = new StringBuffer();
	
	private static ByteArrayDataSource arquivo;
	
	private static int linha = 0;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		try {
			
			initBuffers();
			Transaction t = HibernateUtil.currentSession().beginTransaction();
			
			UsuarioInterface usuario = ImplDAO.findById(1L, Usuario.class);
			
			Integer[] dependencias = {Dependente.TIPO_FILHO_MENOR_Q_21,Dependente.TIPO_FILHO_MENOR_25_ANOS,Dependente.TIPO_ENTEADO};
			
			System.out.println("buscando dependentes...");
			List<Dependente> dependentes = HibernateUtil.currentSession().createCriteria(Dependente.class)
			.add(Expression.in("tipoDeDependencia", dependencias))
			.add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
			.list();
			
			System.out.println(dependentes.size());
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			WritableWorkbook pasta = Workbook.createWorkbook(baos);
			WritableSheet planilha = pasta.createSheet("Dependentes Suplementar", 0);
			planilha.addCell(new Label(0, 0, "DEPENDENTE"));
	        planilha.addCell(new Label(1, 0, "CARTAO"));
	        planilha.addCell(new Label(2, 0, "IDADE"));
	        planilha.addCell(new Label(3, 0, "TIPO DEPENDENTE"));

	        
			for (Dependente dependente : dependentes) {
	
				if(dependente.getTipoDeDependencia().equals(Dependente.TIPO_FILHO_MENOR_Q_21) && dependente.getIdade() >20){
					dependente.mudarSituacao(usuario, SituacaoEnum.SUSPENSO.descricao(), MotivoEnumSR.DEPENDENTE_COM_IDADE_SUPERIOR_A_PERMITIDA.getMessage(), new Date());
					planilha.addCell(new Label(0,linha,dependente.getPessoaFisica().getNome()));
					planilha.addCell(new Label(1,linha,dependente.getNumeroDoCartao()));
					planilha.addCell(new Number(2,linha,dependente.getIdade()));
					planilha.addCell(new Label(3,linha,"Filho Menor que 21 anos"));
					linha++;
					ImplDAO.save(dependente);
				}
				if(dependente.getTipoDeDependencia().equals(Dependente.TIPO_FILHO_MENOR_25_ANOS) && dependente.getIdade() > 24){
					dependente.mudarSituacao(usuario, SituacaoEnum.SUSPENSO.descricao(), MotivoEnumSR.DEPENDENTE_COM_IDADE_SUPERIOR_A_PERMITIDA.getMessage(), new Date());
					planilha.addCell(new Label(0,linha,dependente.getPessoaFisica().getNome()));
					planilha.addCell(new Label(1,linha,dependente.getNumeroDoCartao()));
					planilha.addCell(new Number(2,linha,dependente.getIdade()));
					planilha.addCell(new Label(3,linha,"Filho Menor que 25 anos"));
					linha++;
					ImplDAO.save(dependente);
				}
				
				if(dependente.getTipoDeDependencia().equals(Dependente.TIPO_ENTEADO) && dependente.getIdade() > 24){
					dependente.mudarSituacao(usuario, SituacaoEnum.SUSPENSO.descricao(), MotivoEnumSR.DEPENDENTE_COM_IDADE_SUPERIOR_A_PERMITIDA.getMessage(), new Date());
					planilha.addCell(new Label(0,linha,dependente.getPessoaFisica().getNome()));
					planilha.addCell(new Label(1,linha,dependente.getNumeroDoCartao()));
					planilha.addCell(new Number(2,linha,dependente.getIdade()));
					planilha.addCell(new Label(3,linha,"Enteado"));
					linha++;
					ImplDAO.save(dependente);
				}
			}
			t.commit();
			pasta.write();
			pasta.close();
			
			arquivo = new ByteArrayDataSource(baos.toByteArray(),"teste");
			
			sendMail();
		}catch (Exception e) {
			e.printStackTrace();
			throw new JobExecutionException(e.getMessage());
		}
	}

	private static void initBuffers() {
		buffer_email_com_dependentes.append("E-Care Saude Recife informa:");
		buffer_email_com_dependentes.append(System.getProperty("line.separator"));
		buffer_email_com_dependentes.append("Em anexo, a listagem de dependentes(s) suspenso(s) automaticamente.");
		buffer_email_com_dependentes.append(System.getProperty("line.separator"));
		buffer_email_com_dependentes.append(System.getProperty("line.separator"));
		buffer_email_com_dependentes.append("E-mail enviado automaticamente no dia "+ Utils.format(new Date())+". Por favor, não responder.");
		
		buffer_email_sem_dependentes.append("E-Care Saude Recife informa:");
		buffer_email_sem_dependentes.append(System.getProperty("line.separator"));
		buffer_email_sem_dependentes.append("Não houve supensão de dependentes hoje.");
		buffer_email_sem_dependentes.append(System.getProperty("line.separator"));
		buffer_email_sem_dependentes.append(System.getProperty("line.separator"));
		buffer_email_sem_dependentes.append("E-mail enviado automaticamente no dia "+ Utils.format(new Date())+". Por favor, não responder.");
	}
	
	private static void sendMail() {

		try { 
			MultiPartEmail  email = new MultiPartEmail ();  
		    email.setHostName("smtp.gmail.com"); 
		    email.setAuthentication("contatoSR@infoway-pi.com.br","contatoSR");  
//		    email.setSSL(true); 
		    email.setSmtpPort(587);
		    email.addTo("josino@infoway-pi.com.br"); //pode ser qualquer um email
		    email.addTo("marcus@infoway-pi.com.br");
		    email.setFrom("contatoSR@infoway-pi.com.br"); //aqui necessita ser o email que voce fara a autenticacao  
		    email.setSubject("Dependentes Suspensos de Hoje (E-mail Automático)");  
		    email.setMsg(buffer_email_com_dependentes.toString()); 
		   
		    if(linha > 0){
		    	email.attach(arquivo,"Dependentes_Suspensos","Dependentes Suspensos");
		    	email.setMsg(buffer_email_com_dependentes.toString());
		    }else {
		    	email.setMsg(buffer_email_sem_dependentes.toString());
		    }  
		    email.send();
		} catch (EmailException e) {  
			e.printStackTrace();  
		}
	}

}
