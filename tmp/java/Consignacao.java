package br.com.infowaypi.ecarebc.financeiro.consignacao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeEnvio;
import br.com.infowaypi.ecarebc.financeiro.conta.Conta;
import br.com.infowaypi.ecarebc.financeiro.conta.ContaInterface;
import br.com.infowaypi.ecarebc.financeiro.conta.ImplColecaoContas;
import br.com.infowaypi.msr.financeiro.Banco;
import br.com.infowaypi.msr.utils.Utils;

public class Consignacao<T extends TitularFinanceiroInterface> extends ImplColecaoContas implements FluxoFinanceiroInterface{
	
	public static char STATUS_PAGO = 'P';
	public static char STATUS_ABERTO = 'A';
	private static final long serialVersionUID = 1L;
	private Long idFluxoFinanceiro;
	private Date competencia;
	private T titular;
	private Banco banco;
	private String agencia;
	private String contaCorrente;
	private String operacao;
	private Date dataDoCredito;
	private Date dataPagamento;
	private char statusConsignacao;
	private Set<GuiaSimples> guias;
	private BigDecimal valorCoparticipacao;
	private BigDecimal valorParcelamento;
	private BigDecimal valorFinanciamento;
	private BigDecimal valorPrevidencia;
	private String matriculaNoEstado;
	private ArquivoDeEnvio arquivoEnvioConsignacao;
	private Integer tipoDeRetorno;
	private BigDecimal valorRetornoCoparticipacao;
	private BigDecimal valorADeduzirDaCoparticipacao;
	
	public Consignacao(){
		super();
		guias = new HashSet<GuiaSimples>();
		valorCoparticipacao = BigDecimal.ZERO;
		valorRetornoCoparticipacao = BigDecimal.ZERO;
		valorFinanciamento = BigDecimal.ZERO;
		valorParcelamento = BigDecimal.ZERO;
		valorPrevidencia = BigDecimal.ZERO;
		valorADeduzirDaCoparticipacao = BigDecimal.ZERO;
		tipoDeRetorno = 0;

		valorCoparticipacao.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorRetornoCoparticipacao.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorFinanciamento.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorPrevidencia.setScale(2, BigDecimal.ROUND_HALF_UP);
		valorADeduzirDaCoparticipacao.setScale(2, BigDecimal.ROUND_HALF_UP);

	}
	
	public String getCompetenciaFormatada(){
		return Utils.format(competencia, "MM/yyyy");
	}
	
	public String getAgencia() {
		return agencia;
	}
	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}
	
	public Banco getBanco() {
		return banco;
	}
	public void setBanco(Banco banco) {
		this.banco = banco;
	}
	
	public Date getCompetencia() {
		return competencia;
	}
	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	public String getContaCorrente() {
		return contaCorrente;
	}
	public void setContaCorrente(String contaCorrente) {
		this.contaCorrente = contaCorrente;
	}
	
	public int getQuantidadeGuias(){
		return this.getGuias().size();
	}
	
	public Set<GuiaSimples> getGuias() {
		return guias;
	}
	public void setGuias(Set<GuiaSimples> guias) {
		this.guias = guias;
	}
	
	public String getOperacao() {
		return operacao;
	}
	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}
	
	public T getTitular() {
		return titular;
	}

	public void setTitular(T titular) {
		this.titular = titular;
	}
	
	
	public BigDecimal getValorCoparticipacao() {
		return valorCoparticipacao;
	}
	public void setValorCoparticipacao(BigDecimal valorCoparticipacao) {
		this.valorCoparticipacao = valorCoparticipacao;
	}
	
	public BigDecimal getValorFinanciamento() {
		return valorFinanciamento;
	}
	public void setValorFinanciamento(BigDecimal valorFinanciamento) {
		this.valorFinanciamento = valorFinanciamento;
	}
	
	public Date getDataDoCredito() {
		return this.dataDoCredito;
	}

	public void setDataDoCredito(Date dataDoCredito) {
		this.dataDoCredito = dataDoCredito;
	}

	public String getMatriculaNoEstado() {
		return matriculaNoEstado;
	}

	public void setMatriculaNoEstado(String matriculaNoEstado) {
		this.matriculaNoEstado = matriculaNoEstado;
	}

	public char getStatusConsignacao() {
		return statusConsignacao;
	}

	public void setStatusConsignacao(char statusConsignacao) {
		this.statusConsignacao = statusConsignacao;
	}

	public BigDecimal getValorPrevidencia() {
		return valorPrevidencia;
	}

	public void setValorPrevidencia(BigDecimal valorPrevidencia) {
		this.valorPrevidencia = valorPrevidencia;
	}

	public void addGuia(GuiaSimples guia) {
		guia.setFluxoFinanceiro(this);
		this.getGuias().add(guia);
//		valorCoparticipacao = MoneyCalculation.getSoma(new BigDecimal(valorCoparticipacao), guia.getValorDaCoparticipacao()).floatValue();
	}
	
	public void addGuias(Collection<GuiaSimples> guias){
		for (GuiaSimples guia : guias) {
			addGuia(guia);
		}
	}

	public Integer getTipoDeRetorno() {
		return tipoDeRetorno;
	}

	public void setTipoDeRetorno(Integer tipoDeRetorno) {
		this.tipoDeRetorno = tipoDeRetorno;
	}

	public BigDecimal getValorRetornoCoparticipacao() {
		return this.valorRetornoCoparticipacao;
	}

	public void setValorRetornoCoparticipacao(BigDecimal valorRetornoCoparticipacao) {
		this.valorRetornoCoparticipacao = valorRetornoCoparticipacao;
	}

	public void add(ContaInterface conta) {
		// TODO Auto-generated method stub
		
	}

	public void addAll(Collection<ContaInterface> contas) {
		// TODO Auto-generated method stub
		
	}

	public void setContas(Set<ContaInterface> contas) {
		// TODO Auto-generated method stub
	}
	
	
	public Long getIdFluxoFinanceiro() {
		return idFluxoFinanceiro;
	}

	public TitularFinanceiroInterface getTitularFinanceiro() {
		return titular;
	}

	public void setIdFluxoFinanceiro(Long idFluxoFinanceiro) {
		this.idFluxoFinanceiro = idFluxoFinanceiro;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public Date getDataVencimento() {
		return null;
	}

	public BigDecimal getValorCobrado() {
		return valorFinanciamento.add(valorCoparticipacao);
	}

	public BigDecimal getValorPago() {
		return valorFinanciamento.add(valorCoparticipacao);
	}

	public void processarVencimento() {
		
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public void setDataVencimento(Date dataVencimento) {
		
	}

	public void setValorCobrado(BigDecimal valorCobrado) {
		
	}

	public void setValorPago(BigDecimal valorPago) {
		
	}
	
	public void tocarObjetos(){
		this.getDataPagamento();
		this.getValorPago();
	}

	public Conta getConta(Date competencia){
		for(ContaInterface conta : this.getContas()){
			if(conta.getCompetencia().compareTo(competencia) == 0)
				return (Conta) conta;
		}
		return null;
	}
	
	public BigDecimal getValorADeduzirDaCoparticipacao() {
		return valorADeduzirDaCoparticipacao;
	}
	
	public void setValorADeduzirDaCoparticipacao(
			BigDecimal valorADeduzirDaCoparticipacao) {
		this.valorADeduzirDaCoparticipacao = valorADeduzirDaCoparticipacao;
	}
	
	public BigDecimal getValorParcelamento() {
		return valorParcelamento;
	}
	
	public void setValorParcelamento(
			BigDecimal valorParcelamento) {
		this.valorParcelamento = valorParcelamento;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Consignacao)) {
			return false;
		}
		Consignacao consignacao = (Consignacao) obj;
		return new EqualsBuilder().append(this.getIdFluxoFinanceiro(), consignacao.getIdFluxoFinanceiro()).isEquals();
		
	}
	
	public String getDescricaoStatus(){
		String status = "";
		if(statusConsignacao == 'A')
			status =  "Aberto(a)";
		if(statusConsignacao == 'P')
			status = "Pago(a)";
		return status;
	}

	public boolean isCobranca() {
		return false;
	}

	public boolean isConsignacao() {
		return true;
	}

	public boolean isFaturamento() {
		return false;
	}
	
}