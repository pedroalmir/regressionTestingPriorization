package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecarebc.odonto.enums.FaceEnum;


/**
 * Classe que representa uma face de um dente no sistema odontológico
 * @author Danilo Nogueira Portela
 *
 */
public class Face implements Serializable, EstruturaOdontoInterface{
	
	private static final long serialVersionUID = -1525675583543192574L;
	
	public long idFace;
	private String descricao;
	public TipoFace tipoFace;
	public Dente dente;
	
	public Face(){
	}
	
	public Face(TipoFace tipoFace){
		this();
		this.tipoFace = tipoFace;
	}
	
	public TipoFace getTipoFace() {
		return tipoFace;
	}

	public void setTipoFace(TipoFace tipoFace) {
		this.tipoFace = tipoFace;
	}

	public long getIdFace() {
		return idFace;
	}

	public void setIdFace(long idFace) {
		this.idFace = idFace;
	}

	public Dente getDente() {
		return dente;
	}

	public void setDente(Dente dente) {
		this.dente = dente;
	}
	
	public Boolean validate(){
		this.setDescricao(this.getTipoFace().getTipo().substring(0,1) + " - " + this.getTipoFace().getTipo());
		return true;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricaoFormatada(){
		return (this.getDescricao().equals(FaceEnum.TODAS.getDescricao())) ? "-" : this.getDescricao().substring(0,1);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Face))
			return false;
		
		Face face = (Face) obj;
		return new EqualsBuilder()
		.append(this.getIdFace(), face.getIdFace())
		.append(this.getDescricao(), face.getDescricao())
		.isEquals();
		
	}
	
}

