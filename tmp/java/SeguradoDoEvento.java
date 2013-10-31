package br.com.infowaypi.ecare.programaPrevencao;

import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;

/**
 * Classe que serve apenas de objeto de transporte de dados de um segurado para
 * se associar ou ser removido de um evento.
 */
public class SeguradoDoEvento {

	private Long idSeguradoDoEvento;
	
	private String nome;
	private AbstractSegurado segurado;
	private String numeroDoCartao;
	private String cpf;

	public Long getIdSeguradoDoEvento() {
		return idSeguradoDoEvento;
	}

	public void setIdSeguradoDoEvento(Long idSeguradoDoEvento) {
		this.idSeguradoDoEvento = idSeguradoDoEvento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public AbstractSegurado getSegurado() {
		return segurado;
	}

	public void setSegurado(AbstractSegurado segurado) {
		this.segurado = segurado;
	}

	public String getNumeroDoCartao() {
		return numeroDoCartao;
	}

	public void setNumeroDoCartao(String numeroDoCartao) {
		this.numeroDoCartao = numeroDoCartao;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
}