package br.com.infowaypi.ecare.mensagem.alerta;

import br.com.infowaypi.msr.msg.Mensagem;
import br.com.infowaypi.msr.user.UsuarioInterface;

public enum CategoriaMensagemEnum {

	
	NOVAS("Não Lidas") {
		@Override
		public UsuarioInterface getUsuario(Mensagem mensagem) {
			return mensagem.getDestinatario();
		}
	},
	RECEBIDAS("Recebidas") {
		@Override
		public UsuarioInterface getUsuario(Mensagem mensagem) {
			return mensagem.getDestinatario();
		}
	},
	ENVIADAS("Enviadas") {
		@Override
		public UsuarioInterface getUsuario(Mensagem mensagem) {
			return mensagem.getRemetente();
		}
	},
	APAGADAS("Lixeira") {
		@Override
		public UsuarioInterface getUsuario(Mensagem mensagem) {
			return null;
		}
	};
	
	private String descricao;
	
	public abstract UsuarioInterface getUsuario(Mensagem mensagem);
	
	private CategoriaMensagemEnum(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

}
