package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;

import br.com.infowaypi.ecarebc.odonto.enums.TipoDenteEnum;

/**
 * Classe que representa os tipos de dentes no sistema odontológico
 * @author Danilo Nogueira Portela
 *
 */
public class TipoDente implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long idTipoDente;
	private TipoDenteEnum tipo;
	
	public TipoDente(){
		this.tipo = TipoDenteEnum.NENHUM;
	}
	
	public TipoDente(TipoDenteEnum tipo){
		this.tipo = tipo;
	}
	
	public String getTipo() {
		return tipo.getDescricao();
	}

	public void setTipo(String nome) {
		for (TipoDenteEnum d : TipoDenteEnum.values()) {
			if(nome.equals(d.getDescricao()))
				this.tipo = d;
		}
	}

	public Long getIdTipoDente() {
		return idTipoDente;
	}

	public void setIdTipoDente(Long idTipoDente) {
		this.idTipoDente = idTipoDente;
	}
	
	
}
