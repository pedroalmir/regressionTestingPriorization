package br.com.infowaypi.ecare.associados;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.associados.Prestador;

public class PrestadorReport extends Prestador{
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Prestador)) {
			return false;
		}
		Prestador prestador = (Prestador) object;
		return new EqualsBuilder()
			.append(this.getIdPrestador(),prestador.getIdPrestador())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(1308791639, 309108973)
			.append(this.getIdPrestador())
			.toHashCode();
	}
	
}
