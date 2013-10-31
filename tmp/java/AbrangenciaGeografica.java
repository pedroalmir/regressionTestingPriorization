package br.com.infowaypi.ecarebc.produto;

/**
 * Abrang�ncia geogr�fica onde o benefici�rio poder� ser atendido.
 * Ela pode abranger um munic�pio (cobertura municipal), 
 * um conjunto espec�fico de munic�pios, 
 * um estado (cobertura estadual), 
 * um conjunto espec�fico de estados, 
 * ou mesmo todo pa�s (cobertura nacional). 
 * @author Alan
 *
 */
public enum AbrangenciaGeografica {
	MUNICIPAL("Municipal"),// um munic�pio
	GRUPO_MUNICIPIOS("Grupo de Munic�pios"), // um conjunto espec�fico de munic�pios, 
	ESTADUAL("Estadual"), // um estado (cobertura estadual), 
	GRUPO_ESTADOS("Grupo de Estados"), // um conjunto espec�fico de estados, 
	NACIONAL("Nacional"); // todo pa�s
	
	private String descricao;

	private AbrangenciaGeografica(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
