package br.com.infowaypi.ecare.programaPrevencao;

import java.util.Date;

import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;

/**
 * classe que serve apenas de objeto de transporte de dados de um segurado para
 * se associar ou ser removido de um programa de prevenção
 */
public class SeguradoDoPrograma {
	
	private Long idSeguradoDoPrograma;
	private String nomeSegurado;
	private AbstractSegurado segurado;
	String numeroDoCartao;
	String cpf;
	private Boolean remover = false;
	private Date dataInsercao;
	private String motivo;
	private String descricao;

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Date getDataInsercao() {
		return dataInsercao;
	}

	public void setDataInsercao(Date dataInsercao) {
		this.dataInsercao = dataInsercao;
	}

	public Long getIdSeguradoDoPrograma() {
		return idSeguradoDoPrograma;
	}

	public void setIdSeguradoDoPrograma(Long idSeguradoDoPrograma) {
		this.idSeguradoDoPrograma = idSeguradoDoPrograma;
	}

	public AbstractSegurado getSegurado() {
		return segurado;
	}

	public void setSegurado(AbstractSegurado segurado) {
		this.segurado = segurado;
	}

	public String getNomeSegurado() {
		return nomeSegurado;
	}

	public void setNomeSegurado(String nomeSegurado) {
		this.nomeSegurado = nomeSegurado;
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

	public Boolean getRemover() {
		return remover;
	}

	public void setRemover(Boolean remover) {
		this.remover = remover;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
