package br.com.infowaypi.ecarebc.produto;

import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

/**
 * @author DANNYLVAN
 *
 * Enum que armazena os tipos de segmentação assistencial existentes.
 * Feita para facilitar o uso nos services.
 */
public enum SegmentacaoAssistencialEnum {
	
	ODONTOLOGICO("Odontologico"),
	AMBULATORIAL_ELETIVO("Ambulatorial  Eletivo"),
	AMBULATORIAL_URGENCIA("Ambulatorial Urgencia"),
	HOSPITALAR_ELETIVO("Hospitalar Eletivo"),
	HOSPITALAR_URGENCIA("Hospitalar Urgencia"),
	OBSTETRICIA_ELETIVO("Obstetrícia Eletivo"),
	OBSTETRICIA_URGENCIA("Obstetrícia Urgencia");
	
	private SegmentacaoAssistencial segmentacaoAssistencial;
	private String descricao;

	private SegmentacaoAssistencialEnum(String descricaoSegmentacao) {
		this.descricao = descricaoSegmentacao; 
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("descricao", descricaoSegmentacao));
		this.segmentacaoAssistencial = sa.uniqueResult(SegmentacaoAssistencial.class);
	}

	public SegmentacaoAssistencial getSegmentacaoAssistencial() {
		return segmentacaoAssistencial;
	}

	public void setSegmentacaoAssistencial(SegmentacaoAssistencial segmentacaoAssistencial) {
		this.segmentacaoAssistencial = segmentacaoAssistencial;
	}

	public String getDescricao() {
		return descricao;
	}
	
}
