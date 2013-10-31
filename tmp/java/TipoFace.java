package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;

import br.com.infowaypi.ecarebc.odonto.enums.FaceEnum;

/**
 * Classe que representa os tipos de faces no sistema odontológico
 * @author Danilo Nogueira Portela
 *
 */
public class TipoFace implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long idTipoFace;
	private FaceEnum tipo;
	
	public TipoFace(){
		this.tipo = FaceEnum.NENHUMA;
	}
	
	public TipoFace(FaceEnum tipo){
		this.tipo = tipo;
	}
	
	public String getTipo() {
		return tipo.getDescricao();
	}

	public void setTipo(String nome) {
		for (FaceEnum f : FaceEnum.values()) {
			if(nome.equals(f.getDescricao()))
				this.tipo = f;
		}
	}

	public Long getIdTipoFace() {
		return idTipoFace;
	}

	public void setIdTipoFace(Long idTipoFace) {
		this.idTipoFace = idTipoFace;
	}

}
