package br.com.infowaypi.ecare.cadastros;

/**
 * Classe Enum respons�vel por guardar os tipos de itens aplic�veis a um motivo de glosa espec�fico.
 * 
 * @author Luciano Rocha
 *
 */
public enum ItemAplicavelEnum {

	TODOS("Todos(as)"),
	ITEM_GUIA_COMPLETA("Guia Completa"),
	ITEM_PROCEDIMENTO("Procedimentos"),
	ITEM_PROCEDIMENTO_EXAME("Procedimentos de Exames"),
	ITEM_OUTROS_PROCEDIMENTOS("Outros Procedimentos"),
	ITEM_GASOTERAPIA("Gasoterapias"),
	ITEM_TAXA("Taxas"),
	ITEM_PACOTE("Pacotes"),
	ITEM_DIARIA("Di�rias");
	
	private String descricao;
	
	ItemAplicavelEnum(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
}