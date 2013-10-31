package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe que representa uma informa��o adicional a qualquer elemento do modelo. </br>
 * Como, por exemplo, as observa��es e registros t�cnicos de auditoria que servem para guias e
 * registro de pacientes cr�nicos para segurado.
 * 
 * @author Eduardo
 *
 */
public abstract class AbstractInformacaoAdicional implements Serializable {

	/**
	 * Representa o id que ser� salvo no banco, tem esse nome devido ao <i>refactor -> pull up</i> 
	 * do eclipse que foi feito em <code>AbtractObserva��o.class</code>
	 */
	private Long idObservacao;
	/**
	 * Texto que cont�m a <del>observa��o</del> <ins>informa��o adicional</ins>. 
	 */
	protected String texto;
	/**
	 * Data na qual a <del>observa��o</del> <ins>informa��o adicional</ins> foi inserida.
	 */
	protected Date dataDeCriacao;
	/**
	 * Usu�rio que inseriu a <del>observa��o</del> <ins>informa��o adicional</ins>.
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
	 * M�todo que carrega os campos do objeto que s�o requeridos na sess�o do Hibernate.
	 */
	public void tocarObjetos() {
		this.getTexto();
		this.getDataDeCriacao();
	}

	/**
	 * Valida��o que impede a inser��o de uma <del>observa��o</del> <ins>informa��o adicional</ins> vazia.
	 * @return Boolean
	 */
	public Boolean validate(){
		Assert.isNotEmpty(texto, "Digite um texto para a observa��o!");
		return true;
	}

}