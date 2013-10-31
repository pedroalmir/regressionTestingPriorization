package br.com.infowaypi.ecare.enums;

/**
 * @author Marcos Roberto - 10.10.2011 
 */
public enum TipoArquivoEnum {

	PDF	("pdf"),
	XLS ("xls"),
	JPG	("jpg"),
	XML	("xml");

	TipoArquivoEnum(String descricao) {
		this.descricao = descricao;
	}

	private String descricao;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
