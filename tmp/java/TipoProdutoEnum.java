package br.com.infowaypi.ecarebc.produto;


/**
 * Enum para distinguir os tipos de produtos na classe Produto.java
 * @author ewerton/Dannylvan
 */

public enum TipoProdutoEnum {

	PRODUTO_ODONTO("Odontológica"),
	PRODUTO_HOSPITALAR("Hospitalar");
	
	private String descricao;
	
	TipoProdutoEnum(String descricao){
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoProdutoEnum getProdutoEnumByDescricao(String descricao){
		for (TipoProdutoEnum c : TipoProdutoEnum.values()) {
			if (c.getDescricao().equals(descricao)) {
				return c;				
			}
		}
		return null;
	}
}
