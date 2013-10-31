package br.com.infowaypi.ecare.mensagem;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.msr.msg.Mensagem;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe responsável  pelo envio de email.<br>
 * Obs: Esta classe veio do Uniplam
 * @author Diogo Vinícius / Rondinele / Thalisson
 * @changes Emanuel,Jefferson
 */
public class MailSender {

	public static void mandarEmail(UsuarioInterface usuarioDestino, String assunto, String corpo) {
		UsuarioInterface usuarioRemetente = new Usuario();
		usuarioRemetente.setEmail("contato-no-reply@infoway-pi.com.br");
		usuarioRemetente.setNome("Infoway");
		
		Mensagem mensagem = new Mensagem();
		mensagem.setAssunto(assunto);
		mensagem.setAvisarRemetente(true);
		mensagem.setCorpo(corpo);
		mensagem.setDataMensagem(new Date());
		mensagem.setDestinatario(usuarioDestino);
		mensagem.setEnviarEmail(true);
		mensagem.setRemetente(usuarioRemetente);
		mensagem.enviarEmail();
	}
	
	public static void mandarEmailHTML(UsuarioInterface usuario, String nome, String assunto, String corpo) {
		Usuario usuarioDestino = new Usuario();
		Usuario usuarioRemetente = new Usuario();
		usuarioRemetente.setEmail("contato-no-reply@infoway-pi.com.br");
		usuarioRemetente.setNome(nome);
		
		usuarioDestino.setNome(usuario.getNome());
		usuarioDestino.setEmail(usuario.getEmail());

		criarMensagem(assunto, corpo, usuarioRemetente, usuarioDestino);
	}
	
	public static void mandarEmailEmMassaHTML( List<Usuario> usuarios, String nome, String assunto, String corpo) {
		Usuario usuarioRemetente = new Usuario();
		usuarioRemetente.setEmail("contato-no-reply@infoway-pi.com.br");
		usuarioRemetente.setNome(nome);
		for (Usuario usuarioDestino : usuarios ){
			criarMensagem(assunto, corpo, usuarioRemetente, usuarioDestino);
		}
	}

	private static void criarMensagem(String assunto, String corpo,
			Usuario usuarioRemetente, Usuario usuarioDestino) {
		Mensagem mensagem = new Mensagem();
		mensagem.setAssunto(assunto);
		mensagem.setAvisarRemetente(true);
		mensagem.setCorpo(corpo);
		mensagem.setDataMensagem(new Date());
		mensagem.setDestinatario(usuarioDestino);
		mensagem.setRemetente(usuarioRemetente);
		mensagem.setEnviarEmail(true);
		mensagem.enviarEmailHTML();
	}
	
}
