package br.com.infowaypi.ecare.scheduller.sms;

import java.util.LinkedList;
import java.util.List;
import java.util.PropertyResourceBundle;

import br.com.infowaypi.config.Config;

public class SMSSender implements Runnable {
	
	private static SMSSender smsSender;
	private boolean sendTest = false;
	private List<MensagemAvisoRegulador> mensagens;

	
	public SMSSender() {
		mensagens = new LinkedList<MensagemAvisoRegulador>();
	}
	
	public static SMSSender getInstance(boolean test){
		String key = PropertyResourceBundle.getBundle( "sms" ).getString( "dbmkt.key" );
		if (smsSender == null)
			return new DBmktSMSSender(key, test);
		else
			return smsSender;
	}

	@Override
	public void run() {
		send();
	}

	protected void send(){
		SMSSender sender = SMSSender.getInstance(this.isSendTest());
		sender.setMensagens(mensagens);
		sender.send();
	}
	
	/*
	*******************************************************************************
	*******************************************************************************
	************** 2º Método mais importante do mundo *****************************
	****** O SMS não foram enviados pois esse não é o servidor Uniplam ************
	****** Para enviar SMS Altere o ip padrão do servidor no Config.java **********
	*******************************************************************************
	******************************************************************************* 
	*/
	
	public void sendAll() {
		if (Config.verificarHostName().equals(Config.HOST)){
			run();
		} else {
			sendAllWithouThreads();
		}
	}
	
	public void sendAllWithouThreads() {
		this.sendTest = true;
		send();
	}

	public int count() {
		return mensagens.size();
	}

	public void addSMS(MensagemAvisoRegulador sms) {
		mensagens.add(sms);
	}

	public List<MensagemAvisoRegulador> getMensagens() {
		return mensagens;
	}
	
	public void setMensagens(List<MensagemAvisoRegulador> mensagens) {
		this.mensagens = mensagens;
	}
	
	public boolean isSendTest() {
		return sendTest;
	}

	public void setSendTest(boolean sendTest) {
		this.sendTest = sendTest;
	}

}
