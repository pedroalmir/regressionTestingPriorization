package br.com.infowaypi.ecarebc.procedimentos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Assert;

public class CapituloCBHPM extends ClassificacaoCBHPM {

	private static final long serialVersionUID = 1L;
	
	private static final int QUANTIDADE_CARACTERES = 1;
	
	@Override
	public Boolean validate() throws Exception {
		Assert.isEquals(QUANTIDADE_CARACTERES, this.getCodigo().length(), "O código do capítulo deve conter apenas " + QUANTIDADE_CARACTERES + " caractere(s)");
		hasCapituloSalvoComMesmoCodigo();
		return super.validate();
	}
	
	private void hasCapituloSalvoComMesmoCodigo(){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("codigo", this.getCodigo()));
		CapituloCBHPM capitulo = sa.uniqueResult(CapituloCBHPM.class);
		Assert.isNull(capitulo, "Não é possível cadastrar capitulos com o mesmo código");
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CapituloCBHPM)) {
			return false;
		}
		CapituloCBHPM outro = (CapituloCBHPM) obj;
		return new EqualsBuilder().append(this.getCodigo(), outro.getCodigo())
			.append(this.getDescricao(), outro.getDescricao())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getCodigo())
			.append(this.getDescricao())
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(this.getCodigo())
			.append(" - ").append(this.getDescricao())
			.toString();
	}
}
