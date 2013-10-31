package br.com.infowaypi.ecare.financeiro.consignacao.tuning;

import java.math.BigDecimal;

import br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao;
import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoMatriculaInterface;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;

public class ConsignacaoMatriculaTuning extends ConsignacaoTuning implements ConsignacaoMatriculaInterface{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6839081624329854155L;

	private AbstractMatricula matricula;
	private BigDecimal salarioBase;
	private BigDecimal salarioMatricula;
	private ArquivoDeEnvioConsignacao arquivoEnvio;
	private BigDecimal percentualAliquota;
	
	public ConsignacaoMatriculaTuning() {
		salarioBase = BigDecimal.ZERO;
		salarioMatricula = BigDecimal.ZERO;
		percentualAliquota = BigDecimal.ZERO;
		salarioBase.setScale(2, BigDecimal.ROUND_HALF_UP);
		salarioMatricula.setScale(2, BigDecimal.ROUND_HALF_UP);
		salarioMatricula.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public AbstractMatricula getMatricula() {
		return matricula;
	}
	
	public void setMatricula(AbstractMatricula matricula) {
		this.matricula = matricula;
	}
	
	@Override
	public TitularFinanceiroInterface getTitularFinanceiro() {
		return matricula;
	}
	
	public BigDecimal getSalarioBase() {
		return salarioBase;
	}
	
	public BigDecimal getSalarioMatricula() {
		return salarioMatricula;
	}
	
	public void setSalarioBase(BigDecimal salarioBase) {
		this.salarioBase = salarioBase;
	}
	
	public void setSalarioMatricula(BigDecimal salarioMatricula) {
		this.salarioMatricula = salarioMatricula;
	}
	
	public ArquivoDeEnvioConsignacao getArquivoEnvio() {
		return arquivoEnvio;
	}
	
	public void setArquivoEnvio(ArquivoDeEnvioConsignacao arquivoEnvio) {
		this.arquivoEnvio = arquivoEnvio;
	}
	
	public BigDecimal getPercentualAliquota() {
		return percentualAliquota;
	}
	
	public void setPercentualAliquota(BigDecimal percentualAliquota) {
		this.percentualAliquota = percentualAliquota;
	}
}
