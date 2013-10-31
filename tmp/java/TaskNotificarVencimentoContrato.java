package br.com.infowaypi.ecare.scheduller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.infowaypi.ecare.mensagem.MailSender;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.painelDeControle.EmailPainel;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioBuilder;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Essa Classe é responsável por notificar através de email cadastrado no Painel de 
 * Controle (PN), que determinados contratos ativos de prestadores estão vencidos. 
 * São enviados 2 alertas. O primeiro de acordo com a quantidade de dias informado
 * também no PN, antes do vencimento e 15 DIAS ANTES do dia do vencimento.
 * 
 * Exemplo de uso:
 * <pre>
 *    @see RunTasks.java
 * </pre>
 *
 * @author SR Team - Marcos Roberto 06.06.2012
 * @see Job.java, TaskNotificarVencimentoContrato.java, RunTasks.java
 * @changes Luciano Rocha 18/01/2013 Ajustes na manipulação do Calendar e para notificar 15 dias antes do vencimento.
 */
public class TaskNotificarVencimentoContrato implements Job {

	protected List<Prestador> prestadoresContratosEmAlerta;
	
	protected String ativo = Constantes.SITUACAO_ATIVO;
	protected Session sessao;
	protected Integer diasParaEnvioPrimeiroEmail;

	protected String assunto = "Aviso sobre de vencimento de contrato!";
	protected Usuario user = ImplDAO.findById(1l, Usuario.class);
	protected StringBuilder corpo = new StringBuilder();
	protected PainelDeControle painel = PainelDeControle.getPainel();
	
	@Override	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		sessao = HibernateUtil.currentSession();
		Transaction transacao = sessao.beginTransaction();
		
		try{
			System.out.println("Task TaskNotificarVencimentoContrato executando...");
			
			this.verificaContratosPrestadores(this.getPrestadoresAtivos());
			
			if(!prestadoresContratosEmAlerta.isEmpty()) {
				this.enviarAlerta(prestadoresContratosEmAlerta);
			} else {
				System.out.println("Não existem contratos vencidos. Nenhum alerta foi enviado.");
			}

			sessao.close();
			System.out.println("Task TaskNotificarVencimentoContrato finalizou.");

		} catch (Exception e) {
			transacao.rollback();
			e.printStackTrace();
			throw new JobExecutionException(e.getMessage());
		}

	}

	private static List<Usuario> costruirListaUsuariosDestino(Set<EmailPainel> emailpainel) {
		List<Usuario> usuarios = new ArrayList<Usuario>();
		for (EmailPainel email : emailpainel) {
			int index = email.getEmail().indexOf("@");
			
			Usuario usuario = new Usuario();
			usuario.setNome(email.getEmail().substring(0, index));
			usuario.setEmail(email.getEmail());
			usuarios.add(usuario);
		}
		return usuarios;
	}
	
	public List<Prestador> verificaContratosPrestadores(List<Prestador> prestadores) {
		corpo.append("<html><body>");
		corpo.append("<h3>O(s) Prestador(es) abaixo encontra(m)-se com contrato prestes a vencer/vencidos.</h3>");
		
		diasParaEnvioPrimeiroEmail = this.painel.getQuantidadeDiasEnvioPrimeiroEmail();
		prestadoresContratosEmAlerta = new ArrayList<Prestador>();
		
		Calendar dataTermino = Calendar.getInstance();
		Calendar hoje = Calendar.getInstance();
		
		for (Prestador prestador : prestadores) {
			if(prestador.getDataTerminoVigencia()!= null) {
				
				dataTermino.setTime(prestador.getDataTerminoVigencia());
				
				Integer diasRestantes = diferencaEmDias(dataTermino, hoje);
				
				boolean isQuantidadeDiasLimiteVencimento = diasRestantes.compareTo(diasParaEnvioPrimeiroEmail)==0;
				boolean isQuinzeDiasAntes = diasRestantes == 15;
				if(isQuantidadeDiasLimiteVencimento || isQuinzeDiasAntes) {
					prestadoresContratosEmAlerta.add(prestador);
					addPrestadorCorpoEmail(prestador);
				} 		
			}
		}
		corpo.append("<h4>SR -&nbsp; Sistema de Gest&atilde;o do Sa&uacute;de Recife&nbsp;- Saude Recife</h4>");
		corpo.append("</body></html>");
		
		return prestadoresContratosEmAlerta;
	}

	/**
	 * Método que calcula a diferença em dias.
	 * @param data1
	 * @param data2
	 * @return
	 */
	public Integer diferencaEmDias(Calendar data1, Calendar data2) {
		data2.set(Calendar.HOUR_OF_DAY, 0);
		data2.set(Calendar.MINUTE, 0);
		data2.set(Calendar.SECOND, 0);
		String dataString = Utils.format(data2.getTime(), "dd/MM/yyyy HH:mm:ss"); 
		
		Integer diff = Math.abs(Utils.getDiferencaEmDias(data1.getTime(), Utils.parse(dataString, "dd/MM/yyyy HH:mm:ss")));
		
		return diff;
	}

	private void addPrestadorCorpoEmail(Prestador prestador) {
		corpo.append("<div><strong>Prestador</strong>: " + prestador.getPessoaJuridica().getFantasia() + "</div>");
		corpo.append("<div>Inicio da Vigência: " + Utils.format(prestador.getDataInicioVigencia(), "dd/MM/yyyy") + "</div>");
		corpo.append("<div>Término da Vigência: " + Utils.format(prestador.getDataTerminoVigencia(), "dd/MM/yyyy") + "</div>");
		corpo.append("</br></br>");
	}

	protected void enviarAlerta(List<Prestador> prestadores) {
		Set<EmailPainel> emailpainel = painel.getEmailsContratos();
		List<Usuario> usuariosDestino = costruirListaUsuariosDestino(emailpainel);
		
		System.out.println("Contéudo do email: "+corpo.toString());
		MailSender.mandarEmailEmMassaHTML(usuariosDestino,"Infoway", assunto, corpo.toString());
		
//		Usuario u = new Usuario();
//		u.setEmail("wislanildo@infoway-pi.com.br");
//		u.setNome("Wislanildo");
//		MailSender.mandarEmailHTML((UsuarioInterface)u,"Infoway", assunto, corpo.toString());
	}

	@SuppressWarnings("unchecked")
	public List<Prestador> getPrestadoresAtivos() {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao", ativo));
		
		return sa.list(Prestador.class);
	}

	public static void main(String[] args) throws JobExecutionException {
		TaskNotificarVencimentoContrato task = new TaskNotificarVencimentoContrato();
		task.execute(null);
	}
}