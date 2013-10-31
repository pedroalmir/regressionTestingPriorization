package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.odonto.enums.DenticaoEnum;
import br.com.infowaypi.molecular.SearchAgent;

/**
 * Classe que representa uma dentição humana no sistema odontológico
 * @author Danilo Nogueira Portela
 *
 */
public class Denticao implements  Serializable, EstruturaOdontoInterface{

	private static final long serialVersionUID = -8287020535096831252L;
	
	private long idDenticao;
	private String descricao;
	private DenticaoEnum tipo;
	
	//Arcadas
	private Arcada arcadaSuperior;
	private Arcada arcadaInferior;
	
	public Denticao(){
//		this.tipo = DenticaoEnum.NENHUMA;
//		this.arcadaInferior = new Arcada(PosicaoEnum.INFERIOR);
//		this.arcadaSuperior = new Arcada(PosicaoEnum.SUPERIOR);
	}
	
	public Denticao(DenticaoEnum tipo){
		this();
		this.tipo = tipo;
	}
	
	public String getTipo() {
		return tipo.getDescricao();
	}

	public void setTipo(String tipo) {
		for (DenticaoEnum d : DenticaoEnum.values()) {
			if(d.getDescricao().equals(tipo))
				this.tipo = d;
		}
	}
	
	public Arcada getArcadaInferior() {
		return arcadaInferior;
	}

	public void setArcadaInferior(Arcada arcadaInferior) {
		this.arcadaInferior = arcadaInferior;
	}

	public Arcada getArcadaSuperior() {
		return arcadaSuperior;
	}

	public void setArcadaSuperior(Arcada arcadaSuperior) {
		this.arcadaSuperior = arcadaSuperior;
	}

	public long getIdDenticao() {
		return idDenticao;
	}
	
	public void setIdDenticao(long idDenticao) {
		this.idDenticao = idDenticao;
	}
	
	public Boolean validate(){
		this.setDescricao("Dent. " + this.getTipo());
		return true;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricaoFormatada(){
		return this.getDescricao().substring(0, 1);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Denticao))
			return false;
		
		Denticao denticao = (Denticao) obj;
		return new EqualsBuilder()
		.append(this.getIdDenticao(), denticao.getIdDenticao())
		.append(this.getDescricao(), denticao.getDescricao())
		.isEquals();
		
	}
	
	public List<Arcada> getArcadas(Denticao denticao){
		if (denticao == null)
			return new ArrayList<Arcada>();
		
		SearchAgent sa = new SearchAgent();
		Criteria criteria = sa.createCriteriaFor(Arcada.class);
		criteria.add(Expression.or(Expression.eq("denticao", denticao), Expression.isNull("denticao")));
		return criteria.list();
	}
}
