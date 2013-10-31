package br.com.infowaypi.ecarebc.produto;

/**
 * @author DANNYLVAN
 *
 */
public enum CaraterEnum {

	URGENCIA("Urgência"),
	ELETIVO("Eletivo"),
	AMBOS("Ambos");
	
	private String descricao;

	CaraterEnum(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
    public static CaraterEnum getTipoSegmentacaoByDescricao (String descricao) {
		for (CaraterEnum carater : values()) {
			if (carater.getDescricao().equals(descricao)) {
				return carater;
			}
		}
		return null;
	}
}
