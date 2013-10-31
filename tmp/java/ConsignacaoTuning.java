package br.com.infowaypi.ecare.financeiro.consignacao.tuning;

import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.atendimentos.GuiaSimplesTuning;
import br.com.infowaypi.ecare.segurados.tuning.TitularFinanceiroTuning;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;

public class ConsignacaoTuning extends Consignacao<TitularFinanceiroTuning>{
	
	private Set<GuiaSimplesTuning> guiasConsignacao;
	private boolean cobrancaFuncionarioURBEfetivo;
	
	public ConsignacaoTuning() {
		super();
		guiasConsignacao = new HashSet<GuiaSimplesTuning>();
	}
	
	public Set<GuiaSimplesTuning> getGuiasConsignacao() {
		return guiasConsignacao;
	}
	
	public void setGuiasConsignacao(Set<GuiaSimplesTuning> guiasConsignacao) {
		this.guiasConsignacao = guiasConsignacao;
	}
	
	public void addGuia(GuiaSimplesTuning guia) {
		guia.setFluxoFinanceiro(this);
		this.getGuiasConsignacao().add(guia);
//		valorCoparticipacao = MoneyCalculation.getSoma(new BigDecimal(valorCoparticipacao), guia.getValorDaCoparticipacao()).floatValue();
	}
	
	public void addGuias(Set<GuiaSimplesTuning> guias){
		for (GuiaSimplesTuning guia : guias) {
			addGuia(guia);
		}
	}

	public boolean isCobrancaFuncionarioURBEfetivo() {
		return cobrancaFuncionarioURBEfetivo;
	}

	public void setCobrancaFuncionarioURBEfetivo(
			boolean cobrancaFuncionarioURBEfetivo) {
		this.cobrancaFuncionarioURBEfetivo = cobrancaFuncionarioURBEfetivo;
	}
	
}