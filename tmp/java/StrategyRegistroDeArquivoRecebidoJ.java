package br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo;

import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class StrategyRegistroDeArquivoRecebidoJ implements StrategyRegistroBancoInterface {

	public void executar(String linha, ArquivoDeRetorno arquivo, UsuarioInterface usuario) throws Exception {
		System.out.println(linha.substring(1));
	}
}

