package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * Resumo de guias.
 * @author Mário Sérgio Coelho Marroquim
 */
public class ResumoProcedimento {
	
	TabelaCBHPM procedimento;
	int tipoProcedimento;
	Integer quantidade = 0;
	Float valorTotal = 0f;
	Float valorUnitario = 0f;
	
	public ResumoProcedimento(ProcedimentoInterface procedimento) {
		if(procedimento == null)
			return;
		
		this.procedimento = procedimento.getProcedimentoDaTabelaCBHPM();
		this.quantidade = procedimento.getQuantidade();
		this.tipoProcedimento = procedimento.getTipoProcedimento();
		this.valorTotal = procedimento.getValorTotal().floatValue();
		this.valorUnitario = procedimento.getValorAtualDoProcedimento().floatValue();
	}

	public TabelaCBHPM getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(TabelaCBHPM procedimento) {
		this.procedimento = procedimento;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void incrementarQuantidade(int quantidade) {
		this.quantidade+= quantidade;
	}

	public Float getValorTotal() {
		return valorTotal;
	}

	public void incrementarValor(float valorTotal) {
		this.valorTotal = MoneyCalculation.getSoma(new BigDecimal(this.valorTotal), valorTotal).floatValue();
	}

	public Float getValorUnitario() {
		return valorUnitario;
	}
	
	public int getTipoProcedimento() {
		return tipoProcedimento;
	}
	
	public void setTipoProcedimento(int tipoProcedimento) {
		this.tipoProcedimento = tipoProcedimento;
	}
	
}