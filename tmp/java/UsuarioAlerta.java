package br.com.infowaypi.ecare.mensagem.alerta;

import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.msg.Mensagem;
import br.com.infowaypi.msr.user.Usuario;

public class UsuarioAlerta extends Usuario{
	
	private static final long serialVersionUID = 1L;

	@Override
	public int getNumeroMensagens(String idUsuario) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("destinatario.idUsuario",Long.parseLong(idUsuario)));
		sa.addParameter(new Equals("lida",false));
		
		return sa.resultCount(Mensagem.class);
	}

}
