package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;

public class Faturamento extends AbstractFaturamento implements FluxoFinanceiroInterface {
	
	private boolean capitacao;

	
	public Faturamento() {
		super();
	}
	
	public Faturamento(Date competencia, char status, Prestador prestador) {
		super(competencia, status, prestador);
	}
	public boolean isCapitacao() {
		return capitacao;
	}

	public void setCapitacao(boolean capitacao) {
		this.capitacao = capitacao;
	}

	@Override
	public boolean isFaturamentoNormal() {
		return true;
	}

	@Override
	public boolean isFaturamentoPassivo() {
		return false;
	}
	
	@Override
	public String getTipoFaturamento() {
		return "Faturamento Normal";
	}
}