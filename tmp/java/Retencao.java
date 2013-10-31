package br.com.infowaypi.ecarebc.financeiro.faturamento.retencao;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.msr.utils.MoneyCalculation;

@SuppressWarnings("serial")
public class Retencao implements Serializable{

	public static final int IMPOSTO_DE_RENDA = Constantes.IMPOSTO_DE_RENDA;
	public static final int ISS = Constantes.ISS;
	public static final int INSS = Constantes.INSS;
	
	private Long idRetencao;
	private BigDecimal valorBaseDoCalculo;
	private float percentualDoCalculo;
	private BigDecimal valorDeducaoBaseDoCalculo;
	private BigDecimal valor;
	private String descricao;
	private BigDecimal valorDeducao;
	private AbstractFaturamento faturamento;
	private Integer tipoDeRetencao;
	
	public Retencao(){
		valorBaseDoCalculo = new BigDecimal(0f);
		valor = new BigDecimal(0f);
		valorDeducao = new BigDecimal(0f);
	}
	
	public String getDescricaoTipoDeRetencao() {
		if(tipoDeRetencao == Constantes.IMPOSTO_DE_RENDA)
			return "I.R.";
		else if(tipoDeRetencao == Constantes.ISS)
			return "ISS";
		else if(tipoDeRetencao == Constantes.INSS)
			return "INSS";
	   return "";
	}
	
	void setIdRetencao(Long idRetencao) {
		this.idRetencao = idRetencao;
	}
	public Long getIdRetencao() {
		return idRetencao;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public float getPercentualDoCalculo() {
		return percentualDoCalculo;
	}
	public void setPercentualDoCalculo(float percentualDoCalculo) {
		this.percentualDoCalculo = percentualDoCalculo;
	}

	public BigDecimal getValor() {
		return MoneyCalculation.rounded(this.valor);
	}

	public BigDecimal getValorTotal() {
		return MoneyCalculation.rounded(valor.subtract(valorDeducao));
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getValorBaseDoCalculo() {
		return MoneyCalculation.rounded(valorBaseDoCalculo);
	}
	public void setValorBaseDoCalculo(BigDecimal valorBaseDoCalculo) {
		this.valorBaseDoCalculo = valorBaseDoCalculo;
	}
	
	public AbstractFaturamento getFaturamento() {
		return faturamento;
	}
	public void setFaturamento(AbstractFaturamento faturamento) {
		this.faturamento = faturamento;
	}

	public void setValorDeducaoBaseDoCalculo(BigDecimal valorDeducao) {
		this.valorDeducaoBaseDoCalculo = valorDeducao;
	}

	public BigDecimal getValorDeducaoBaseDoCalculo() {
		return MoneyCalculation.rounded(valorDeducaoBaseDoCalculo);
	}

	public BigDecimal getValorDeducao() {
		return MoneyCalculation.rounded(valorDeducao);
	}
	public void setValorDeducao(BigDecimal valorDeducao) {
		this.valorDeducao = valorDeducao;
	}

	public Integer getTipoDeRetencao() {
		return tipoDeRetencao;
	}
	public void setTipoDeRetencao(Integer tipoDeRetencao) {
		this.tipoDeRetencao = tipoDeRetencao;
	}
	
}