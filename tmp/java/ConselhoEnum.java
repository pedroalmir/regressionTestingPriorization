package br.com.infowaypi.ecarebc.enums;

/**
 * Tabela 26 -Terminologia de conselho profissional. 
 *
 * @author
 * @change Pedro Almir
 * <ul>
 * 	<li>Documentação</li>
 *  <li>Inclusão do CRAS e do CRFA</li>
 *  <li>Inclusão do código de cada conselho</li>
 * </ul>
 *
 */
public enum ConselhoEnum {
	/**
	 * Conselho Regional de Assistência Social (CRAS)
	 */
	CRAS("01", "CRAS"),
	/**
	 * Conselho Regional de Enfermagem (COREN)
	 */
	COREN("02", "COREN"),
	/**
	 * Conselho Regional de Farmácia (CRF)
	 */
	CRF("03", "CRF"),
	/**
	 * Conselho Regional de Fonoaudiologia (CRFA)
	 */
	CRFA("04", "CRFA"),
	/**
	 * Conselho Regional de Fisioterapia e Terapia Ocupacional (CREFITO)
	 */
	CREFITO("05", "CREFITO"),
	/**
	 * Conselho Regional de Medicina (CRM)
	 */
	CRM("06", "CRM"),
	/**
	 * Conselho Regional de Nutrição (CRN)
	 */
	CRN("07", "CRN"),
	/**
	 * Conselho Regional de Odontologia (CRO)
	 */
	CRO("08", "CRO"),
	/**
	 * Conselho Regional de Psicologia (CRP)
	 */
	CRP("09", "CRP"),
	/**
	 * Outros Conselhos 
	 */
	OUTROS("10", "Outros Conselhos");
	
	
	private String codigo;
	private String descricao;
	
	/**
	 * @param codigo
	 * @param descricao
	 */
	ConselhoEnum(String codigo, String descricao){
		this.codigo = codigo;
		this.descricao = descricao;
	}

	/**
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}

	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
}