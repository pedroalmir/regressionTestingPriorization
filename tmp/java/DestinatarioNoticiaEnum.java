package br.com.infowaypi.ecare.enums;
/**
 * Enum para destinatários da noticia
 * 
 * @author jefferson
 */
public enum DestinatarioNoticiaEnum {

	TODOS_EXCETO_SEGURADO("todos","Todos, exceto Segurados"),
	PRESTADOR("prestador","Prestador"),
	SEGURADO("segurado","Segurado");
	
	private final String valor;
	private final String descricao;
	
	DestinatarioNoticiaEnum(String valor, String descricao){
		this.valor = valor;
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getValor() {
		return valor;
	}

	
}
