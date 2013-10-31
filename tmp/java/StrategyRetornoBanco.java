package br.com.infowaypi.ecarebc.financeiro.arquivos;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import br.com.infowaypi.msr.user.UsuarioInterface;

public class StrategyRetornoBanco implements StrategyRetornoBancoInterface{

	public void executar(byte[] conteudo, ArquivoDeRetorno arquivo, UsuarioInterface usuario) throws Exception {
		arquivo.setConteudo(conteudo);
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(conteudo)));
		String linha;
		int i = 0;
		int count = 1;
		long inicio, fim;
		inicio = System.nanoTime();
		do {
			i++;
			linha = br.readLine();
//			if (linha != null) {
//				this.processa(linha, usuario);
//			}
			if((i == 1000) ){
				fim = System.nanoTime();
				int x = (count * i);
				inicio = fim;
				i = 0;
				count++;
			}
		} 
		while(linha != null);
		br.close();


//		arquivo.setCompetencia(Utils.parse(linha.substring(14, 20), "MMyyyy"));
	}
}

