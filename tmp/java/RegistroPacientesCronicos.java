package br.com.infowaypi.ecarebc.segurados;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.AbstractInformacaoAdicional;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 *  
 * @author Eduardo
 *
 */
@SuppressWarnings("serial")
public class RegistroPacientesCronicos extends AbstractInformacaoAdicional {

	private String titulo;
	private AbstractSegurado segurado;
	
	public RegistroPacientesCronicos(){
		this(new Date(), null, null, null);
	}
	
	public RegistroPacientesCronicos(Date dataDeCriacao, String titulo, String texto, UsuarioInterface usuario) {
		this.dataDeCriacao = dataDeCriacao;
		this.titulo = titulo;
		this.texto = texto;
		this.usuario = usuario;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public AbstractSegurado getSegurado() {
		return segurado;
	}

	public void setSegurado(AbstractSegurado segurado) {
		this.segurado = segurado;
	}

	/**
	 * Esse método deveria ser chamado apenas pelo jHeat. Por isso, ele está @Deprecated.
	 * 
	 * @param usuario
	 * @return
	 */
	@Deprecated
	public Boolean validate(UsuarioInterface usuario) {
		this.setUsuario(usuario);
		
		return super.validate();
	}
	
}
