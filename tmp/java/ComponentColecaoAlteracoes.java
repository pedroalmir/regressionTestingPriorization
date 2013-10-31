package br.com.infowaypi.ecarebc.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import br.com.infowaypi.msr.user.UsuarioInterface;

public class ComponentColecaoAlteracoes implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long idColecaoAlteracoes;
	
	private Set<Alteracao> alteracoes;
	
	public ComponentColecaoAlteracoes() {
		super();
		alteracoes = new LinkedHashSet<Alteracao>();
	}

	public Long getIdColecaoAlteracoes() {
		return idColecaoAlteracoes;
	}

	public void setIdColecaoAlteracoes(Long idColecaoAlteracoes) {
		this.idColecaoAlteracoes = idColecaoAlteracoes;
	}

	public Set<Alteracao> getAlteracoes() {
		return alteracoes;
	}

	public void setAlteracoes(Set<Alteracao> alteracoes) {
		this.alteracoes = alteracoes;
	}
	
	public void adicionarAlteracao(String motivo, UsuarioInterface usuario, Date data) {
		Alteracao item = new Alteracao();
		povoarAlteracao(usuario, motivo, data, item);
		guardarAlteracao(item);
	}

	private void guardarAlteracao(Alteracao item) {
		item.setOrdem(this.getAlteracoes().size() + 1);
		this.getAlteracoes().add(item);
	}
	
	private void povoarAlteracao(UsuarioInterface usuario, String motivo, Date data, Alteracao item){
		item.setMotivo(motivo);
		item.setUsuario(usuario);
		if (data != null) {
			item.setData(data);
		}
	}

}
