package br.com.infowaypi.ecarebc.financeiro.arquivos;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.msr.financeiro.BancoInterface;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.utils.Utils;

public class StrategyRemessaBancoDefault implements StrategyRemessaBancoInterface<Conta, InformacaoFinanceiraInterface, ArquivoDeEnvio>{
	
	public void executar(Conta conta, InformacaoFinanceiraInterface informacaoFinanceira) throws Exception {
		BancoInterface banco = informacaoFinanceira.getBanco();
		
		String identificacaoDoCliente = String.valueOf(informacaoFinanceira.getMatricula());
//		identificacaoDoCliente = StringUtils.leftPad(identificacaoDoCliente, 7, "0");
		
		String agencia = informacaoFinanceira.getAgencia().substring(0,4);
		String contaCorrente = informacaoFinanceira.getConta();
		String dataVencimento = Utils.format(conta.getDataVencimento(), "yyyyMMdd");
		BigDecimal valor = conta.getValorCobrado();
		String valorCobradoFormatado = (new DecimalFormat("######0.00")).format(valor.floatValue());

		boolean bradesco = "237".equals(banco.getCodigoFebraban());
		boolean bb = "001".equals(banco.getCodigoFebraban());
		if(bradesco)
			identificacaoDoCliente = StringUtils.leftPad(identificacaoDoCliente, 25, "0");
		else
			identificacaoDoCliente = StringUtils.rightPad(identificacaoDoCliente, 25, " ");
		
		contaCorrente = StringUtils.replace(contaCorrente, "-", "");
		contaCorrente = StringUtils.replace(contaCorrente, "=", "");
		contaCorrente = StringUtils.replace(contaCorrente, "/", "");
		contaCorrente = StringUtils.replace(contaCorrente, ".", "");

		String codigoOperacao = "";
		if(bb){ // BANCO DO BRASIL
			contaCorrente = contaCorrente.substring(0, contaCorrente.length() - 1);
			contaCorrente = StringUtils.leftPad(contaCorrente, 14, "0");
		}
		else if(bradesco){ // BRADESCO
			contaCorrente = StringUtils.leftPad(contaCorrente, 14, "0");
		}
		
		valorCobradoFormatado = StringUtils.replace(valorCobradoFormatado, ",", "");
		valorCobradoFormatado = StringUtils.replace(valorCobradoFormatado, ".", "");
		valorCobradoFormatado = StringUtils.leftPad(valorCobradoFormatado, 15, "0");

		String codigoMoeda = "03";
		String codigoCobranca = StringUtils.leftPad(String.valueOf(conta.getIdConta()), 10, "0"); 
		String usoEmpresa = StringUtils.rightPad(codigoCobranca, 60, " ");

		String agendamentoCliente = "";
		agendamentoCliente = StringUtils.leftPad(agendamentoCliente, 6, " ");
		
		String usoFuturo = StringUtils.leftPad("", 8, " ");

		String codigoRegistro = "E";
		
		String textoCobranca = codigoRegistro + identificacaoDoCliente + agencia + codigoOperacao + contaCorrente + 
							   dataVencimento + valorCobradoFormatado + codigoMoeda + usoEmpresa + 
							   agendamentoCliente + usoFuturo;
		conta.getArquivoEnvio().addConteudo(textoCobranca, valor);
	}

	public String getCabecalho(ArquivoDeEnvio arquivoEnvio) {
		boolean bb = "001".equals(arquivoEnvio.getBanco().getCodigoFebraban());
		StringBuffer cabecalho = new StringBuffer();
		BancoInterface banco = arquivoEnvio.getBanco();
		String convenio = StringUtils.rightPad(banco.getConvenio(), 20, " ");
		String nomeDaEmpresa = StringUtils.rightPad(banco.getNomeDaEmpresa().toUpperCase(), 20, " ");
		String codigoFebraban = StringUtils.leftPad(banco.getCodigoFebraban(), 3, "0");
		String nomeBanco = StringUtils.upperCase(banco.getDescricao());
		if(bb){
			nomeBanco = "BANCO DO BRASIL";
		}
		nomeBanco = StringUtils.abbreviate(nomeBanco, 20);
		nomeBanco = StringUtils.rightPad(nomeBanco, 20, " ");
		String dataGeracao = Utils.format(arquivoEnvio.getDataProcessamento(), "yyyyMMdd");
		String numeroSequencial = StringUtils.leftPad(String.valueOf(arquivoEnvio.getSequencial()), 6, "0");
		String identificacaoServico = StringUtils.rightPad(banco.getIdentificacaoDoServico(), 17, " ");
		String contaCompromisso;
		if(banco.getContaCompromisso() != null && banco.getContaCompromisso().trim().length() > 0){
			contaCompromisso = StringUtils.leftPad(banco.getContaCompromisso(), 16, "0");
		}
		else{
			if(!banco.getCodigoFebraban().equals("237"))
				numeroSequencial = StringUtils.leftPad("", 6, "0");
			contaCompromisso = StringUtils.leftPad("", 16, " ");
		}
		String identificacaoAmbiente = banco.getIdentificacaoAmbiente() == null ? "" : banco.getIdentificacaoAmbiente();
		identificacaoAmbiente = StringUtils.rightPad(identificacaoAmbiente, 29, " ");
		
		String numeroSequencialRegistro;
		if(banco.getSequencialRegistro() != null && banco.getSequencialRegistro().trim().length() > 0)
			numeroSequencialRegistro = StringUtils.leftPad(banco.getSequencialRegistro(), 6, "0");
		else
			numeroSequencialRegistro = StringUtils.leftPad("", 6, " ");
		
		cabecalho.append("A1");
		cabecalho.append(convenio);
		cabecalho.append(nomeDaEmpresa);
		cabecalho.append(codigoFebraban);
		cabecalho.append(nomeBanco);
		cabecalho.append(dataGeracao);
		cabecalho.append(numeroSequencial);
		cabecalho.append("04");
		cabecalho.append(identificacaoServico);
		cabecalho.append(contaCompromisso);
		cabecalho.append(identificacaoAmbiente);
		cabecalho.append(numeroSequencialRegistro);
		cabecalho.append(" ");
		cabecalho.append(System.getProperty("line.separator"));
		return cabecalho.toString();
	}

	public String getCorpo(ArquivoDeEnvio arquivoEnvio, List<String> conteudo) {
		int numeroLinhasConteudo = conteudo.size();
		StringBuffer corpo = new StringBuffer();
		int contador = 1;
		for(String linha : conteudo){
			corpo.append(linha);
			if(arquivoEnvio.getBanco().isAgendamentoCliente())
				corpo.append(StringUtils.leftPad(String.valueOf(contador), 6, "0"));
			else
				corpo.append(StringUtils.leftPad("", 6, " "));
			
			corpo.append("0");
			if(contador != numeroLinhasConteudo)
			corpo.append(System.getProperty("line.separator"));
			contador++;
		}
		corpo.append(System.getProperty("line.separator"));
		return corpo.toString();
	}

	public String getRodape(ArquivoDeEnvio arquivoEnvio, int quantidadeCobrancas, BigDecimal valorTotalRemessa) {
		StringBuffer rodape = new StringBuffer();
		String totalDeLinhas = String.valueOf(quantidadeCobrancas + 2);
		totalDeLinhas = StringUtils.leftPad(totalDeLinhas, 6, "0");

		String valorCobrado = (new DecimalFormat("######0.00")).format(valorTotalRemessa);
		valorCobrado = StringUtils.replace(valorCobrado, ".", "");
		valorCobrado = StringUtils.replace(valorCobrado, ",", "");
		valorCobrado = StringUtils.leftPad(valorCobrado, 17, "0");

		String sequencialRodape = "";
		if(arquivoEnvio.getBanco().isAgendamentoCliente()){
			sequencialRodape = String.valueOf(quantidadeCobrancas + 1);
			sequencialRodape = StringUtils.leftPad(sequencialRodape, 6, "0");
		}
		else
			sequencialRodape = StringUtils.leftPad("", 6, " ");

		sequencialRodape = StringUtils.leftPad(sequencialRodape, 125, " ");
		sequencialRodape = StringUtils.rightPad(sequencialRodape, 126, " ");
		
		rodape.append("Z");
		rodape.append(totalDeLinhas);
		rodape.append(valorCobrado);
		rodape.append(sequencialRodape);
		return rodape.toString();
	}
}
