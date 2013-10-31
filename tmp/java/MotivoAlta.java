package br.com.infowaypi.ecarebc.atendimentos.alta;

import java.io.Serializable;

import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class MotivoAlta implements Serializable {

	private Long idMotivoAlta;
	private String codigo;
	private String descricao;
	private String grupo;
	private String descricaoCompleta;
	
	
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	public Long getIdMotivoAlta() {
		return idMotivoAlta;
	}
	public void setIdMotivoAlta(Long idMotivoAlta) {
		this.idMotivoAlta = idMotivoAlta;
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
	public String getDescricaoCompleta() {
		return descricaoCompleta;
	}
	public void setDescricaoCompleta(String descricaoCompleta) {
		this.descricaoCompleta = descricaoCompleta;
	}
	
	public Boolean validate() throws ValidateException {
		this.descricaoCompleta = this.grupo+" - "+this.descricao;
		
		if(Utils.isStringVazia(this.getCodigo()))
			throw new ValidateException("O Codigo deve ser informado.");

		if(Utils.isStringVazia(this.getDescricao()))
			throw new ValidateException("A Descrição do motivo deve ser informada.");

		if(Utils.isCampoDuplicado(this, "codigo", this.getCodigo()))
			throw new ValidateException("O Codigo informado já existe.");
		
		else {
			return true;
		}

	}
}
