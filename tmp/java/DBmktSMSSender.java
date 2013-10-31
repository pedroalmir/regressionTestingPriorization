package br.com.infowaypi.ecare.scheduller.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para envio de SMS via <a href="http://www.dbmkt.com.br/">DBmkt mobile marketing</a>
 * @author Alan Santos
 *
 */
public class DBmktSMSSender extends SMSSender {
	
	private static final String DBMKT_SEND_URL_DOMAIN = "www.dbmkt.com.br";
	private static final String DBMKT_SEND_URL_SUBDOMAIN = "/__envio/gw.php";
	
	private static final String DBMKT_NEWSLETTER_URL = "http://www.dbmkt.com.br/cadastros/gateway.php";
	private static final String DBMKT_BALANCE_URL = "http://www.dbmkt.com.br/consulta_creditos.php";
	
	private String chave;
	
	public DBmktSMSSender( String chave , boolean test ) {
		this.chave = chave;
		this.setSendTest(test);
	}

	public void addNewsLetter() {
		String nome  = "ponha aqui o nome do usuario a ser cadastrado no newsletter"; 
		String email = "ponha aqui o e-mail do usuario a ser cadastrado no newsletter"; 
		String grupo = "ponha aqui o nome dos grupos com os quais o usuario será associado, separados por vírgula";
		try { 
			URL teste = new URL(DBMKT_NEWSLETTER_URL + "?chave=" + chave + "&email=" + email + "&nome=" + nome + "&grupo=" + grupo + "&ativo=1&acao=adicionar"); 
			BufferedReader in = new BufferedReader(new InputStreamReader(teste.openStream()));
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
	}
	
	/**
	 * @param remetente Nome ou telefone do remetente, com até 11 caracteres alfanuméricos ou 14 numéricos.
	 * @param tel DDD e telefone do destinatário.
	 * @param msg Mmensagem a ser enviada, com até 160 caracteres alfanuméricos incluindo !@#$%&*()-_+=.,;?/
	 * @param data (opcional) Data programada para o envio no formato DD/MM/AAAA
	 * @param hora (opcional) Hora programada para o envio no formato 00:00, sendo a hora de 00 a 23.
	 * @return O resultado será um código numérico que indica o código de confirmação do envio, ou a mensagem de erro em caso de falha.
	 */
	public String send(String remetente, String phone, String msg, String data, String hora) {
		
		if( data == null ) data = Utils.format(new Date(), "dd/MM/yyyy");
		if( hora == null ) hora = getHora();
		
		String params = "chave=" + chave + "&de=" + remetente + "&tel=" + phone + "&msg=" + msg + "&data=" + data + "&hora=" + hora;
		
		GetMethod http = null;
		try {
			http = new GetMethod( new URI( "http", DBMKT_SEND_URL_DOMAIN , DBMKT_SEND_URL_SUBDOMAIN , params, null).toURL().toString() );
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		try {
			
			HttpClient httpclient = new HttpClient();
			httpclient.executeMethod(http);
			return http.getResponseBodyAsString();
		}	catch (IOException e) {
			return "Houve um problema durante a conexão com o servidor de envio SMS.\n" + e.getMessage();
		}
		
	}
	
	public void consultarSaldo(String chave, String tipo){
		try { 
			URL teste = new URL(DBMKT_BALANCE_URL + "?chave=" + chave + "&tipo=" + tipo); 
			BufferedReader in = new BufferedReader(new InputStreamReader(teste.openStream())); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getHora() {
		int hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int min  = Calendar.getInstance().get(Calendar.MINUTE);
		
		return hora + ":" + min;
	}
	
	/**
	 * Envia e salva a mensagem, caso o envio seja bem sucedido.
	 * @throws IOException 
	 */
	protected void send() {
		
		for (MensagemAvisoRegulador sms : this.getMensagens()) {
			
			String response = "0";
			String destino = SMSService.retirarCaracteres(sms.getDestino(), '(', ')', '-', ' ', '*');
			if (!this.isSendTest()) {
				response = send( "Uniplam" , destino, Utils.removeAccents(sms.getConteudo()) , null , null);
			}
			
			if ( Integer.valueOf( response ) > 0 ) {
				try {
					ImplDAO.save(sms);
				} catch (Exception e) {
					System.out.println("DBmktSMSSender.send(): Erro ao salvar sms. Tipo: SMS. nº: " + sms.getDestino());
				}
			} else {
				try {
				    MailSender.reportarErroAosDesenvolvedores("Erro no envio de SMS", "Atenção!!!! Falha no servidor ao enviar SMS. Tempo de envio excedido.\nResponse: "+response);
				}
				catch(Exception e) {
				    e.printStackTrace();
				}
			}
		}
	}
	
}


