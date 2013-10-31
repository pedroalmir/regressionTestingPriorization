package br.com.infowaypi.ecarebc.procedimentos;

import java.io.Serializable;

public class AreaEspecialidadeCBHPM implements Serializable {
	
	private Long idAreaEspecialidadeCBHPM;
	private String descricao;

	public Long getIdAreaEspecialidadeCBHPM() {
		return idAreaEspecialidadeCBHPM;
	}

	public void setIdAreaEspecialidadeCBHPM(Long id) {
		this.idAreaEspecialidadeCBHPM = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
