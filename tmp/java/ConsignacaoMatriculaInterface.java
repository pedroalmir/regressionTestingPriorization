package br.com.infowaypi.ecare.financeiro.consignacao;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecare.financeiro.arquivo.ArquivoDeEnvioConsignacao;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.conta.ComponenteColecaoContas;
import br.com.infowaypi.ecarebc.financeiro.conta.ComponenteColecaoContasInterface;

public interface ConsignacaoMatriculaInterface extends ComponenteColecaoContasInterface{

	public abstract AbstractMatricula getMatricula();

	public abstract void setMatricula(AbstractMatricula matricula);

	public abstract TitularFinanceiroInterface getTitularFinanceiro();

	public abstract BigDecimal getSalarioBase();

	public abstract BigDecimal getSalarioMatricula();

	public abstract void setSalarioBase(BigDecimal salarioBase);

	public abstract void setSalarioMatricula(BigDecimal salarioMatricula);

	public abstract ArquivoDeEnvioConsignacao getArquivoEnvio();

	public abstract void setArquivoEnvio(ArquivoDeEnvioConsignacao arquivoEnvio);

	public abstract BigDecimal getPercentualAliquota();

	public abstract void setPercentualAliquota(BigDecimal percentualAliquota);

	public BigDecimal getValorFinanciamento();
	
	public void setValorFinanciamento(BigDecimal valorFinanciamento);
	
	public BigDecimal getValorParcelamento();
	
	public void setValorParcelamento(BigDecimal valorParcelamento);
	
	public Date getCompetencia();
	
	public BigDecimal getValorCoparticipacao();
	
	public void setValorCoparticipacao(BigDecimal valorCoparticipacao);
	
	public void setColecaoContas(ComponenteColecaoContas colecaoContas);
	
	public void setStatusConsignacao(char statusConsignacao);
	
	public char getStatusConsignacao();
	
	public Date getDataDoCredito() ;

	public void setDataDoCredito(Date dataDoCredito);
	
	public Date getDataPagamento();
	
	public void setDataPagamento(Date dataPagamento);
	
	public boolean isCobrancaFuncionarioURBEfetivo();

	public void setCobrancaFuncionarioURBEfetivo(boolean cobrancaFuncionarioURBEfetivo);

}