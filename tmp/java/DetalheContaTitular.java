package br.com.infowaypi.ecare.financeiro.conta;

import java.util.Date;

import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;

/**
 * 
 * @author Diogo Vinícius
 *
 */
public class DetalheContaTitular extends DetalheContaSegurado{
	
	private static final long serialVersionUID = 1L;
	private TitularFinanceiroSR titular;

	private DetalheContaTitular(){}
	
	public DetalheContaTitular(Date competencia, TitularFinanceiroSR titular){
		super(competencia);
		this.titular = titular;
		titular.getDetalhesContaTitular().add(this);
	}

	public TitularFinanceiroSR getTitular() {
		return titular;
	}

	public void setTitular(TitularFinanceiroSR titular) {
		this.titular = titular;
	}
	
}
