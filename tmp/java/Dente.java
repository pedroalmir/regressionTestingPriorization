package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.odonto.enums.DenteEnum;
import br.com.infowaypi.molecular.SearchAgent;

/**
 * Classe que representa um dente.
 * @author Danilo Nogueira Portela
 */

public class Dente implements  Serializable, EstruturaOdontoInterface{

	private static final long serialVersionUID = -2979068248967928390L;
	
	private long idDente;
	private String descricao;
	private Quadrante quadrante;
	private TipoDente tipoDente;
	private DenteEnum especificacao;
	
	//Faces
	private Face mesial;
	private Face oclusal_Incisiva;
	private Face lingual_Palatina;
	private Face vestibular;
	private Face distal;

	public Dente() {
	}
	
	public Dente(TipoDente tipo) {
		this();
		this.tipoDente = tipo;
	}
	
	public Face getDistal() {
		return distal;
	}

	public void setDistal(Face distal) {
		this.distal = distal;
	}

	public Face getLingual_Palatina() {
		return lingual_Palatina;
	}

	public void setLingual_Palatina(Face lingual_Palatina) {
		this.lingual_Palatina = lingual_Palatina;
	}

	public Face getMesial() {
		return mesial;
	}

	public void setMesial(Face mesial) {
		this.mesial = mesial;
	}

	public Face getOclusal_Incisiva() {
		return oclusal_Incisiva;
	}

	public void setOclusal_Incisiva(Face oclusal_Incisiva) {
		this.oclusal_Incisiva = oclusal_Incisiva;
	}

	public Face getVestibular() {
		return vestibular;
	}

	public void setVestibular(Face vestibular) {
		this.vestibular = vestibular;
	}

	public Integer getNumero() {
		return especificacao.getNumero();
	}

	public void setNumero(Integer numero) {
		for (DenteEnum d : DenteEnum.values()) {
			if(numero.equals(d.getNumero()))
				this.especificacao = d;
		}
	}
	
	public TipoDente getTipoDente() {
		return tipoDente;
	}

	public void setTipoDente(TipoDente tipoDente) {
		this.tipoDente = tipoDente;
	}

	public long getIdDente() {
		return idDente;
	}

	public void setIdDente(long idDente) {
		this.idDente = idDente;
	}

	public Quadrante getQuadrante() {
		return quadrante;
	}

	public void setQuadrante(Quadrante quadrante) {
		this.quadrante = quadrante;
	}
	
	public Boolean validate(){
		this.setDescricao(this.getNumero() + " - " + this.getTipoDente().getTipo());
		return true;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricaoFormatada(){
		String descricao = "- ";
			if(!this.getNumero().equals(DenteEnum.TODOS.getNumero()))
				descricao = this.getNumero().toString();
		return descricao;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Dente))
			return false;
		
		Dente dente = (Dente) obj;

		return new EqualsBuilder()
		.append(this.getDescricao(), dente.getDescricao())
		.isEquals();
		
	}
	
	public List<Face> getFaces(Dente dente){
		if (dente == null)
			return new ArrayList<Face>();
		
		SearchAgent sa = new SearchAgent();
		Criteria criteria = sa.createCriteriaFor(Face.class);
		criteria.add(Expression.or(Expression.eq("dente", dente), Expression.isNull("dente")));
		return criteria.list();
	}

}