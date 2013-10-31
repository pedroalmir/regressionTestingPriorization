package br.com.infowaypi.ecare.enums;

public enum TipoEventoEnum {
	
	PALESTRA(1, "Palestra"),
	REUNIAO(2, "Reuniao"),
	OFICINA(3, "Oficina"),;
	
	private Integer valor;
	private String descricao;
	
	public Integer valor() {
		return valor;
	}

	public String descricao() {
		return descricao;
	}

	private TipoEventoEnum(Integer valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}

	public static String descricao(Integer valor) {
		String descricao = null;
		for (TipoEventoEnum estado : TipoEventoEnum.values()) {
			if (estado.valor().equals(valor)) {
				descricao = estado.descricao();
				break;
			}
		}
		
		return descricao;
	}	
}