package br.com.infowaypi.ecarebc.procedimentos;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.msr.utils.Assert;

public class GrupoCBHPM extends ClassificacaoCBHPM {

	private static final long serialVersionUID = 1L;

	private static final int QUANTIDADE_CARACTERES = 3;

	private CapituloCBHPM capituloCBHPM;
	
	public GrupoCBHPM() {}
	
	public GrupoCBHPM(CapituloCBHPM capitulo) {
		this.capituloCBHPM = capitulo;
	} 

	public CapituloCBHPM getCapituloCBHPM() {
		return capituloCBHPM;
	}

	public void setCapituloCBHPM(CapituloCBHPM capitulo) {
		this.capituloCBHPM = capitulo;
	}

	@Override
	public Boolean validate() throws Exception {
		Assert.isEquals(QUANTIDADE_CARACTERES, this.getCodigo().length(), "O código do grupo deve conter "+QUANTIDADE_CARACTERES+" caractere(s)");
		Assert.isTrue(this.getCodigo().startsWith(this.getCapituloCBHPM().getCodigo()), "O código do grupo deve iniciar com o código do capítulo");
		return super.validate();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getCodigo())
			.append(this.getDescricao())
			.append(this.capituloCBHPM)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof GrupoCBHPM)) {
			return false;
		}
		GrupoCBHPM outro = (GrupoCBHPM)obj;
		return new EqualsBuilder().append(this.getCodigo(), outro.getCodigo())
			.append(this.getDescricao(), outro.getCodigo())
			.append(this.capituloCBHPM, outro.capituloCBHPM)
			.isEquals();
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(this.getCodigo())
			.append(" - ").append(this.getDescricao())
			.toString();
	}
	
	@SuppressWarnings("unchecked")
	public List<SubgrupoCBHPM> getSubgrupos(GrupoCBHPM grupo){
		if (grupo == null){
			return new ArrayList<SubgrupoCBHPM>();
		}
		SearchAgent sa = new SearchAgent();
		Criteria criteria = sa.createCriteriaFor(SubgrupoCBHPM.class);
		criteria.add(Expression.eq("grupoCBHPM", grupo));
		return criteria.list();
	}

}
