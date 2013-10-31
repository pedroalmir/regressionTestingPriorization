package br.com.infowaypi.ecare.programaPrevencao;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.enums.TipoEventoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;

/**
 * @author SR Team - Marcos Roberto 15.02.2012
 * 
 * Entidade respons�vel por encapsular as informa��es referentes ao evento, associa��o 
 * a um programa de preven��o a saude, e a associa��o dos benefici�rios.
 */
public class Evento implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idEvento;
	private String titulo;
	private String descricao;
	private Integer tipo;
	private Date data;
	
	private Set<AbstractSegurado> beneficiarios;
	
	/**
	 * @author SR Team - Marcos Roberto 23.02.2012
	 * Lista de benefici�rios 
	 */
	private transient Set<SeguradoDoEvento> beneficiariosTransient;
	
	public Evento() {
		beneficiarios = new HashSet<AbstractSegurado>();
		beneficiariosTransient = new HashSet<SeguradoDoEvento>();
	}
	
	public Long getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(Long idEvento) {
		this.idEvento = idEvento;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Set<AbstractSegurado> getBeneficiarios() {
		return beneficiarios;
	}

	public void setBeneficiarios(Set<AbstractSegurado> beneficiarios) {
		this.beneficiarios = beneficiarios;
	}

	public Set<SeguradoDoEvento> getBeneficiariosTransient() {
		return beneficiariosTransient;
	}

	public void setBeneficiariosTransient(
			Set<SeguradoDoEvento> beneficiariosTransient) {
		this.beneficiariosTransient = beneficiariosTransient;
	}
	
	public String getTipoDescricao() {
		return TipoEventoEnum.descricao(tipo);
	}
}