package br.com.infowaypi.ecarebc.procedimentos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.msr.utils.Assert;

public class SubgrupoCBHPM extends ClassificacaoCBHPM {
	
	private static final long serialVersionUID = 1L;

	private static final int QUANTIDADE_CARACTERES = 5;

	private GrupoCBHPM grupoCBHPM;

	public SubgrupoCBHPM() {
		super();
	}

	public SubgrupoCBHPM(GrupoCBHPM grupo) {
		super();
		this.grupoCBHPM = grupo;
	}

	public GrupoCBHPM getGrupoCBHPM() {
		return grupoCBHPM;
	}

	public void setGrupoCBHPM(GrupoCBHPM grupo) {
		this.grupoCBHPM = grupo;
	}
	
	public CapituloCBHPM getCapituloCBHPM() {
		return this.getGrupoCBHPM().getCapituloCBHPM();
	}
	
	@Override
	public Boolean validate() throws Exception {
		Assert.isEquals(QUANTIDADE_CARACTERES, this.getCodigo().length(), "O códido do subgrupo deve conter "+QUANTIDADE_CARACTERES+" caractere(s)");
		Assert.isTrue(this.getCodigo().startsWith(this.getGrupoCBHPM().getCodigo()), "O código do subgrupo deve iniciar com o códigodo grupo.");
		return super.validate();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getCodigo())
			.append(this.getDescricao())
			.append(this.grupoCBHPM)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SubgrupoCBHPM)) {
			return false;
		}
		SubgrupoCBHPM outro = (SubgrupoCBHPM)obj;
		return new EqualsBuilder().append(this.getCodigo(), outro.getCodigo())
			.append(this.getCodigo(), outro.getCodigo())
			.append(this.grupoCBHPM.getDescricao(), outro.grupoCBHPM.getDescricao())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append(this.getCodigo())
			.append(" - ").append(this.getDescricao())
			.toString();
	}

}
