package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;

public enum FaixaValorFixo {
		
		FAIXA_0_25 (1, 0, 25 , 15.0),
		FAIXA_26_39(2, 26, 39, 25.0),
		FAIXA_40_69(3, 40, 69, 35.0),
		FAIXA_ACIMA_70(4, 70, 1000, 50.0);
		
		private int valor;
		private int faixaInicial;
		private int faixaFinal;
		private BigDecimal preco;
		
		FaixaValorFixo(int valor, int faixaDe, int FaixaAte, double preco){
			this.valor = valor;
			this.faixaInicial = faixaDe;
			this.faixaFinal = FaixaAte;
			this.preco = new BigDecimal(preco);
		}
		public int getFaixaFinal() {
			return faixaFinal;
		}
		public int getFaixaInicial() {
			return faixaInicial;
		}
		public BigDecimal getPreco() {
			return preco;
		}
		public int getValor() {
			return valor;
		}
		
		public static FaixaValorFixo getFaixaValorFixo(int idade) {
			for (FaixaValorFixo faixa : FaixaValorFixo.values()) {
				if((idade >= faixa.getFaixaInicial()) && (idade <= faixa.getFaixaFinal()))
					return faixa;
			}
			return null;
		}
		
}
