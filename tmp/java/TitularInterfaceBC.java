package br.com.infowaypi.ecarebc.segurados;

import java.math.BigDecimal;
import java.util.Set;

import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.planos.Plano;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;

public interface TitularInterfaceBC extends SeguradoInterface, TitularFinanceiroInterface{
	
	public Plano getPlano();
	
	public BigDecimal getValorContrato();
	
	public Set<? extends DependenteInterfaceBC> getDependentes();
	
	public PessoaFisicaInterface getPessoaFisica();
	
	public Set<Consignacao> getConsignacoes();
}
