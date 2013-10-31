package br.com.infowaypi.ecarebc.financeiro.arquivos;


import java.math.BigDecimal;
import java.util.List;

import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;

public interface StrategyRemessaBancoInterface<C extends Conta, IF extends InformacaoFinanceiraInterface,A extends ArquivoDeEnvio> {

	public final static int BANCO_DO_BRASIL = 1; 
	public final static int CAIXA_ECONOMICA = 104; 
	public final static int BRADESCO = 237;
	public final static int BEP = 39;
	
	public void executar(C conta, IF informacaoFinanceira) throws Exception;

	public String getCabecalho(A arquivo);

	public String getCorpo(A arquivo, List<String> conteudo);

	public String getRodape(A arquivo, int quantidadeCobrancas, BigDecimal valorTotalRemessa);
}
