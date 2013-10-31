package br.com.infowaypi.ecarebc.financeiro.arquivos;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.exceptions.EmptyFileException;
import br.com.infowaypi.ecarebc.exceptions.ReferenciaNaoEncontradaException;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.SearchAgentInterface;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.financeiro.BancoInterface;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;


public class ArquivoDeEnvio extends Arquivo{

	private static final long serialVersionUID = 1L;
	 

	public ArquivoDeEnvio(){
		this(null);
	
	}
	public ArquivoDeEnvio(UsuarioInterface usuario){
		super();
		this.mudarSituacao(usuario, Constantes.SITUACAO_ABERTO, "Geração automática de arquivo", new Date());
	
	}

	@SuppressWarnings("static-access")
	@Override
	public void criarArquivo() throws Exception {
		if(this.getContas().isEmpty())
			throw new EmptyFileException("Não existe nenhuma conta para ser enviada.");
		String nomeArquivo = this.gerarNomeArquivo();
		this.setFileName(nomeArquivo);
//		this.setTipoArquivo(ArquivoInterface.REMESSA);
		this.setSequencial(this.getTotalArquivos(this.getBanco()));
		byte[] conteudo = getRelatorio().getBytes();
		this.setArquivo(conteudo);
		FileOutputStream file = new FileOutputStream(REPOSITORIOTESTE + nomeArquivo);
		file.write(conteudo);
		file.close();
	}

	public static int getTotalArquivos(BancoInterface banco) {
		SearchAgentInterface sa = new SearchAgent();
		sa.addParameter(new Equals("banco", banco));
		sa.addParameter(new NotEquals("statusArquivo", 'C'));
		return sa.resultCount(ArquivoDeEnvio.class);
//		List qtd = sa.list(ArquivoDeEnvio.class);
//		if(qtd == null || qtd.isEmpty())
//			return 0;
//		return qtd.size();
	}
	
	private String getRelatorio() throws Exception{
		StringBuffer relatorio = new StringBuffer();
		relatorio.append(setCabecalho());
		relatorio.append(setCorpo());
		relatorio.append(setRodape(valorTotal));
		return relatorio.toString();
	}
	
	@SuppressWarnings("unchecked")
	protected String setCabecalho() throws Exception {
		StrategyRemessaBancoInterface estrategia = getStrategy();
		String cabecalho = estrategia.getCabecalho(this);
		return cabecalho;
	}
	
	@SuppressWarnings("unchecked")
	protected String setCorpo() throws Exception {
		StrategyRemessaBancoInterface estrategia = getStrategy();
		String corpo = estrategia.getCorpo(this, conteudo);
		return corpo;
	}
	
	@SuppressWarnings("unchecked")
	protected String setRodape(BigDecimal valorTotalRemessa) throws Exception {
		StrategyRemessaBancoInterface estrategia = getStrategy();
		String rodape = estrategia.getRodape(this, quantidadeContas, valorTotalRemessa);
		return rodape;
	}

	protected StrategyRemessaBancoInterface getStrategy() {
		int codigoBanco = Integer.parseInt(this.getBanco().getCodigoFebraban());
		StrategyRemessaBancoInterface estrategia = StrategyRemessaBancoFactory.getInstance(codigoBanco);

		if (estrategia == null)
			throw new ReferenciaNaoEncontradaException("Não foi encontrada nenhuma Estratégia para o banco " + this.getBanco().getDescricao());
		return estrategia;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <C extends Conta, IF extends InformacaoFinanceiraInterface> void populate(C conta, IF informacaoFinanceira) throws Exception{
		this.getContas().add(conta);
		conta.setArquivoEnvio(this);
		StrategyRemessaBancoInterface estrategia = getStrategy();

		estrategia.executar(conta, informacaoFinanceira);
	}
	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.DEFAULT_STYLE)
		.append("id", this.getIdArquivo())
		.toString();
	}
	public Integer getTipoArquivo() {
		return ArquivoInterface.REMESSA;
	}
	
}
