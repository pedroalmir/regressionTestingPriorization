package br.com.infowaypi.ecare.segurados.tuning;

import java.util.Set;

import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;

public interface TitularFinanceiroTuning extends TitularFinanceiroInterface{
	
	public Set<? extends AbstractMatricula> getMatriculas();

}
