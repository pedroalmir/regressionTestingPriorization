package br.com.infoway.ecare.services.tarefasCorrecao.relatorioMedAlliance;

import java.math.BigDecimal;
import java.math.BigInteger;

import br.com.infowaypi.msr.utils.Utils;

public class ResumoGuias {
	private BigInteger quantidade = BigInteger.ZERO;
	private BigDecimal valor = BigDecimal.ZERO;
	
	public void add(BigInteger v1, BigDecimal v2) {
		quantidade = quantidade.add(v1);
		valor = valor.add(v2);
	}
	
	@Override
	public String toString() {
		return Utils.format(valor);
	}

	public BigInteger getQuantidade() {
		return quantidade;
	}

	public BigDecimal getValor() {
		return valor;
	}
	
}
