package br.com.infowaypi.ecarebc.procedimentos;

import br.com.infowaypi.ecarebc.constantes.Constantes;

public enum PorteAnestesicoEnum {
	PORTE_ANESTESICO_1(Constantes.DESCRICAO_PORTE_ANESTESICO_1, 1),
	PORTE_ANESTESICO_2(Constantes.DESCRICAO_PORTE_ANESTESICO_2, 2),
	PORTE_ANESTESICO_3(Constantes.DESCRICAO_PORTE_ANESTESICO_3, 3),
	PORTE_ANESTESICO_4(Constantes.DESCRICAO_PORTE_ANESTESICO_4, 4),
	PORTE_ANESTESICO_5(Constantes.DESCRICAO_PORTE_ANESTESICO_5, 5),
	PORTE_ANESTESICO_6(Constantes.DESCRICAO_PORTE_ANESTESICO_6, 6),
	PORTE_ANESTESICO_7(Constantes.DESCRICAO_PORTE_ANESTESICO_7, 7),
	PORTE_ANESTESICO_8(Constantes.DESCRICAO_PORTE_ANESTESICO_8, 8);
	
	private String descricao;
	private Integer numero;
	
	private PorteAnestesicoEnum(String descricao, Integer numero) {
		this.descricao = descricao;
		this.numero = numero;
	}

	public String getDescricao() {
		return descricao;
	}

	public Integer getNumero() {
		return numero;
	}
	
	public static String getDescricao(Integer porte) {
		for (PorteAnestesicoEnum porteAnestesico : PorteAnestesicoEnum.values()) {
			if (porteAnestesico.getNumero().equals(porte)) {
				return porteAnestesico.getDescricao();
			}
		}
		throw new RuntimeException("Não existe porte para este valor");
	}
	
}
