package br.com.infowaypi.ecarebc.segurados;

import java.io.Serializable;

import br.com.infowaypi.msr.exceptions.ValidateException;

public class SubGrupo implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idSubGrupo;
	private String codigoLegado;
	private String descricao;
	private GrupoBC grupoBC;
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getIdSubGrupo() {
		return idSubGrupo;
	}

	public void setIdSubGrupo(Long idSubGrupo) {
		this.idSubGrupo = idSubGrupo;
	}

	public String getCodigoLegado() {
		return codigoLegado;
	}

	public void setCodigoLegado(String codigoLegado) {
		this.codigoLegado = codigoLegado;
	}

	public GrupoBC getGrupo() {
		return grupoBC;
	}
	
	public void setGrupo(GrupoBC grupoBC) {
		this.grupoBC = grupoBC;
	}

	public Boolean validate() throws ValidateException {
		return true;
	}

}