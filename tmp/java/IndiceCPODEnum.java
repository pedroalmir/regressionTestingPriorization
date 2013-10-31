package br.com.infowaypi.ecarebc.odonto.enums;

import java.io.Serializable;

/**
 * O �ndice CPO-D mede o ataque de c�rie dental � denti��o permanente. A representa��o aqui presente � simplificada e adotada pelo TISS.
 * @author Idelvane
 *
 */
public enum IndiceCPODEnum implements Serializable{
	
	DEFAULT ("-", ""),
	AUSENTE ("A", "(A) Ausente"),
	EXTRACAO_INDICADA ("E", "(E) Extra��o Indicada"),
	HIGIDO ("H", "(H) Higido"),
	CARIADO ("C", "(C) Cariado"),
	RESTAURADO_1FACE ("R1", "(R1) Restaurado 1 Face"),
	RESTAURADO_2FACE ("R2", "(R2) Restaurado 2 Face"),
	RESTAURADO_3FACE ("R2", "(R3) Restaurado 3 Face"),
	RESTAURADO_4FACE ("R4", "(R4) Restaurado 4 Face"),
	RESTAURADO_5FACE ("R5", "(R5) Restaurado 5 Face"),
	;
	
	private String sigla;
	private String descricao;
	
	private IndiceCPODEnum(String sigla, String descricao) {
		this.sigla = sigla;
		this.descricao = descricao;
	}

	public String getSigla() {
		return sigla;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static IndiceCPODEnum getIndiceByDescricao(String descricao){
		for (IndiceCPODEnum indice : IndiceCPODEnum.values()) {
			if(indice.getDescricao().equals(descricao)){
				return indice;
			}
		}
		return null;
	}

}
