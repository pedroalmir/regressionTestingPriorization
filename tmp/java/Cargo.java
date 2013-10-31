package br.com.infowaypi.ecare.segurados;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Classe que representa um Cargo no sistema.
 * @author Mário Sérgio Coelho Marroquim
 */
@SuppressWarnings("serial")
public class Cargo implements CargoInterface {

	private Long idCargo;
	private String identificadorNoSMC;
	private String tipoOrgao;
	private String descricao;
	

	public String getDescricao() {
		return this.descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(String tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public Long getIdCargo() {
		return this.idCargo;
	}
	
	public void setIdCargo(Long idCargo) {
		this.idCargo = idCargo;
	}
	
	public String getIdentificadorNoSMC() {
		return this.identificadorNoSMC;
	}

	public void setIdentificadorNoSMC(String identificadorNoSMC) {
		this.identificadorNoSMC = identificadorNoSMC;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof CargoInterface)) {
			return false;
		}
		CargoInterface otherObject = (CargoInterface) object;
		return new EqualsBuilder()
			.append(this.idCargo, otherObject.getIdCargo())
			.append(this.tipoOrgao, otherObject.getTipoOrgao())
			.append(this.descricao, otherObject.getDescricao())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getIdCargo())
			.append(this.getTipoOrgao())
			.append(this.getDescricao())
			.toHashCode();
	}
}