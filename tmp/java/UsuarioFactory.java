package br.com.infowaypi.ecarebc.associados.factory;

import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * 
 * @author Emanuel
 *
 */
public class UsuarioFactory {

	public UsuarioInterface criarUsuario(String login, String role, String nome){
		UsuarioInterface usuario = new Usuario();
		usuario.setLogin(login);
		usuario.setRole(role);
		usuario.setNome(nome.length() > 100 ? nome.substring(0, 99) : nome);
		usuario.setStatus(Usuario.ATIVO);
		((Usuario) usuario).setRecebeMensagem(false);
		return usuario;
	}
}
