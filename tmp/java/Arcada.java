package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.odonto.enums.PosicaoEnum;
import br.com.infowaypi.molecular.SearchAgent;

/**
 * Classe que representa uma arcada no sistema odontológico.
 * @author Danilo Nogueira Portela
 */

public class Arcada implements Serializable, EstruturaOdontoInterface{

	private static final long serialVersionUID = 1L;
	
	private long idArcada;
	private String descricao;
	private Denticao denticao;
	private PosicaoEnum posicao;
	
	//Quadrantes
	private Quadrante quadranteDireito;
	private Quadrante quadranteEsquerdo;

	public Arcada(){
	}
	
	public Arcada(PosicaoEnum posicao){
		this();
		this.posicao = posicao;
	}

	public String getPosicao() {
		return posicao.getDescricao();
	}

	public void setPosicao(String posicao) {
		for (PosicaoEnum pos : PosicaoEnum.values()) {
			if(pos.getDescricao().equals(posicao))
				this.posicao = pos;
		}
	}
	
	public Quadrante getQuadranteDireito() {
		return quadranteDireito;
	}

	public void setQuadranteDireito(Quadrante quadranteDireito) {
		this.quadranteDireito = quadranteDireito;
	}

	public Quadrante getQuadranteEsquerdo() {
		return quadranteEsquerdo;
	}

	public void setQuadranteEsquerdo(Quadrante quadranteEsquerdo) {
		this.quadranteEsquerdo = quadranteEsquerdo;
	}

	public Denticao getDenticao() {
		return denticao;
	}

	public void setDenticao(Denticao denticao) {
		this.denticao = denticao;
	}
	
	public long getIdArcada() {
		return idArcada;
	}

	public void setIdArcada(long idArcada) {
		this.idArcada = idArcada;
	}

	public Boolean validate(){
		this.setDescricao("Arc. " + this.getPosicao().substring(0,3) + ". " + this.getDenticao().getTipo());
		return true;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricaoFormatada(){
		if(this.getDescricao().equals(PosicaoEnum.TODOS.getDescricao()))
			return "-";
		return this.getDescricao().substring(0, 1);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Arcada))
			return false;
		
		Arcada arcada = (Arcada) obj;
		return new EqualsBuilder()
		.append(this.getIdArcada(), arcada.getIdArcada())
		.append(this.getDescricao(), arcada.getDescricao())
		.isEquals();
		
	}
	
	public List<Quadrante> getQuadrantes(Arcada arcada){
		if (arcada == null)
			return new ArrayList<Quadrante>();
		
		SearchAgent sa = new SearchAgent();
		Criteria criteria = sa.createCriteriaFor(Quadrante.class);
		criteria.add(Expression.or(Expression.eq("arcada", arcada), Expression.isNull("arcada")));
		return criteria.list();
	}
	
}