package br.com.infowaypi.ecare.segurados;

import java.io.Serializable;

/**
 * Interface para a classe Validador.
 * @author Mário Sérgio Coelho Marroquim
 */
public interface CargoInterface extends Serializable {

	public abstract Long getIdCargo();

	public abstract String getIdentificadorNoSMC();

	public abstract void setIdentificadorNoSMC(String identificadorNoSMC);
	
	public abstract String getTipoOrgao();

	public abstract void setTipoOrgao(String tipoOrgao);
	
	public abstract String getDescricao();

	public abstract void setDescricao(String descricao);

}