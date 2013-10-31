package br.com.infowaypi.ecarebc.procedimentos;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TipoAcomodacao implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long idTipoAcomodacao;
	private String descricao;
	
	public Long getIdTipoAcomodacao() {
		return idTipoAcomodacao;
	}
	public void setIdTipoAcomodacao(Long idTipoAcomodacao) {
		this.idTipoAcomodacao = idTipoAcomodacao;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TipoAcomodacao)) {
			return false;
		}
		
		TipoAcomodacao local = (TipoAcomodacao)obj;
		
		boolean isEquals = new EqualsBuilder()
			.append(this.descricao, local.getDescricao())
			.isEquals();
		
		return isEquals;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.descricao)
		.toHashCode();
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public static TipoAcomodacao createAcomodacao(String descricao) {
		TipoAcomodacao result = new TipoAcomodacao();
		
		result.setDescricao(descricao);
		
		return result;
	}
}
