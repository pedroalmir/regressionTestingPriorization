package br.com.infowaypi.ecare.financeiro;

public class ResumoCobrancaIndividual {
	
	private Cobranca cobrancaAtual;
	private Cobranca ultimaCobranca;
	
	
	public Cobranca getCobrancaAtual() {
		return cobrancaAtual;
	}
	
	public void setCobrancaAtual(Cobranca cobrancaAtual) {
		this.cobrancaAtual = cobrancaAtual;
	}
	
	public Cobranca getUltimaCobranca() {
		return ultimaCobranca;
	}

	public void setUltimaCobranca(Cobranca ultimaCobranca) {
		this.ultimaCobranca = ultimaCobranca;
	}
}
