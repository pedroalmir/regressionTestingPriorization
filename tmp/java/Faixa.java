package br.com.infowaypi.ecarebc.produto;

import java.io.Serializable;
import java.math.BigDecimal;

public class Faixa implements Serializable, Comparable<Faixa> {
	private static final long serialVersionUID = 1L;
	private long idFaixa;
	private int idadeInicial;
	private int idadeFinal;
	private BigDecimal valor;
	private Tabela tabela;
	
	public Faixa() {}
	
	public Faixa(int idadeInicial, int idadeFinal, BigDecimal valor, Tabela tabela) {
		setIdadeInicial(idadeInicial);
		setIdadeFinal(idadeFinal);
		setValor(valor);
		setTabela(tabela);
	}

	public boolean isNaFaixa(int idade) {
		return ((idade >= idadeInicial) && (idade <= idadeFinal));
	}
	
	public Tabela getTabela() {
		return tabela;
	}

	public void setTabela(Tabela tabela) {
		this.tabela = tabela;
	}

	public long getIdFaixa() {
		return idFaixa;
	}
	
	public void setIdFaixa(long idFaixa) {
		this.idFaixa = idFaixa;
	}
	
	public int getIdadeInicial() {
		return idadeInicial;
	}
	
	public void setIdadeInicial(int idadeInicial) {
		this.idadeInicial = idadeInicial;
	}
	
	public int getIdadeFinal() {
		return idadeFinal;
	}
	
	public void setIdadeFinal(int idadeFinal) {
		this.idadeFinal = idadeFinal;
	}
	
	public BigDecimal getValor() {
		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Override
	public int compareTo(Faixa o) {
		if(this.getIdadeInicial() < o.getIdadeInicial()){
			return -1;
		}
		if(this.getIdadeInicial() > o.getIdadeInicial()){
			return 1;
		}
		return 0;
	}
	
	@Override
	public String toString() {
		return this.getIdadeInicial() + " - " + this.getIdadeFinal()+" = "+this.getValor();
	}
}
