package br.com.infowaypi.ecare.relatorio.portalBeneficiario.demostrativoIR;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaDependente;
import br.com.infowaypi.ecare.financeiro.conta.DetalheContaTitular;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.TitularFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.msr.utils.Utils;
/**
 * 
 * @author Emanuel
 *
 */
public class CompetenciaFluxo {

	private Date competencia;
	private List<FluxoFinanceiroInterface> fluxos;
	private boolean boletoSemDetalhe;
	
	public CompetenciaFluxo(Date competencia, List<FluxoFinanceiroInterface> fluxos) {
		this.competencia = competencia;
		this.fluxos = fluxos;
		this.boletoSemDetalhe = false;
	}
	
	public BigDecimal getValorFinancimento(){
		BigDecimal valor = BigDecimal.ZERO;
		for (FluxoFinanceiroInterface f : fluxos) {
			if(f.isCobranca()){
				BigDecimal valorFinanciamento = getValoresBoleto((Cobranca)f).get(0);
				valor = valor.add(valorFinanciamento);
			} else if(f.isConsignacao()){
				valor = valor.add(((Consignacao)f).getValorFinanciamento());
			}
		}
		return valor;
	}

	public BigDecimal getValorFinancimento_ComBoletosSemDetalhe(){
		BigDecimal valor = BigDecimal.ZERO;
		for (FluxoFinanceiroInterface f : fluxos) {
			if(f.isCobranca()){
				valor = valor.add(f.getValorPago());
			} else if(f.isConsignacao()){
				valor = valor.add(((Consignacao)f).getValorFinanciamento());
			}
		}
		return valor;
	}

	public BigDecimal getValorSegundaViaCartao(){
		BigDecimal valor = BigDecimal.ZERO;
		for (FluxoFinanceiroInterface f : fluxos) {
			if(f.isCobranca()){
				BigDecimal valorSegundaVia = getValoresBoleto((Cobranca)f).get(1);
				valor = valor.add(valorSegundaVia);
			} 
		}
		return valor;
	}
	
	public BigDecimal getValorCoparticipacao(){
		BigDecimal valor = BigDecimal.ZERO;
		for (FluxoFinanceiroInterface f : fluxos) {
			if(f.isCobranca()){
				BigDecimal valorCoparticipacao = getValoresBoleto((Cobranca)f).get(2);
				valor = valor.add(valorCoparticipacao);
			} else if(f.isConsignacao()){
				valor = valor.add(((Consignacao)f).getValorCoparticipacao());
			}
		}
		return valor;
	}
	
	public BigDecimal getValorTotal(){
		BigDecimal valor = BigDecimal.ZERO;
		if(isBoletoSemDetalhe()){
			valor = valor.add(getValorFinancimento_ComBoletosSemDetalhe().add(getValorCoparticipacao()).add(getValorSegundaViaCartao()));
		} else{
			valor = valor.add(getValorFinancimento().add(getValorCoparticipacao()).add(getValorSegundaViaCartao()));
		}
		
		return valor;
	}
	
	/**
	 * calcula os valores para boletos. <br>
	 * retorna um array com os valores: financiamento (get(0)), segundaviacartao(get(1)) e coparticipacao(get(2)), respectivamente.
	 * @param cobranca
	 * @return
	 */
	public List<BigDecimal> getValoresBoleto(Cobranca cobranca){
		List<BigDecimal> valores = new ArrayList<BigDecimal>();
		BigDecimal valorTotalFinanciamento    = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal valorTotalCoparticipacao   = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal valorTotalSegundaViaCartao = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		DetalheContaTitular detalheContaTitular = cobranca.getTitular().getDetalheContaTitular(cobranca.getCompetencia());
		
		valorTotalFinanciamento = valorTotalFinanciamento.add(detalheContaTitular.getValorFinanciamento());
		valorTotalCoparticipacao   = valorTotalCoparticipacao.add(detalheContaTitular.getValorCoparticipacao());
		valorTotalSegundaViaCartao = valorTotalSegundaViaCartao.add(detalheContaTitular.getValorSegundaViaCartao());
		
		for (DependenteSR dependente : cobranca.getTitular().getDependentes(SituacaoEnum.ATIVO.descricao())) {
			DetalheContaDependente detalheContaDependente = dependente.getDetalheContaDependente(cobranca.getCompetencia());
			valorTotalFinanciamento    = valorTotalFinanciamento.add(detalheContaDependente.getValorFinanciamento());
			valorTotalCoparticipacao   = valorTotalCoparticipacao.add(detalheContaDependente.getValorCoparticipacao());
			valorTotalSegundaViaCartao = valorTotalSegundaViaCartao.add(detalheContaDependente.getValorSegundaViaCartao());
		}
		
		BigDecimal zero = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		boolean isValorFinZerado = valorTotalFinanciamento.equals(zero);
		boolean isValorSegZerado = valorTotalSegundaViaCartao.equals(zero);
		boolean isValorCoZerado = valorTotalCoparticipacao.equals(zero);
		
		if(isValorFinZerado && isValorSegZerado && isValorCoZerado){
			setBoletoSemDetalhe(true);
			valores.add(cobranca.getValorCobrado());
		}else{
			valores.add(valorTotalFinanciamento);
		}
		valores.add(valorTotalSegundaViaCartao);
		valores.add(valorTotalCoparticipacao);
		return valores;
	}
	
	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Date getDataPagamento() {
		Date dataBoleto = null;
		Date dataConsignacao = null;
		Date dataPagamento = new Date();
		for (FluxoFinanceiroInterface f : fluxos) {
			if(f.isConsignacao()){
				dataConsignacao = ((Consignacao)f).getDataDoCredito();
			}
			else{
				dataBoleto = f.getDataPagamento();
			}
		}
		
		if(dataBoleto == null && dataConsignacao != null){
			dataPagamento = dataConsignacao;
		}
		else if(dataBoleto != null && dataConsignacao == null){
			dataPagamento = dataBoleto;
		}
		else if(dataBoleto != null && dataConsignacao != null){
			boolean boletoMaior = Utils.compareData(dataBoleto, dataConsignacao) >= 0;
			if(boletoMaior){
				dataPagamento = dataBoleto;
			}
			else{
				dataPagamento = dataConsignacao;
			}
		}
		
		return dataPagamento;
	}

	public List<FluxoFinanceiroInterface> getFluxos() {
		return fluxos;
	}

	public void setFluxos(List<FluxoFinanceiroInterface> fluxos) {
		this.fluxos = fluxos;
	}

	public boolean isBoletoSemDetalhe() {
		return boletoSemDetalhe;
	}

	public void setBoletoSemDetalhe(boolean boletoSemDetalhe) {
		this.boletoSemDetalhe = boletoSemDetalhe;
	}

	public TitularFinanceiroInterface getTitular(){
		TitularFinanceiroInterface titular = null;
		for (FluxoFinanceiroInterface f : fluxos) {
			if(f.getTitularFinanceiro() != null){
				titular = f.getTitularFinanceiro();
				break;
			}
		}
		return titular;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		CompetenciaFluxo outro = (CompetenciaFluxo) obj;
		EqualsBuilder eq =  new EqualsBuilder()
			.append(this.getCompetencia(),outro.getCompetencia());
		 return	eq.isEquals();
	}
	
}
