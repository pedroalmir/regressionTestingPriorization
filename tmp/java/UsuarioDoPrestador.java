package br.com.infowaypi.ecarebc.associados;

import br.com.infowaypi.msr.user.Usuario;

/**
 * Classe que representa um usuário de um prestador do sistema.
 * @author root
 * @changes Danilo Nogueira Portela
 */
public class UsuarioDoPrestador extends Usuario {

	private static final long serialVersionUID = 1L;
	private Long idUsuarioDoPrestador;
	private Prestador prestador;
	 
	public UsuarioDoPrestador(){
		super();
	}
	
	public Long getIdUsuarioDoPrestador() {
		return idUsuarioDoPrestador;
	}
	
	public void setIdUsuarioDoPrestador(Long idUsuarioDoPrestador) {
		this.idUsuarioDoPrestador = idUsuarioDoPrestador;
	}
	
	public Prestador getPrestador() {
		return prestador;
	}
	
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	
}
