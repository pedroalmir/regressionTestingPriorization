package br.com.infowaypi.ecare.mensagem.alerta;


public enum TipoMensagemEnum {

	INFORMACAO	(5, 	"Informação", 	"information"),
	ALERTA		(3, 	"Alerta", 		"warning"),
	NOTIFICACAO	(1, 	"Notificação", 	"notify"),
	ERRO		(null, 	"Erro", 		"error"),
	SUCESSO		(null, 	"Sucesso", 		"confirmation");
	
	private String descricao;
	private Integer prazo;
	private String style;
	
	private TipoMensagemEnum(Integer prazo, String descricao, String style) {
		this.prazo = prazo;
		this.descricao = descricao;
		this.style = style;
	}
	
	/** valida o prazo informado caso o mesmo seja maior que o prazo do tipo da mensagem */
	public boolean validarPrazo(Integer prazo){
		Integer prazoMensagem = getPrazo();
		return (prazoMensagem != null && prazo == prazoMensagem);
	}
	
//	public Mensagem enviarMensagem(Usuario remetente, Usuario destinatario, String assunto, String conteudo, 
//			boolean avisarRemetente, boolean enviarEmail, boolean enviarSMS) throws Exception{
//		
//		Mensagem msg = new Mensagem(remetente, destinatario, getDescricao(), assunto, conteudo, avisarRemetente, enviarEmail, enviarSMS);
//		Cooperativa coop = Cooperativa.getInstance();
//		coop.tocarObjetos();
//		
//		String codigoEnvio = RetornoEnvioSMSEnum.CD012.getCodigo();
//		
//		//Enviando mensagem via SMS
//		String celular = destinatario.getCelular();
//		boolean recebeSMS = destinatario.isRecebeSMS();
//		
//		if(enviarSMS && recebeSMS && celular != null){
//			InformacaoSMS sms = coop.getInfoSms();
//			Integer limiteCaracteres = sms.getLimiteCaracteres();
//			
//			Assert.isTrue(conteudo.length() <= limiteCaracteres, MensagemErroEnum.TAMANHO_MAX_SMS.getMessage(
//					limiteCaracteres.toString(), String.valueOf(conteudo.length())));
//		
//			codigoEnvio = SMSService.enviarSMS(remetente.getNome(), sms.getLogin(), sms.getSenha(), 
//					coop.getPessoaJuridica().getFantasia(), conteudo, celular);
//		}
//		
//		//Enviando mensagem via e-mail
//		boolean recebeEmail = destinatario.isRecebeEmail();
//		if(enviarEmail && recebeEmail && destinatario.getEmail() != null){
//			InformacaoEmail infoEmail = coop.getInfoEmail();
//			boolean isEmailEnviado = msg.enviarEmail(infoEmail.getHost(), infoEmail.getPortaSmtp(), infoEmail.getEmail(), infoEmail.getSenha());
//			
//			if(isEmailEnviado){
//				codigoEnvio = RetornoEnvioSMSEnum.CD000.getCodigo();
//			}
//		}
//		
//		//Confirmando envio de mensagens pelo sistema
//		if(!enviarSMS && !enviarEmail){
//			codigoEnvio = RetornoEnvioSMSEnum.CD000.getCodigo();
//		}
//		
//		msg.setCodigoEnvio(codigoEnvio);
//		ImplDAO.save(msg);
//		
//		return msg;
//	}
	
	public static TipoMensagemEnum getTipoByDescricao(String descricao){
		for (TipoMensagemEnum tipo : TipoMensagemEnum.values()) {
			if(tipo.getDescricao().equals(descricao)){
				return tipo;
			}
		}
		return null;
	}
	
	public Integer getPrazo(){
		return this.prazo;
	}
	
	public String getDescricao(){
		return this.descricao;
	}
	
	public String getStyle(){
		return this.style;
	}
}
