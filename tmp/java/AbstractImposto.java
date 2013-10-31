package br.com.infowaypi.ecarebc.financeiro.faturamento.retencao;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.MoneyCalculation;

@SuppressWarnings("serial")
public abstract class AbstractImposto {
	
	public static final Integer PESSOA_FISICA = Constantes.PESSOA_FISICA;
	public static final Integer PESSOA_JURIDICA = Constantes.PESSOA_JURIDICA;
	
	private BigDecimal valorBaseCalculo;
	private Date competencia;
	
	public AbstractImposto(){
	}

	public void setValorBaseCalculo(BigDecimal valorBase) {
		valorBaseCalculo = valorBase;
	}
	
	public BigDecimal getValorBaseCalculo(){
		return MoneyCalculation.rounded(valorBaseCalculo);
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}
	
	public Boolean validate() throws ValidateException {
		return true;	
	}
	
}
