package br.com.infowaypi.ecare.segurados;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.msr.user.UsuarioInterface;

public class Regularizacao implements Serializable, Comparable<Regularizacao>{

	private static final long serialVersionUID = 1L;
	
	private Long idRegularizacao;
	private Date dataRegularizacao;
	private UsuarioInterface usuario;
	private DependenteInterface dependente;
	
	public Regularizacao() {}

	public Regularizacao(UsuarioInterface usuario, DependenteInterface dependente) {
		this.usuario = usuario;
		this.dependente = dependente;
		this.dataRegularizacao = new Date();
	}
	
	public Regularizacao(UsuarioInterface usuario, DependenteInterface dependente, Date dataRegularizacao) {
		this.usuario = usuario;
		this.dependente = dependente;
		this.dataRegularizacao = dataRegularizacao;
	}
	
	public Long getIdRegularizacao() {
		return idRegularizacao;
	}
	public void setIdRegularizacao(Long idRegularizacao) {
		this.idRegularizacao = idRegularizacao;
	}
	public Date getDataRegularizacao() {
		return dataRegularizacao;
	}
	public void setDataRegularizacao(Date dataRegularizacao) {
		this.dataRegularizacao = dataRegularizacao;
	}
	public UsuarioInterface getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}

	public DependenteInterface getDependente() {
		return dependente;
	}

	public void setDependente(DependenteInterface dependente) {
		this.dependente = dependente;
	}

	public int compareTo(Regularizacao reg) {
		return this.getDataRegularizacao().compareTo(reg.getDataRegularizacao()) * -1;
	}
}