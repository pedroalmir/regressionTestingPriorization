package br.com.infowaypi.ecarebc.financeiro.arquivos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public abstract class Arquivo extends ImplColecaoSituacoesComponent implements ArquivoInterface {

	private static final long serialVersionUID = 1L;
	
	private Long idArquivo;
//	private Long codigoLegado;
//	private int tipoArquivo;
	private Date dataProcessamento;
	private Date dataGeracao;
	private String fileName;
	
	private Integer tipoDeArquivo;

//	private String descricaoArquivo;
	
	private char statusArquivo;
	private Date competencia;
	
	private String observacao;
	private boolean impresso;
	private int tipoPagamento;
	private Banco banco;
	private int sequencial;
	
	private Set<ContaInterface> contas;	
	protected List<String> conteudo;
	// não mapeadas
	private byte[] arquivo;
	protected int quantidadeContas = 0;
	protected Float valorContas;
	protected BigDecimal valorTotal;
	
	public Arquivo(){
		super();
		conteudo = new ArrayList<String>();
		contas = new HashSet<ContaInterface>();
		valorContas = 0f;
		valorTotal = new BigDecimal(0f);
		valorTotal.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
//	public Long getCodigoLegado() {
//		return codigoLegado;
//	}
//	
//	public void setCodigoLegado(Long codigoLegado) {
//		this.codigoLegado = codigoLegado;
//	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getArquivo()
	 */
	public byte[] getArquivo() {
		return arquivo;
	}
	public Banco getBanco() {
		return banco;
	}
	
	public void setBanco(Banco banco) {
		this.banco = banco;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setArquivo(java.lang.Byte[])
	 */
	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getIdArquivo()
	 */
	public Long getIdArquivo() {
		return idArquivo;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setIdArquivo(java.lang.Long)
	 */
	public void setIdArquivo(Long idArquivo) {
		this.idArquivo = idArquivo;
	}
	

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getObservacao()
	 */
	public String getObservacao() {
		return observacao;
	}
	

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setObservacao(java.lang.String)
	 */
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getCompetencia()
	 */
	public Date getCompetencia() {
		return competencia;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setCompetencia(java.util.Date)
	 */
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getDataCriacao()
	 */
	public Date getDataProcessamento() {
		return dataProcessamento;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setDataCriacao(java.util.Date)
	 */
	public void setDataProcessamento(Date dataProcessamento) {
		this.dataProcessamento = dataProcessamento;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getFileName()
	 */
	public String getFileName() {
		return fileName;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setFileName(java.lang.String)
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getQuantidadeContratos()
	 */
	public int getQuantidadeContas() {
		return quantidadeContas;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setQuantidadeContratos(int)
	 */
	public void setQuantidadeContas(int quantidadeContratos) {
		this.quantidadeContas = quantidadeContratos;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getStatusArquivo()
	 */
	public char getStatusArquivo() {
		return statusArquivo;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setStatusArquivo(char)
	 */
	public void setStatusArquivo(char statusArquivo) {
		this.statusArquivo = statusArquivo;
	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getTipoArquivo()
	 */
//	public Integer getTipoArquivo() {
//		return tipoArquivo;
//	}
//
//
//	/* (non-Javadoc)
//	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setTipoArquivo(java.lang.Integer)
//	 */
//	public void setTipoArquivo(Integer tipoArquivo) {
//		this.tipoArquivo = tipoArquivo;
//	}


	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#getValorContratos()
	 */
	public Float getValorContas() {
		return valorContas;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.arquivos.ArquivoInterface#setValorContratos(float)
	 */
	public void setValorContas(Float valorContas) {
		this.valorContas = valorContas;
	}

	public Set<ContaInterface> getContas() {
		return contas;
	}


	public void setContas(Set<ContaInterface> contas) {
		this.contas = contas;
	}
	
//	public String getDescricaoArquivo() {
//		return descricaoArquivo;
//	}
//
//	public void setDescricaoArquivo(String descricaoArquivo) {
//		this.descricaoArquivo = descricaoArquivo;
//	}

	public boolean isImpresso() {
		return impresso;
	}

	public void setImpresso(boolean impresso) {
		this.impresso = impresso;
	}

	public Integer getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(Integer tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}
	
	public int getSequencial() {
		return sequencial;
	}
	
	public void setSequencial(int sequencial) {
		this.sequencial = sequencial;
	}
	
	
	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public abstract void criarArquivo() throws Exception;	
	
	public String getUrl() {
		return REPOSITORIOTESTE + this.getFileName();
	}
	
	public byte[] getConteudo(){
		byte[] conteudo = null;
		try {
			FileInputStream file = new FileInputStream(this.getUrl());
			conteudo = new byte[file.available()];
			file.read(conteudo);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conteudo;
	}
	
	/**
	 * Utilizado para colocar conteúdo no arquivo.
	 * @param byte[] Recebe um vetor de bytes para inserir dados no arquivo
	 * @param usuario Usuario que executa a operacao
	 */
	public void processar(byte[] conteudo, UsuarioInterface usuario) throws Exception{
		try {
			FileOutputStream file = new FileOutputStream(getUrl());
			file.write(conteudo);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adiciona conteúdo(consignação específica) ao arquivo.
	 * @param String conteúdo do arquivo
	 * @param float valor da determinada consignação 
	 */
	public void addConteudo(String conteudo, BigDecimal valorConta) {
		this.conteudo.add(conteudo);
		this.quantidadeContas += 1;
		this.valorTotal = MoneyCalculation.getSoma(this.valorTotal, valorConta.floatValue());
		this.valorContas = valorTotal.floatValue();
	}

	public void setConteudo(byte[] conteudo) {
		this.arquivo = conteudo;
	}
	
	public abstract <C extends Conta, IF extends InformacaoFinanceiraInterface> void populate(C conta, IF informacaoFinanceira)throws Exception;

	/**
	 * Gera um nome para o arquivo.
	 * @param String descrição para formação do nome do arquivo.  
	 * @param ArquivoInterface arquivo com o restante dos dados 
	 */
	protected String gerarNomeArquivo() {
		String nome = "";
		String descricaoBanco = this.getBanco() != null ? this.getBanco().getDescricao() : "";
		String descricao = StringUtils.deleteWhitespace(descricaoBanco);
		String tipo = (ArquivoInterface.REMESSA.equals(this.getTipoArquivo()) ? "Remessa" : "Retorno");
		String competencia = Utils.format(this.getCompetencia(), "MMyyyy");
		String dataGeracao = Utils.format(new Date(), "ddMMyyyy HHmmss");
		nome = descricao + "_" + tipo + "_" + competencia + "_" + dataGeracao + ".txt";
		return nome;
	}
	
	/**
	 * Utilizado para marcar o arquivo com enviado.
	 */
	public void marcarComoEnviado() {
		if(this.getStatusArquivo() == ARQUIVO_ABERTO){
			this.setStatusArquivo(ARQUIVO_ENVIADO);
		}
	}

	/**
	 * Utilizado para cancelar o arquivo.
	 */
	public void cancelar(UsuarioInterface usuario) throws Exception{
		if(this.getStatusArquivo() == ARQUIVO_CANCELADO)
			throw new Exception("[ ID Arquivo: "+ this.getIdArquivo() +" ] A Remessa ja foi cancelada!");

		if(this.getStatusArquivo() == ARQUIVO_ENVIADO)
			throw new Exception("[ ID Arquivo: "+ this.getIdArquivo() +" ] A Remessa ja foi enviada!");
		
//		ArquivoInterface ultimaRemessaAberta = this.getBanco().getUltimoArquivo(COBRANCA, ARQUIVO_ABERTO);
//		ArquivoInterface ultimaRemessaEnviada = this.getBanco().getUltimoArquivo(COBRANCA, ARQUIVO_ENVIADO);
		
//		if(ultimaRemessaEnviada != null && ultimaRemessaEnviada.getIdArquivo() > this.getIdArquivo())
//			throw new Exception("[ ID Arquivo: "+ this.getIdArquivo() +" ] Uma remessa posterior a esta ja foi enviada!");
//		
//		if(ultimaRemessaAberta != null && this.getIdArquivo() != ultimaRemessaAberta.getIdArquivo())
//			throw new Exception("[ ID Arquivo: "+ this.getIdArquivo() +" ] Somente a ultima remessa pode ser cancelada!");
		
		this.setStatusArquivo(ARQUIVO_CANCELADO);
		String motivo = "O Arquivo de Remessa foi Cancelado";

//		for(ContaInterface conta : this.contas()){
//			cobranca.cancelar(usuario, motivo);
//		}

	}

	public Integer getTipoDeArquivo() {
		return tipoDeArquivo;
	}

	public void setTipoDeArquivo(Integer tipoDeArquivo) {
		this.tipoDeArquivo = tipoDeArquivo;
	}
	
}
