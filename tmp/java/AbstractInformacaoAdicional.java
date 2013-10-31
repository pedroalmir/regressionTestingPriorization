package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe que representa uma informação adicional a qualquer elemento do modelo. </br>
 * Como, por exemplo, as observações e registros técnicos de auditoria que servem para guias e
 * registro de pacientes crônicos para segurado.
 * 
 * @author Eduardo
 *
 */
public abstract class AbstractInformacaoAdicional implements Serializable {

	/**
	 * Representa o id que será salvo no banco, tem esse nome devido ao <i>refactor -> pull up</i> 
	 * do eclipse que foi feito em <code>AbtractObservação.class</code>
	 */
	private Long idObservacao;
	/**
	 * Texto que contém a <del>observação</del> <ins>informação adicional</ins>. 
	 */
	protected String texto;
	/**
	 * Data na qual a <del>observação</del> <ins>informação adicional</ins> foi inserida.
	 */
	protected Date dataDeCriacao;
	/**
	 * Usuário que inseriu a <del>observação</del> <ins>informação adicional</ins>.
	 */
	protected UsuarioInterface usuario;

	public AbstractInformacaoAdicional() {
		super();
	}

	public Date getDataDeCriacao() {
		return dataDeCriacao;
	}

	public void setDataDeCriacao(Date dataDeCriacao) {
		this.dataDeCriacao = dataDeCriacao;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}

	public Long getIdObservacao() {
		return idObservacao;
	}

	public void setIdObservacao(Long idObservacao) {
		this.idObservacao = idObservacao;
	}

	/**
	 * Método que carrega os campos do objeto que são requeridos na sessão do Hibernate.
	 */
	public void tocarObjetos() {
		this.getTexto();
		this.getDataDeCriacao();
	}

	/**
	 * Validação que impede a inserção de uma <del>observação</del> <ins>informação adicional</ins> vazia.
	 * @return Boolean
	 */
	public Boolean validate(){
		Assert.isNotEmpty(texto, "Digite um texto para a observação!");
		return true;
	}

}