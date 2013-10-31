package br.com.infowaypi.ecare.mensagem.alerta;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.msg.Mensagem;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Manager para o gerenciamento do trânsito de mensagens no sistema
 * @author Danilo Portela
 *
 */
public class ManagerMensagem {

	/** Caso a mensagem não tenha sido lida ainda, marca como lida e salva as alterações */
	public static boolean lerMensagem1aVez(Mensagem mensagem) throws Exception{
		if(!mensagem.getLida()){
			mensagem.setLida(true);
			ImplDAO.save(mensagem);
			return true;
		}
		return false;
	}
	
//	/** Envia uma mensagem de recebimento ao remetente de uma mensagem, caso a mesma necessita de confirmação de recebimento */
//	public static boolean avisarRecebimento(Mensagem mensagem) throws Exception{
//		if(mensagem.isAvisarRemetente()){
//			String assunto = MensagemUsuarioEnum.MENSAGEM_RECEBIDA.getMessage(mensagem.getDestinatario().getNome(), mensagem.getAssunto());
//			Mensagem resposta = new Mensagem(Cooperativa.getUsuario(), mensagem.getRemetente(), TipoMensagemEnum.INFORMACAO.getDescricao(),
//					MotivoEnum.RECEBIMENTO_MENSAGEM.getMessage(), assunto, false, mensagem.getEnviarEmail(), mensagem.getEnviarSMS());
//			
//			if(mensagem.enviarEmail()){
//				resposta.enviarEmail();
//			}
//			
//			ImplDAO.save(resposta);
//			return true;
//		}
//		return false;
//	}
	
	
	public static List<Mensagem> buscar(UsuarioInterface remetente, UsuarioInterface destinatario, TipoMensagemEnum tipo, Date data, Boolean lida){
		SearchAgent sa = new SearchAgent();
		
		if(remetente != null){
			sa.addParameter(new Equals("remetente", remetente));
		}
		
		if(destinatario != null){
			sa.addParameter(new Equals("destinatario", destinatario));
		}
		
		if(tipo != null){
			sa.addParameter(new Equals("tipoMensagem", tipo.getDescricao()));
		}
		
		if(data != null){
			Calendar dataCalendar = new GregorianCalendar();
			dataCalendar.setTime(data);
			dataCalendar.set(Calendar.HOUR_OF_DAY, 0);
			dataCalendar.set(Calendar.MINUTE, 0);
			dataCalendar.set(Calendar.SECOND, 0);
			sa.addParameter(new Equals("dataMensagem", dataCalendar.getTime()));
		}
		
		if(lida != null){
			sa.addParameter(new Equals("lida", lida));
		}
		return sa.list(Mensagem.class);
	}
	
//	public static Mensagem criarMensagem(UsuarioInterface remetente, UsuarioInterface destinatario, String tipo, String assunto, String conteudo, 
//			boolean avisarRemetente, boolean enviarEmail, boolean enviarSMS) throws Exception{
//		
//		Mensagem msg = new Mensagem(remetente, destinatario, tipo, assunto, conteudo, avisarRemetente, enviarEmail, enviarSMS);
//		ImplDAO.save(msg);
//		return msg;
//	}
//
//	public static Mensagem criarMensagemLeituraTiss(UsuarioInterface remetente, UsuarioInterface destinatario, String tipo, String assunto, String conteudo, 
//			boolean avisarRemetente, boolean enviarEmail, boolean enviarSMS) throws Exception{
//		
//		Mensagem msg = new Mensagem(remetente, destinatario, tipo, assunto, conteudo, avisarRemetente, enviarEmail, enviarSMS);
//		return msg;
//	}

//	public static Mensagem criarMensagemGuia(UsuarioInterface remetente, UsuarioInterface destinatario, String tipo, String assunto, String conteudo, 
//			boolean avisarRemetente, boolean enviarEmail, boolean enviarSMS, AbstractGuia guia) throws Exception{
//		
//		Mensagem msg = new MensagemGuia(remetente, destinatario, tipo, assunto, conteudo, avisarRemetente, enviarEmail, enviarSMS, guia, null);
//		ImplDAO.save(msg);
//		return msg;
//	}
//
//	public static Mensagem buildMensagemAnaliseGuia(Usuario remetente, Usuario destinatario, TipoAnaliseEnum tipoRegistro, AbstractGuia guia,
//		MensagemUsuarioEnum assunto, MensagemUsuarioEnum conteudo) throws Exception{
//
//		String numeroGuia = guia.getNumeroGuia();
//		return ManagerMensagem.criarMensagemGuia(Cooperativa.getUsuario(), destinatario, 
//				TipoMensagemEnum.INFORMACAO.getDescricao(), 
//				assunto.getMessage(tipoRegistro.getDescricao(), numeroGuia), 
//				conteudo.getMessage("", numeroGuia, guia.getBeneficiario().getNome(),
//				guia.getContratante().getPessoaJuridica().getFantasia(), 
//				Utils.format(guia.getDataLancamento()),
//				((AuditoriaGlosa)guia.getAuditorias().get(guia.getAuditorias().size()-1)).getAuditoria(),
//				remetente.getNome()),
//				false, false, false, guia);
//	}
//
//	public static MensagemGuia buildMensagemAuditoriaFatura(Usuario remetente, Usuario destinatario, TipoAnaliseEnum tipoRegistro, 
//			Set<AbstractGuia> guias, Faturamento fatura, MensagemUsuarioEnum assunto, MensagemUsuarioEnum conteudo) throws Exception{
//
//			StringBuilder numerosGuias = new StringBuilder();
//			String separador = ", ";
//			
//			for (AbstractGuia g : guias) {
//				numerosGuias.append(g.getNumeroGuia());
//				numerosGuias.append(separador);
//			}
//			
//			MensagemGuia msg = new MensagemGuia(Cooperativa.getUsuario(), destinatario, TipoMensagemEnum.INFORMACAO.getDescricao(), 
//					assunto.getMessage(tipoRegistro.getDescricao()), conteudo.getMessage(numerosGuias.toString()), false, false, false, null, fatura);
//			
//			ImplDAO.save(msg);
//			return msg;
//		}
//	
//
//	public static Mensagem buildMensagemLeituraTiss(UsuarioInterface remetente, UsuarioInterface destinatario, ReaderTiss reader,
//			MensagemUsuarioEnum assunto, String conteudo) throws Exception{
//		
//		return ManagerMensagem.criarMensagemLeituraTiss(remetente, destinatario, TipoMensagemEnum.INFORMACAO.getDescricao(), assunto.getMessage(), 
//				conteudo, false, false, false);
//	}
//	
//	public static boolean enviarMsgRepasse(List<Repasse> repasses) throws Exception{
//		String data = Utils.format(repasses.get(0).getDataGeracao(), "dd/MM");
//		
//		Cooperativa.PROCESS.info("Enviando mensagens SMS de repasse aos cooperados...");
//		for (Repasse repasse : repasses) {
//			if(repasse.isRepasse()){
//				
//				Usuario destinatario = (Usuario) repasse.getCooperado().getUsuario();
//				String celular = destinatario.getCelular();
//				String nome = destinatario.getNome().split(" ")[0];
//				String valor = repasse.getValorLiquido().toString();
//				String mensagem = MensagemUsuarioEnum.REPASSE_ENVIADO_PARA_BANCO.getMessage(nome, valor, data);
//				
//				if(!Utils.isStringVazia(celular)) {
//					Cooperativa.PROCESS.info(celular + " - " + nome);
//					TipoMensagemEnum.INFORMACAO.enviarMensagem(Cooperativa.getUsuario(), (Usuario) destinatario, 
//							"Repasse de Produção", mensagem, false, true, true);
//				}
//			}
//		}
//		return true;
//	}
//	
//	/** Constrói o corpo da mensagem a ser enviada a partir do template cadastrado no BD */
//	public static String getMessage(Long id, Integer quantidade, String ... params){
//		String mensagem = ManagerMensagemSistema.getMensagemById(id);
//		Assert.isNotNull(mensagem, "A mensagem de código " + id + " não foi configurada para o sistema. Entre em contato com o administrador.");
//		
//		for(int i = 0; i < quantidade; i++){
//			if(mensagem.contains("{" + i + "}")) {
//				String param = (params.length > i && params[i] != null) ? params[i] : "";
//				mensagem = mensagem.replace("{" + i + "}", param);
//			}
//		}
//		return mensagem;
//	}
//	
//	
//	public static void main(String[] args) throws Exception {
//		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new Equals("arquivoDeBanco.dataRepasse", Utils.parse("07/02/2011")));
//		List<Repasse> repasses = sa.list(Repasse.class);
//		
//		enviarMsgRepasse(repasses);
//	}
}
