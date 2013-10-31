package br.com.infowaypi.ecare.financeiro.boletos;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.CobrancaInterface;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.msr.utils.Utils;

public class RemessaDeBoletos implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long idRemessaDeBoletos;
	private Set<ContaInterface> contas;
	private Date dataGeracao;
	private byte[] conteudoArquivo;
	private Date competencia;

	private Set<Cobranca> cobranca;
	
	public RemessaDeBoletos(){
		this.contas = new HashSet<ContaInterface>();
		this.cobranca = new HashSet<Cobranca>();
	}
	
	/**
	 * 
	 * @return a quantidade de boletos criados nesta remessa
	 */
	public int getQuantidadeDeBoletos(){
		return this.contas.size();
	}
	
	public String getNomeArquivo(){
		return "RemessaDeBoletos_"+Utils.format(getDataGeracao(), "dd_MM_yyyy_")+getIdRemessaDeBoletos()+".pdf";
	}

	public Long getIdRemessaDeBoletos() {
		return idRemessaDeBoletos;
	}

	public void setIdRemessaDeBoletos(Long idRemessaDeBoletos) {
		this.idRemessaDeBoletos = idRemessaDeBoletos;
	}

	public Set<ContaInterface> getContas() {
		return contas;
	}

	public void setContas(Set<ContaInterface> contas) {
		this.contas = contas;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public byte[] getConteudoArquivo() {
		return conteudoArquivo;
	}

	public void setConteudoArquivo(byte[] conteudoArquivo) {
		this.conteudoArquivo = conteudoArquivo;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Date getCompetencia() {
		return competencia;
	}
	
	public Set<Cobranca> getCobranca() {
		return cobranca;
	}
	
	public void setCobranca(Set<Cobranca> cobranca) {
		this.cobranca = cobranca;
	}

}
