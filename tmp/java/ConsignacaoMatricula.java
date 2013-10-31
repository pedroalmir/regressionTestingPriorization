package br.com.infowaypi.ecare.financeiro.consignacao;


import java.math.BigDecimal;

import br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;

public class ConsignacaoMatricula extends Consignacao<Matricula> implements ConsignacaoMatriculaInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6839081624329854155L;

	private AbstractMatricula matricula;
	private BigDecimal salarioBase;
	private BigDecimal salarioMatricula;
	private ArquivoDeEnvioConsignacao arquivoEnvio;
	private BigDecimal percentualAliquota;
	private boolean cobrancaFuncionarioURBEfetivo;
	
	public ConsignacaoMatricula() {
		salarioBase = BigDecimal.ZERO;
		salarioMatricula = BigDecimal.ZERO;
		percentualAliquota = BigDecimal.ZERO;
		salarioBase.setScale(2, BigDecimal.ROUND_HALF_UP);
		salarioMatricula.setScale(2, BigDecimal.ROUND_HALF_UP);
		salarioMatricula.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#getMatricula()
	 */
	public AbstractMatricula getMatricula() {
		return matricula;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#setMatricula(br.com.infowaypi.ecare.segurados.Matricula)
	 */
	public void setMatricula(AbstractMatricula matricula) {
		this.matricula = matricula;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#getTitularFinanceiro()
	 */
	@Override
	public TitularFinanceiroInterface getTitularFinanceiro() {
		return matricula;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#getSalarioBase()
	 */
	public BigDecimal getSalarioBase() {
		return salarioBase;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#getSalarioMatricula()
	 */
	public BigDecimal getSalarioMatricula() {
		return salarioMatricula;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#setSalarioBase(java.math.BigDecimal)
	 */
	public void setSalarioBase(BigDecimal salarioBase) {
		this.salarioBase = salarioBase;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#setSalarioMatricula(java.math.BigDecimal)
	 */
	public void setSalarioMatricula(BigDecimal salarioMatricula) {
		this.salarioMatricula = salarioMatricula;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#getArquivoEnvio()
	 */
	public ArquivoDeEnvioConsignacao getArquivoEnvio() {
		return arquivoEnvio;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#setArquivoEnvio(br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao)
	 */
	public void setArquivoEnvio(ArquivoDeEnvioConsignacao arquivoEnvio) {
		this.arquivoEnvio = arquivoEnvio;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#getPercentualAliquota()
	 */
	public BigDecimal getPercentualAliquota() {
		return percentualAliquota;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface#setPercentualAliquota(java.math.BigDecimal)
	 */
	public void setPercentualAliquota(BigDecimal percentualAliquota) {
		this.percentualAliquota = percentualAliquota;
	}

	public boolean isCobrancaFuncionarioURBEfetivo() {
		return cobrancaFuncionarioURBEfetivo;
	}

	public void setCobrancaFuncionarioURBEfetivo(
			boolean cobrancaFuncionarioURBEfetivo) {
		this.cobrancaFuncionarioURBEfetivo = cobrancaFuncionarioURBEfetivo;
	}
	
	
}
