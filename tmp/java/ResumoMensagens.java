package br.com.infowaypi.ecare.mensagem.alerta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.msr.msg.Mensagem;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class ResumoMensagens {

	private String assunto;
	private String corpo;
	private UsuarioInterface remetente;
	private UsuarioInterface destinatario;
	private CategoriaMensagemEnum categoria;
	private TipoMensagemEnum tipo;
	private boolean enviarSMS;
	private boolean enviarEmail;
	private List<Mensagem> mensagens;
	private List<Mensagem> mensagensNaoEnviadas;
	private Mensagem mensagemLida;
	private Date data;

	
	public ResumoMensagens(UsuarioInterface remetente, TipoMensagemEnum tipo, String assunto, String corpo, Date data, 
			boolean enviarSMS, boolean enviarEmail){
		
		this.remetente = remetente;
		this.data = data;
		this.assunto = assunto;
		this.corpo = corpo;
		this.tipo = tipo;
		this.mensagens = new ArrayList<Mensagem>();
		this.mensagensNaoEnviadas = new ArrayList<Mensagem>();
		this.enviarSMS = enviarSMS;
		this.enviarEmail = enviarEmail;
	}

	public ResumoMensagens(UsuarioInterface remetente, UsuarioInterface destinatario, TipoMensagemEnum tipo, CategoriaMensagemEnum categoria,
			Date data, List<Mensagem> mensagens){
		
		this(remetente, tipo, null, null, data, false, false);
		this.destinatario = destinatario;
		this.categoria = categoria;
		this.mensagens = mensagens;
	}
	
	public List<Mensagem> getMensagensALer() {
		List<Mensagem> msgs = new ArrayList<Mensagem>();
		for (Mensagem mensagem : getMensagens()) {
			if(mensagem.getLer())
				msgs.add(mensagem);
		}
		return msgs;
	}
	
	public List<Mensagem> getMensagens() {
		return mensagens;
	}
	
	public List<Mensagem> getMensagensNaoEnviadas() {
		return mensagensNaoEnviadas;
	}

	public void setMensagens(List<Mensagem> mensagens) {
		this.mensagens = mensagens;
	}
	
	public void setMensagensNaoEnviadas(List<Mensagem> mensagens) {
		this.mensagensNaoEnviadas = mensagens;
	}

	public UsuarioInterface getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(UsuarioInterface destinatario) {
		this.destinatario = destinatario;
	}

	public UsuarioInterface getRemetente() {
		return remetente;
	}

	public void setRemetente(UsuarioInterface remetente) {
		this.remetente = remetente;
	}

	public TipoMensagemEnum getTipo() {
		return tipo;
	}

	public void setTipo(TipoMensagemEnum tipo) {
		this.tipo = tipo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public CategoriaMensagemEnum getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaMensagemEnum categoria) {
		this.categoria = categoria;
	}

	public Mensagem getMensagemLida() {
		return mensagemLida;
	}

	public void setMensagemLida(Mensagem mensagemLida) {
		this.mensagemLida = mensagemLida;
	}
	
	public String getAssunto() {
		return assunto;
	}


	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}


	public String getCorpo() {
		return corpo;
	}


	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}

	public boolean isEnviarSMS() {
		return enviarSMS;
	}

	public void setEnviarSMS(boolean enviarSMS) {
		this.enviarSMS = enviarSMS;
	}

	public boolean isEnviarEmail() {
		return enviarEmail;
	}

	public void setEnviarEmail(boolean enviarEmail) {
		this.enviarEmail = enviarEmail;
	}

}
