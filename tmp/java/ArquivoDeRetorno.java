package br.com.infowaypi.ecarebc.financeiro.arquivos;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.financeiro.CobrancaBC;
import br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo.StrategyRegistroBancoFactory;
import br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo.StrategyRegistroBancoInterface;
import br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo.StrategyRegistroBancoTipoB;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class ArquivoDeRetorno extends Arquivo {

	private static final long serialVersionUID = 1L;

	/**
	 * Atributo para informar à classe que foi processado algum registro B
	 */
	private boolean isRegisterTypeB = false;

	private byte[] boletosNaoEncontrados;
	
	private boolean vazio;

	/**
	 * Atributo transiente utilizado no processamento de retorno
	 */
	private Set<CobrancaBC> cobrancas;

	public ArquivoDeRetorno() {
		super();
		conteudo = new ArrayList<String>();
		cobrancas = new HashSet<CobrancaBC>();

	}

	@Override
	public void criarArquivo() throws Exception {
		// this.setTipoArquivo(ArquivoInterface.RETORNO);
		ImplDAO.save(this);
		String nomeArquivo = this.gerarNomeArquivo();
		this.setFileName(nomeArquivo);
		FileOutputStream file = new FileOutputStream(REPOSITORIOTESTE
				+ nomeArquivo);
		file.write(this.getArquivo());
		file.close();
		ImplDAO.save(this);
	}

	/**
	 * Processa uma linha do arquivo, escolhendo a estrategia adequada para a
	 * mesma.
	 * 
	 * @param String
	 *            linha do arquivo
	 * @param usuario
	 *            Usuario que executa a operacao
	 */
	private void processa(String linha, UsuarioInterface usuario)
			throws Exception {
		StrategyRegistroBancoInterface estrategia = StrategyRegistroBancoFactory
				.getInstance(linha.charAt(0));
		if (estrategia == null)
			estrategia = StrategyRegistroBancoFactory.getInstance(linha
					.charAt(13));

		if (estrategia != null)
			estrategia.executar(linha, this, usuario);

		if (estrategia instanceof StrategyRegistroBancoTipoB) {
			this.isRegisterTypeB = true;
		}

		// throw new FormatoArquivoRetornoInvalidoException(
		// "O Arquivo descarregado contém dados inválidos.");
	}

	// @Override
	public void processar(byte[] conteudo, UsuarioInterface usuario,
			String filename) throws Exception {
		this.setArquivo(conteudo);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(conteudo)));
		String linha = null;
		boolean isConvenioValido = false;
		boolean isBoleto = false;
		int countA = 0;
		SearchAgent sa = new SearchAgent();
		do {
			linha = br.readLine();
			if (linha != null) {
				if (linha.charAt(0) == 'A') {
					// Recupera codigo do banco e convenio
					String convenio = linha.substring(2, 22).trim();
					String codigoBanco = linha.substring(42, 45).trim();
					// Faz a busca
					sa.clearAllParameters();
					sa.addParameter(new Equals("codigoFebraban", codigoBanco));

					List<Banco> bancos = sa.list(Banco.class);
					// Verifica se o convenio é o correto para o banco
					for (Banco banco : bancos) {
						if (!banco.getConvenio().equals(convenio)) {
							continue;
						} else {
							isConvenioValido = true;
							countA++;
						}

					}
				}
				/*
				 * Se não tiver registro A não é de banco então testo para ver
				 * se é boleto
				 */
				if (linha.charAt(13) == 'T' || linha.charAt(13) == 'U') {
					isBoleto = true;
				}
				if (isConvenioValido && !isBoleto) {
					this.processa(linha, usuario);
				}
			}
		} while (linha != null);
		br.close();
		if (countA == 0 && !isBoleto)
			throw new Exception("Código de convênio inválido.");

		if (isConvenioValido && !isBoleto && !this.isRegisterTypeB
				&& this.getContas().isEmpty())
			throw new Exception(
					"Arquivo de Retorno já processado anteriormente.");

		if (isConvenioValido && !isBoleto)
			this.criarArquivo();
	}

	@Override
	public void populate(Conta conta,
			InformacaoFinanceiraInterface informacaoFinanceira)
			throws Exception {
		// TODO Auto-generated method stub

	}

	public Integer getTipoArquivo() {
		return ArquivoInterface.RETORNO;
	}
	public byte[] getBoletosNaoEncontrados() {
		return boletosNaoEncontrados;
	}

	public void setBoletosNaoEncontrados(byte[] boletosNaoEncontrados) {
		this.boletosNaoEncontrados = boletosNaoEncontrados;
	}
	
	public String getNomeArquivoBoletosNaoEncontrados(){
		return "BoletosNaoEncontrados_"+getSequencial()+".txt";
	}
	
	public String getNomeArquivoRetorno(){
		return "Ret_"+getSequencial()+".ret";
	}
	
	public Set<CobrancaBC> getCobrancas() {
		return cobrancas;
	}
	
	public void setCobrancas(Set<CobrancaBC> cobrancas) {
		this.cobrancas = cobrancas;
	}

	public boolean isVazio() {
		return vazio;
	}

	public void setVazio(boolean vazio) {
		this.vazio = vazio;
	}
	
}
