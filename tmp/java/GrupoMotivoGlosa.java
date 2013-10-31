package br.com.infowaypi.ecare.cadastros;

import java.io.Serializable;

public class GrupoMotivoGlosa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long idGrupo;
	private String descricao;
	private MotivoGlosa motivoGlosa;
	
	public Long getIdGrupo() {
		return idGrupo;
	}
	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public MotivoGlosa getMotivoGlosa() {
		return motivoGlosa;
	}
	public void setMotivoGlosa(MotivoGlosa motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}
}
