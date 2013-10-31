package br.com.infowaypi.ecare.services.recurso;

public enum TipoItemRecursavelEnum {
	
	GUIA_COMPLETA("Guia Completa"),
	PROCEDIMENTO_CIRURGICO("Procedimento Cir�rgico"),
	PROCEDIMENTO_OUTROS("Outros Procedimentos"),
	PROCEDIMENTO_EXAME("Procedimento de Exame"),
	GASOTERAPIA("Gasoterapia"),
	TAXA("Taxa"),
	PACOTE("Pacote"),
	DIARIA("Di�ria");
	
	private String descricao;
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	TipoItemRecursavelEnum(String descricao) {
		this.descricao = descricao;
	}
}
