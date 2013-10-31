package br.com.infowaypi.ecare.procedimentos.enums;

import java.math.BigDecimal;


/**
 * Enumeration para classificar procedimentos odontológicos
 * @author Danilo Nogueira Portela
 */
public enum ProcedimentoDescontoEnum {

	RESTAURACAO_DE_AMALGAMA("99000023", "Restauração de amálgama",
			new BigDecimal(3f).divide(new BigDecimal(7f), 4, BigDecimal.ROUND_HALF_UP), 
			new BigDecimal(5f).divide(new BigDecimal(7f), 4, BigDecimal.ROUND_HALF_UP), 
			new BigDecimal(8f).divide(new BigDecimal(7f), 4, BigDecimal.ROUND_HALF_UP)),
	
	RESINA_COMPOSTA("99000060", "Resina Composta",
			new BigDecimal(4f).divide(new BigDecimal(21f), 4, BigDecimal.ROUND_HALF_UP), 
			new BigDecimal(3f).divide(new BigDecimal(7f), 4, BigDecimal.ROUND_HALF_UP));
	
	private String codigo;
	private String descricao;
	private BigDecimal [] porcsAcrescimo;
	
	ProcedimentoDescontoEnum(String codigo, String descricao, BigDecimal ... porcentagens ) {
		this.codigo = codigo;
		this.descricao = descricao;
		this.porcsAcrescimo = porcentagens;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	
	public BigDecimal[] getPorcsDesconto() {
		return porcsAcrescimo;
	}

	public void setPorcsDesconto(BigDecimal[] porcsDesconto) {
		this.porcsAcrescimo = porcsDesconto;
	}

	public BigDecimal getPorcOfIndex(Integer index) {
		BigDecimal porc = BigDecimal.ZERO;
		for (int i = 0; i < porcsAcrescimo.length; i++) {
			if(index.equals(i))
				porc = porcsAcrescimo[i];
		}
		return porc;
	}
}
