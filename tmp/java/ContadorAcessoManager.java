package br.com.infowaypi.ecare.manager;


import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.mensagem.MailSender;
import br.com.infowaypi.ecare.sistema.ContadorAcesso;
import br.com.infowaypi.ecarebc.painelDeControle.EmailPainel;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.IsNull;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;
/**
 * Guarda metodos que podem sem reutilizados pela classe @ContadorAcesso
 * @author Emanuel
 *
 */
public class ContadorAcessoManager {

	/**
	 * Recupera o contador nao foi enviado.
	 * @return
	 */
	public static ContadorAcesso getContadorAtual(){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("enviado", false));
		sa.addParameter(new IsNull("dataEnvio"));
		return sa.uniqueResult(ContadorAcesso.class);
	}
	
	/**
	 * Recupera a ultima data de envio.
	 * @return
	 */
	public static Date getUltimaDataEnvio(){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("enviado", true));
		return (Date) sa.createCriteriaFor(ContadorAcesso.class)
		.setProjection(Projections.max("dataEnvio"))
		.uniqueResult();
	}
	
	/**
	 * Busca o contador, que ainda nao foi enviado, para ser incrementado a cada login de beneficiario.<br>
	 * Caso nao exista nenhum contador, ele cria. Esse caso so ocorre na 1ª vez.<br>
	 * Atualmente é chamado na classe @Autenticador
	 * @throws Exception
	 */
	public static void incrementarContador() throws Exception{
		ContadorAcesso contador = getContadorAtual();
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		if(contador == null){
			contador = new ContadorAcesso(1);
		}
		else{
			contador.incrementaContador();
		}
		ImplDAO.save(contador);
		tx.commit();
	}
	
	/**
	 * Pega o ultimo contador não enviado e seta auditado = true,
	 * e cria um novo contador com a quantidade do anterior. <br>
	 * Também envia um email para o responsavel por receber as estatisticas.<br>
	 * Atualmente é utilizado na classe @TaskEnviarContagemAcessos
	 * @throws Exception 
	 */
	public static void enviarDadosAcesso() throws Exception{
		Date inicio = getUltimaDataEnvio();
		
		if(inicio == null){
			//-7 em referencia a semana
			inicio = Utils.incrementaDias(new Date(), -7);
		}
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		ContadorAcesso ultimoContador = getContadorAtual();
		ultimoContador.setDataEnvio(new Date());
		ultimoContador.setEnviado(true);
		
		int quantidade = ultimoContador.getQuantidade();
		
		ContadorAcesso novoContador = new ContadorAcesso(quantidade);
		
		ImplDAO.save(ultimoContador);
		ImplDAO.save(novoContador);
		tx.commit();
		
		PainelDeControle painel = PainelDeControle.getPainel();
		
		Set<EmailPainel> emailpainel = painel.getEmailsPortal();
		 List<Usuario> usuariosDestino = costruirListaUsuariosDestino(emailpainel);
		
		String assunto = "Quantidade de acessos ao portal do beneficiário do Saúde Recife - " + Utils.format(new Date());
		String periodo = Utils.format(inicio) + " a " + Utils.format(new Date());
		
		StringBuffer corpo = new StringBuffer("");
		corpo.append("<p>O Portal do Beneficiario teve  " + quantidade + " acessos de " + periodo + ".");
		corpo.append("<p>Mensagem enviada automaticamente pelo sistema e-care/Saúde Recife.<p>");
		
		System.out.println(corpo.toString());
		
		MailSender.mandarEmailEmMassaHTML(usuariosDestino,"Infoway", assunto, corpo.toString());
	}
	
	private static List<Usuario> costruirListaUsuariosDestino(Set<EmailPainel> emailpainel) {
		List<Usuario> usuarios = new ArrayList<Usuario>();
		Iterator<EmailPainel> iterator = emailpainel.iterator();
		while(iterator.hasNext()){
			String email = iterator.next().getEmail();
			int index = email.indexOf("@");
			Usuario usuario = new Usuario();
			usuario.setNome(email.substring(0, index));
			usuario.setEmail(email);
			usuarios.add(usuario);
		}
		
		return usuarios;
	}

	public static void main(String[] args) throws Exception {
		enviarDadosAcesso();
	}
}
