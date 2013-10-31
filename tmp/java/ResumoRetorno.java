/**
 * 
 */
package br.com.infowaypi.ecare.financeiro.consignacao;

import java.util.Date;

import br.com.infowaypi.molecular.TransactionManagerHibernate;

/**
 * @author Marcus
 *
 */
public class ResumoRetorno {
	
	private byte[] conteudoArquivo;
	private int numeroDeCPFS;
	private int numeroDeCPFSDuplicados;
	private Date competencia;
	private Date dataPagamento;
	private Exception excecaoDoFluxo;
	
	private TransactionManagerHibernate tm;
	
	public TransactionManagerHibernate getTransaction() {
		return tm;
	}
	
	public Date getCompetencia() {
		return competencia;
	}


	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}


	public Date getDataPagamento() {
		return dataPagamento;
	}


	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}


	public int getNumeroDeCPFS() {
		return numeroDeCPFS;
	}


	public void setNumeroDeCPFS(int numeroDeCPFS) {
		this.numeroDeCPFS = numeroDeCPFS;
	}


	public ResumoRetorno() {
		tm = new TransactionManagerHibernate();
	}


	public byte[] getConteudoArquivo() {
		return conteudoArquivo;
	}


	public void setConteudoArquivo(byte[] conteudoArquivo) {
		this.conteudoArquivo = conteudoArquivo;
	}


	public int getNumeroDeCPFSDuplicados() {
		return numeroDeCPFSDuplicados;
	}


	public void setNumeroDeCPFSDuplicados(int numeroDeCPFSDuplicados) {
		this.numeroDeCPFSDuplicados = numeroDeCPFSDuplicados;
	}

	public void setExcecaoDoFluxo(Exception excecaoDoFluxo) {
		this.excecaoDoFluxo = excecaoDoFluxo;
	}

	public Exception getExcecaoDoFluxo() {
		return excecaoDoFluxo;
	}
}
