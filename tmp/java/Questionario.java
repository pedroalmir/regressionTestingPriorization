package br.com.infowaypi.ecarebc.produto;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Representa uma entidade questionario.
 * @author eCare Team
 */
public class Questionario implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idQuestionario;
	
	/** Descricao do Questionario */
	private String descricao;

	private Date dataCadastro;
	
	private int versao;
	
	private Produto produto;
	
	/** Colecao de perguntas */
	private Set<Pergunta> perguntas;

	private boolean ativo;
	
	public Questionario(){
		this.dataCadastro = new Date();
		this.perguntas = new HashSet<Pergunta>();
	}
	
	public Long getIdQuestionario() {
		return idQuestionario;
	}

	public void setIdQuestionario(Long idQuestionario) {
		this.idQuestionario = idQuestionario;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public int getVersao() {
		return versao;
	}

	public void setVersao(int versao) {
		this.versao = versao;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public Set<Pergunta> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(Set<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}
	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean validate(){
		this.produto.setQuestionario(this);
		return true;
	}
}