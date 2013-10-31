package br.com.infowaypi.ecare.segurados;

import java.util.Set;

import br.com.infowaypi.msr.user.UsuarioInterface;

public class DependenteRelacionamento extends Dependente{
	
	
	private static final long serialVersionUID = 1L;

	public DependenteRelacionamento() {
		super();
	}
	
	public DependenteRelacionamento(UsuarioInterface usuario) {
		super(usuario);
	}
	
	@Override
	public int getCarenciaCumprida() {
		return super.getCarenciaCumprida();
	}
	
	@Override
	public int getCarenciaRestanteUrgencias() {
		return super.getCarenciaRestanteUrgencias();
	}

}
