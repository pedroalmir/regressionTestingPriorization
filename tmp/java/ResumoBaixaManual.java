package br.com.infowaypi.ecare.financeiro.boletos;

import java.util.Date;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;

public class ResumoBaixaManual {
	
	private TitularFinanceiroSR titular;
	private Cobranca cobranca;
	private Date competencia;
	
	public ResumoBaixaManual(TitularFinanceiroSR titular, Cobranca cobranca, Date competencia) {
		this.titular = titular;
		this.cobranca = cobranca;
		this.competencia = competencia;
	}

	public TitularFinanceiroSR getTitular() {
		return titular;
	}

	public void setTitular(TitularFinanceiroSR titular) {
		this.titular = titular;
	}

	public Cobranca getCobranca() {
		return cobranca;
	}

	public void setCobranca(Cobranca cobranca) {
		this.cobranca = cobranca;
	}
	
	public Date getCompetencia() {
		return competencia;
	}
	
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
}
