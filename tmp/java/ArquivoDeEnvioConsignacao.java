package br.com.infowaypi.ecare.financeiro.arquivo;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecare.financeiro.arquivo.strategy.StrategyRemessaConsignacaoRecife;
import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface;
import br.com.infowaypi.ecare.segurados.Empresa;
import br.com.infowaypi.ecarebc.exceptions.EmptyFileException;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeEnvio;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

public class ArquivoDeEnvioConsignacao extends ArquivoDeEnvio {
	private static final long serialVersionUID = 1L;
	
	private Empresa empresa;
	private StringBuffer conteudo;
	private Set<ConsignacaoMatriculaInterface> consignacoes;
	private StrategyRemessaConsignacaoRecife estrategia;
	
	/** Arquivo de log da geração dos arquivos de consignação. */
	private ArquivoBase arquivoLog; 
	
	public ArquivoDeEnvioConsignacao() {
		super();
		consignacoes = new HashSet<ConsignacaoMatriculaInterface>();
		conteudo = new StringBuffer();
	}
	
	public Empresa getEmpresa() {
		return empresa;
	}
	
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public Set<ConsignacaoMatriculaInterface> getConsignacoes() {
		return consignacoes;
	}
	
	public void setConsignacoes(Set<ConsignacaoMatriculaInterface> consignacoes) {
		this.consignacoes = consignacoes;
	}
	
	public <C extends ConsignacaoMatriculaInterface, IF extends InformacaoFinanceiraInterface> void populate(C conta, IF informacaoFinanceira) throws Exception {
		this.consignacoes.add(conta);
		conta.setArquivoEnvio(this);
		StrategyRemessaConsignacaoRecife estrategia = getEstrategia();
		estrategia.executar(conta, informacaoFinanceira);
	}
	
	protected String setIdentificador() throws Exception {
		StrategyRemessaConsignacaoRecife estrategia = getEstrategia();
		return estrategia.getIdentificador(this);
	}
	
	protected String setCabecalho() throws Exception {
		StrategyRemessaConsignacaoRecife estrategia = getEstrategia();
		return estrategia.getCabecalho(this);
	}
	
	protected String setRodape(BigDecimal valorTotalRemessa) throws Exception {
		StrategyRemessaConsignacaoRecife estrategia = getEstrategia();
		return estrategia.getRodape(this, this.getQuantidadeContas(), valorTotalRemessa);
	}
	
	protected String setCorpo() throws Exception {
//		StrategyRemessaConsignacaoRecife estrategia = getEstrategia();
//		return estrategia.getCorpo(this, this.conteudo);
		return this.conteudo.toString();
	}
	
	protected String gerarNomeArquivo() {
		return this.getEmpresa().getDescricao() + " " + super.gerarNomeArquivo();
	}
	
	public static void main(String[] args) {
		long inicio = System.currentTimeMillis();
		System.out.println(getTotalArquivos());
		System.out.println("TEMPO: "+(System.currentTimeMillis() - inicio)/1000);
	}
	
	@Override
	public void criarArquivo() throws Exception {
		if(this.getConsignacoes().isEmpty())
			throw new EmptyFileException("Não existe nenhuma conta para ser enviada.");
		String nomeArquivo = this.gerarNomeArquivo();
		this.setFileName(nomeArquivo);
//		this.setTipoArquivo(ArquivoInterface.REMESSA);
		this.setSequencial(0);
		byte[] conteudo = getRelatorio().getBytes();
		this.setArquivo(conteudo);
//		FileOutputStream file = new FileOutputStream(REPOSITORIOTESTE + nomeArquivo);
//		file.write(conteudo);
//		file.close();
	}
	
	private String getRelatorio() throws Exception{
		StringBuffer relatorio = new StringBuffer();
		relatorio.append(setIdentificador());
		relatorio.append(setCabecalho());
		relatorio.append(setCorpo());
		relatorio.append(setRodape(valorTotal));
		return relatorio.toString();
	}
	
	
	public StrategyRemessaConsignacaoRecife getEstrategia() {
		if(estrategia == null){
			estrategia = new StrategyRemessaConsignacaoRecife();
			estrategia.setEmpresa(empresa);
		}
		return estrategia;
	}
	
	/**
	 * Adiciona conteúdo(consignação específica) ao arquivo.
	 * @param String conteúdo do arquivo
	 * @param float valor da determinada consignação 
	 */
	public void addConteudo(StringBuffer conteudo, BigDecimal valorConta) {
		this.conteudo.append(conteudo);
		this.conteudo.append(System.getProperty("line.separator"));
		this.quantidadeContas += 1;
		this.valorTotal = MoneyCalculation.getSoma(this.valorTotal, valorConta.floatValue());
		this.valorContas = valorTotal.floatValue();
	}

	public static long getTotalArquivos() {
		return (Long)HibernateUtil.currentSession().createCriteria(ArquivoDeEnvioConsignacao.class)
		.setProjection(Projections.rowCount())
		.uniqueResult();
	}

	public ArquivoBase getArquivoLog() {
		return arquivoLog;
	}

	public void setArquivoLog(ArquivoBase arquivoLog) {
		this.arquivoLog = arquivoLog;
	}
	
}
