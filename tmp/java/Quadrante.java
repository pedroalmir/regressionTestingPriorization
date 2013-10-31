package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.odonto.enums.LadoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.QuadranteEnum;
import br.com.infowaypi.molecular.SearchAgent;

/**
 * Classe que representa um quadrante de arcada no sistema odontológico
 * @author Danilo Nogueira Portela
 *
 */
public class Quadrante implements Serializable, EstruturaOdontoInterface{

	private static final long serialVersionUID = 6167061544136707008L;
	
	private long idQuadrante;
	private String descricao;
	private QuadranteEnum tipo;
	private LadoEnum lado;
	private Arcada arcada;
	
	//Dentes
	private Dente incisivoCentral;
	private Dente incisivoLateral;
	private Dente canino;
	private Dente preMolar1;
	private Dente preMolar2;
	private Dente molar1;
	private Dente molar2;
	private Dente molar3;
	
	public Quadrante() {
	}
	
	public Quadrante(LadoEnum lado) {
		this();
		this.lado = lado;
	}
	
	
	public Dente getCanino() {
		return canino;
	}

	public void setCanino(Dente canino) {
		this.canino = canino;
	}

	public Dente getIncisivoCentral() {
		return incisivoCentral;
	}

	public void setIncisivoCentral(Dente incisivoCentral) {
		this.incisivoCentral = incisivoCentral;
	}

	public Dente getIncisivoLateral() {
		return incisivoLateral;
	}

	public void setIncisivoLateral(Dente incisivoLateral) {
		this.incisivoLateral = incisivoLateral;
	}

	public Dente getMolar1() {
		return molar1;
	}

	public void setMolar1(Dente molar1) {
		this.molar1 = molar1;
	}

	public Dente getMolar2() {
		return molar2;
	}

	public void setMolar2(Dente molar2) {
		this.molar2 = molar2;
	}

	public Dente getMolar3() {
		return molar3;
	}

	public void setMolar3(Dente molar3) {
		this.molar3 = molar3;
	}

	public Dente getPreMolar1() {
		return preMolar1;
	}

	public void setPreMolar1(Dente preMolar1) {
		this.preMolar1 = preMolar1;
	}

	public Dente getPreMolar2() {
		return preMolar2;
	}

	public void setPreMolar2(Dente preMolar2) {
		this.preMolar2 = preMolar2;
	}

	public long getIdQuadrante() {
		return idQuadrante;
	}
	
	public void setIdQuadrante(long idQuadrante) {
		this.idQuadrante = idQuadrante;
	}

	public Integer getNumero() {
		return tipo.getNumero();
	}

	public void setNumero(Integer numero) {
		for (QuadranteEnum q : QuadranteEnum.values()) {
			if(numero.equals(q.getNumero()))
				this.tipo = q;
		}
	}
	
	public String getLado() {
		return lado.getDescricao();
	}

	public void setLado(String lado) {
		for (LadoEnum l : LadoEnum.values()) {
			if(lado.equals(l.getDescricao()))
				this.lado = l;
		}
	}
	
	public Arcada getArcada(){
		return arcada;
	}

	public void setArcada(Arcada arcada) {
		this.arcada = arcada;
	}
	
	public Boolean validate(){
		this.setDescricao(this.getNumero() + " - " + this.getLado());
		return true;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricaoFormatada(){
		String descricao = "-";
			if(!this.getNumero().equals(QuadranteEnum.TODOS.getNumero()))
				descricao = this.getLado().substring(0,1);
		return descricao;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Quadrante))
			return false;
		
		Quadrante quadrante = (Quadrante) obj;
		return new EqualsBuilder()
		.append(this.getIdQuadrante(), quadrante.getIdQuadrante())
		.append(this.getDescricao(), quadrante.getDescricao())
		.isEquals();
		
	}
	
	public List<Dente> getDentes(Quadrante quadrante){
		if (quadrante == null)
			return new ArrayList<Dente>();
		
		SearchAgent sa = new SearchAgent();
		Criteria criteria = sa.createCriteriaFor(Dente.class);
		criteria.add(Expression.or(Expression.eq("quadrante", quadrante), Expression.isNull("quadrante")));
		return criteria.list();
	}
}
