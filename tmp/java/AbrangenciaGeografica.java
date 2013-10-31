package br.com.infowaypi.ecarebc.produto;

/**
 * Abrangência geográfica onde o beneficiário poderá ser atendido.
 * Ela pode abranger um município (cobertura municipal), 
 * um conjunto específico de municípios, 
 * um estado (cobertura estadual), 
 * um conjunto específico de estados, 
 * ou mesmo todo país (cobertura nacional). 
 * @author Alan
 *
 */
public enum AbrangenciaGeografica {
	MUNICIPAL("Municipal"),// um município
	GRUPO_MUNICIPIOS("Grupo de Municípios"), // um conjunto específico de municípios, 
	ESTADUAL("Estadual"), // um estado (cobertura estadual), 
	GRUPO_ESTADOS("Grupo de Estados"), // um conjunto específico de estados, 
	NACIONAL("Nacional"); // todo país
	
	private String descricao;

	private AbrangenciaGeografica(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
