package br.com.infowaypi.ecare.cadastros;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Classe criada para representar o Item Aplicável aos Motivos de Glosa. 
 * @author Luciano Rocha
 *
 */
public class ItemAplicavel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long idItemAplicavel;
	private String descricao;
	
	public ItemAplicavel() {
	}

	public Long getIdItemAplicavel() {
		return idItemAplicavel;
	}

	public void setIdItemAplicavel(Long idItemAplicavel) {
		this.idItemAplicavel = idItemAplicavel;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public int hashCode() {
		return descricao.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder().append(descricao, obj).isEquals();
	}
	
}
