package br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceira;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class StrategyRegistroBancoTipoB implements StrategyRegistroBancoInterface {

	private static final String INCLUIR = "2";
	private static final String EXCLUIR = "1";

	@SuppressWarnings("unchecked")
	public void executar(String linha, ArquivoDeRetorno arquivo, UsuarioInterface usuario) throws Exception {
		InformacaoFinanceira informacaoFinanceira;
		Banco banco = arquivo.getBanco();
		
		
		if (banco == null)
			throw new Exception("Não é possível processar este arquivo");		

		SearchAgent sa = new SearchAgent();
		String agencia = linha.substring(26, 30).trim();
		String codigoOperacao = linha.substring(30, 33).trim();
		String contaCorrente = linha.substring(33, 42).trim();

		contaCorrente = StringUtils.stripStart(contaCorrente, "0");

		sa.addParameter(new Equals("agencia", agencia));
		sa.addParameter(new Equals("conta", contaCorrente));
		sa.addParameter(new Equals("banco", banco));
//		StringUtils.
		List<InformacaoFinanceira> informacoesFinanceira = sa.list(InformacaoFinanceira.class);
		
		if (informacoesFinanceira.isEmpty()){
			adicionarLog(linha);
			throw new RuntimeException("A matrícula " + "" + "não consta no sistema.");
		}
		
		if(informacoesFinanceira.size() > 1){
			adicionarLog(linha);
			throw new RuntimeException("A matrícula " + "" + "tem dois cadastos diferentes. Entre em contato com o banco para a regularização.");
		}
		
		informacaoFinanceira = informacoesFinanceira.get(0);
			
		Date dataDaOpcao = Utils.parse(linha.substring(44, 52).trim(), "yyyyMMdd");
		if (dataDaOpcao == null)
			dataDaOpcao = new Date();

		String movimento = linha.substring(149, 150);
		
		if (banco.getCodigoFebraban().equals("104")) // Caixa Economica
			informacaoFinanceira.setOperacao(codigoOperacao);
		
		informacaoFinanceira.setChecado(true);
		mudarSituacao(informacaoFinanceira, dataDaOpcao, movimento);
		ImplDAO.save(informacaoFinanceira);
	}

	public void mudarSituacao(InformacaoFinanceiraInterface informacaoFinanceira, Date dataDaOpcao, String movimento) {
		if (INCLUIR.equals(movimento))
			if (!informacaoFinanceira.isSituacaoAtual(InformacaoFinanceiraInterface.INCLUIDO))
				informacaoFinanceira.mudarSituacao(null, InformacaoFinanceiraInterface.INCLUIDO, "Inclusão de optantes por débito automático", dataDaOpcao);
		else if (EXCLUIR.equals(movimento))
			if (!informacaoFinanceira.isSituacaoAtual(InformacaoFinanceiraInterface.EXCLUIDO))
				informacaoFinanceira.mudarSituacao(null, InformacaoFinanceiraInterface.EXCLUIDO, "Exclusão de optantes por débito automático", dataDaOpcao);
	}

//	private String getCodigoTitular(String linha) {
//		String codigoTitular = linha.substring(1, 7).trim();
//		long codigo = Utils.createLong(codigoTitular);
//		if (codigo > 0)
//			return codigo;
//
//		codigoTitular = linha.substring(01, 26).trim();
//		codigo = Utils.createLong(codigoTitular);
//		if (codigo > 0)
//			return codigo;
//
//		return 0L;
//	}

	private void adicionarLog(String linha) {
		try {
			FileReader reader = new FileReader(ArquivoInterface.REPOSITORIOTESTE + "/LogRegistroB.txt");
			BufferedReader br = new BufferedReader(reader);
			StringBuffer linhas = new StringBuffer();
			String linhaAtual;
			do {
				linhaAtual = br.readLine();
				if (linhaAtual != null) {
					linhas.append(linhaAtual);
					linhas.append(System.getProperty("line.separator"));
				}
			}while (linhaAtual != null);
			br.close();
			reader.close();

			linhas.append(linha);
			linhas.append(System.getProperty("line.separator"));

			FileWriter writer = new FileWriter(ArquivoInterface.REPOSITORIOTESTE + "/LogRegistroB.txt");
			BufferedWriter bw = new BufferedWriter(writer);
			bw.write(linhas.toString());
			bw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}