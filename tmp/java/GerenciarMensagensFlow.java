package br.com.infowaypi.ecare.mensagem.alerta;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.msg.Mensagem;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Service para o gerenciamento das operações com mensagens no sistema
 * @author Danilo Portela
 *
 */
public class GerenciarMensagensFlow {

	/** Busca as mensagens do usuário da sessão apartir dos parâmetros informados  */
	public ResumoMensagens buscarMensagens(UsuarioInterface usuario, CategoriaMensagemEnum categoria, Date data){
		UsuarioInterface remetente = null, destinatario = null;
		Boolean lida = null;
		
		switch (categoria) {
			case ENVIADAS:
				remetente = usuario;
				break;
			
			case RECEBIDAS:
				destinatario = usuario;
				lida = true;
				break;
				
			case NOVAS:
				destinatario = usuario;
				lida = false;
				break;
				
			default:
				break;
		}
		 
		List<Mensagem> mensagens = ManagerMensagem.buscar(remetente, destinatario, null, data, lida);
		Assert.isNotEmpty(mensagens, MensagemErroEnum.NENHUMA_MENSAGEM_ENCONTRADA.getMessage(categoria.getDescricao()));
		return new ResumoMensagens(remetente, destinatario, null, categoria, data, mensagens);
	}
	
	/** Seleciona a mensagem a ser lida  */
	public ResumoMensagens lerMensagem(ResumoMensagens resumo) throws Exception{
		List<Mensagem> mensagensALer = resumo.getMensagensALer();
		
		int quantMensagens = mensagensALer.size();
		Assert.isTrue(quantMensagens <= 1, MensagemErroEnum.SELECIONAR_MENSAGEM_LER.getMessage());
		Assert.isTrue(quantMensagens != 0, MensagemErroEnum.MENSAGEM_NAO_SELECIONADA.getMessage());
		
		Mensagem msg = mensagensALer.get(0);
		boolean isRecebida = resumo.getCategoria().equals(CategoriaMensagemEnum.RECEBIDAS) 
			|| resumo.getCategoria().equals(CategoriaMensagemEnum.NOVAS);
		
		if(isRecebida){
			ManagerMensagem.lerMensagem1aVez(msg);
//			if(ManagerMensagem.lerMensagem1aVez(msg)){
//				ManagerMensagem.avisarRecebimento(msg);
//			}
		}
		
		msg.setLer(false);
		resumo.setMensagemLida(msg);
		return resumo;
	}
	
//	/** Cria e envia a mensagem pelo sistema, e-mail e/ou SMS para usuários comuns */
//	public ResumoMensagens criarMensagem(UsuarioInterface remetente, Usuario dest, Mensagem mensagem) throws Exception{
//		return criarMensagem(remetente, null, null, dest, mensagem);
//	}
		
//	/** Cria e envia a mensagem pelo sistema, e-mail e/ou SMS para usuários privilegiados */
//	public ResumoMensagens criarMensagem(UsuarioInterface remetente, String enviarPor, Role role, UsuarioInterface dest , Mensagem mensagem) throws Exception{
//		
//		
//		TipoMensagemEnum tipoMsg = (mensagem.getTipoMensagem() != null) ?
//			TipoMensagemEnum.getTipoByDescricao(mensagem.getTipoMensagem()) :
//			TipoMensagemEnum.INFORMACAO;
//
//		Assert.isFalse(dest == null && role == null, MensagemErroEnum.INFORMAR_PELO_MENOS_UM_DOS_PARAMETROS.getMessage("Usuário", "Role"));
//		
//		//Carregando destinatários da mensagem
//		List<Usuario> destinatarios = new ArrayList<Usuario>();
//		if(enviarPor.equals(EnviarParaEnum.USUARIO)){
//			destinatarios.add((Usuario) dest);
//			Assert.isFalse(remetente.equals(dest), MensagemErroEnum.DESTINATARIO_MENSAGEM_IGUAL_REMETENTE.getMessage());
//		}
//		
//		if(enviarPor.equals(EnviarParaEnum.GRUPO)){
//			String valorRole = role.getValor();
//			destinatarios.addAll(UsuarioManager.getUsuariosByRole(valorRole));
//			
//			if(remetente.isPossuiRole(valorRole)){
//				destinatarios.remove(remetente);
//			}
//		}
//		
//		Boolean enviarEmail = mensagem.getEnviarEmail();
//		Boolean enviarSMS = mensagem.getEnviarSMS();
//		ResumoMensagens resumo = new ResumoMensagens(remetente, tipoMsg, mensagem.getAssunto(), mensagem.getCorpo(), new Date(), 
//				false, false);
//		
//		//Enviando a(s) mensagem(ns) aos destinatários
//		for (Usuario destinatario : destinatarios) {
//			if(destinatario.getStatus().equals(UsuarioInterface.ATIVO)){
//				
//				Mensagem msg = tipoMsg.enviarMensagem((Usuario) remetente, destinatario, mensagem.getAssunto(), mensagem.getCorpo(), 
//						mensagem.isAvisarRemetente(), false, false);
//				
//				if(msg.getCodigoEnvio().equals(RetornoEnvioSMSEnum.CD000.getCodigo())){
//					resumo.getMensagens().add(msg);
//				}else{
//					resumo.getMensagensNaoEnviadas().add(msg);
//				}
//			}
//		}
//		
//		return resumo;
//	}
	
	public void finalizar(Mensagem mensagem){} 
	
}
