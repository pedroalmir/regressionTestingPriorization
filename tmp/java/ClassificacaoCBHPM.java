package br.com.infowaypi.ecarebc.procedimentos;

import java.io.Serializable;

@SuppressWarnings("serial")
public abstract class ClassificacaoCBHPM implements Serializable {

	private Long idClassificacaoCBHPM;
	private String codigo;
	private String descricao;
	private String codigoDescricao;
	
	public ClassificacaoCBHPM() {}

	public Long getIdClassificacaoCBHPM() {
		return idClassificacaoCBHPM;
	}

	public void setIdClassificacaoCBHPM(Long idClassificacaoCBHPM) {
		this.idClassificacaoCBHPM = idClassificacaoCBHPM;
	}
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getCodigoDescricao() {
		return this.codigoDescricao;
	}
	
	public void setCodigoDescricao(String codigoDescricao) {
		this.codigoDescricao = codigoDescricao;
	}
	
	public Boolean validate() throws Exception {
		this.setCodigoDescricao(this.toString());
		return Boolean.TRUE;
	}
}
