package br.com.infowaypi.ecarebc.produto;


/**
 * Características dos planos de saúde quanto ao tipo de cobertura assistencial
 */
public enum TipoSegmentacao {
	/**
	 * Engloba apenas os atendimentos realizados em consultório (consultas) ou ambulatório (procedimentos ambulatoriais), 
	 * definidos e listados no Rol de Procedimentos, inclusive exames. Não cobre internação hospitalar. 
	 */
	AMBULATORIAL("Ambulatorial"),

	/**
	 * Compreende os atendimentos realizados durante a internação hospitalar. Não tem cobertura ambulatorial. 
	 */
	HOSPITALAR("Hospitalar"),
	
	/**
	 * Engloba os atendimentos realizados durante internação hospitalar e os procedimentos relativos ao pré-natal e à assistência ao parto. 
	 */
	HOSPITALAR_COM_OBSTETRICIA("Hospitalar com Obstetrícia"),
	
	/**
	 * Inclui apenas procedimentos odontológicos realizados em consultório, incluindo exame clínico, radiologia, prevenção, dentística, endodontia, periodontia e cirurgia. 
	 */
	ODONTOLOGICO("Odontológico"),
	
	/**
	 * Constitui o padrão de assistência médico-hospitalar porque conjuga a cobertura ambulatorial, hospitalar e obstétrica. 
	 */
	REFERENCIA("Referência");
	
	private String descricao;

	private TipoSegmentacao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public String descricao() {
		return descricao;
	}
	
    public static TipoSegmentacao getTipoSegmentacaoByDescricao (String descricao) {
		for (TipoSegmentacao segmentacao : values()) {
			if (segmentacao.getDescricao().equals(descricao)) {
				return segmentacao;
			}
		}
		return null;
	}
}
