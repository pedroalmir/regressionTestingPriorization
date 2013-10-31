/**
 * 
 */
package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Prestador;

/**
 * @author Marcus bOolean
 *
 */
@SuppressWarnings("serial")
public class FaturamentoPassivo extends AbstractFaturamento {
	
	public FaturamentoPassivo() {
		super();
	}
	
	public FaturamentoPassivo(Date competencia, char status, Prestador prestador) {
		super(competencia, status, prestador);
	}
	
	@Override
	public boolean isFaturamentoNormal() {
		return false;
	}

	@Override
	public boolean isFaturamentoPassivo() {
		return true;
	}

	@Override
	public String getTipoFaturamento() {
		return "Faturamento Passivo";
	}
}
