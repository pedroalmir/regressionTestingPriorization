package br.com.infowaypi.ecarebc.produto;


/**
 * Caracter�sticas dos planos de sa�de quanto ao tipo de cobertura assistencial
 */
public enum TipoSegmentacao {
	/**
	 * Engloba apenas os atendimentos realizados em consult�rio (consultas) ou ambulat�rio (procedimentos ambulatoriais), 
	 * definidos e listados no Rol de Procedimentos, inclusive exames. N�o cobre interna��o hospitalar. 
	 */
	AMBULATORIAL("Ambulatorial"),

	/**
	 * Compreende os atendimentos realizados durante a interna��o hospitalar. N�o tem cobertura ambulatorial. 
	 */
	HOSPITALAR("Hospitalar"),
	
	/**
	 * Engloba os atendimentos realizados durante interna��o hospitalar e os procedimentos relativos ao pr�-natal e � assist�ncia ao parto. 
	 */
	HOSPITALAR_COM_OBSTETRICIA("Hospitalar com Obstetr�cia"),
	
	/**
	 * Inclui apenas procedimentos odontol�gicos realizados em consult�rio, incluindo exame cl�nico, radiologia, preven��o, dent�stica, endodontia, periodontia e cirurgia. 
	 */
	ODONTOLOGICO("Odontol�gico"),
	
	/**
	 * Constitui o padr�o de assist�ncia m�dico-hospitalar porque conjuga a cobertura ambulatorial, hospitalar e obst�trica. 
	 */
	REFERENCIA("Refer�ncia");
	
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
