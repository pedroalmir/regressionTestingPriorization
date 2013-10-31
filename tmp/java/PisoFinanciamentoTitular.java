package br.com.infowaypi.ecare.financeiro.consignacao;

import java.math.BigDecimal;

import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * Classe que encerra um conjunto de valores de contribuição para um Titular dada sua faixa salarial.
 * @author Marcus BOolean
 */
public enum PisoFinanciamentoTitular {
	
	SALARIO_0_545 (BigDecimal.ZERO, new BigDecimal(545.0), new BigDecimal(15.0)),
	SALARIO_545_1090 (new BigDecimal(545.01), new BigDecimal(1090.0), new BigDecimal(30.0)),
	/**
	 * A Última faixa engloba duas faixas com o mesmo valor no piso.
	 * 	Incio   	Fim       	Piso
	 *  1.090,01	2.180,00	35,00
     *  2.180,01	6.540,00	35,00
	 */
	SALARIO_1090_OU_MAIS (new BigDecimal(1090.01), new BigDecimal(1000000.0), new BigDecimal(35.00));
	
	private BigDecimal salarioMinimo;
	private BigDecimal salarioMaximo;
	private BigDecimal piso;
	
	PisoFinanciamentoTitular(BigDecimal salarioMinimo, BigDecimal salarioMaximo, BigDecimal piso) {
		this.salarioMinimo = MoneyCalculation.rounded(salarioMinimo);
		this.salarioMaximo = MoneyCalculation.rounded(salarioMaximo);
		this.piso = MoneyCalculation.rounded(piso);
	}

	public BigDecimal getSalarioMinimo() {
		return salarioMinimo;
	}

	public void setSalarioMinimo(BigDecimal salarioMinimo) {
		this.salarioMinimo = salarioMinimo;
	}

	public BigDecimal getSalarioMaximo() {
		return salarioMaximo;
	}

	public void setSalarioMaximo(BigDecimal salarioMaximo) {
		this.salarioMaximo = salarioMaximo;
	}

	public BigDecimal getPiso() {
		return piso;
	}

	public void setPiso(BigDecimal piso) {
		this.piso = piso;
	}
	
	public static BigDecimal getPisoPorFaixaSalarial(BigDecimal salario) {
		PisoFinanciamentoTitular faixa = getFaixaPorSalario(salario);
		
		return faixa.getPiso();
	}
	
	private static PisoFinanciamentoTitular getFaixaPorSalario(BigDecimal salario) {
		PisoFinanciamentoTitular resultado = null;
		
		for(PisoFinanciamentoTitular pisoEnum: PisoFinanciamentoTitular.values()){
			if(salario.compareTo(pisoEnum.getSalarioMinimo()) >= 0 && salario.compareTo(pisoEnum.getSalarioMaximo()) <= 0){
				resultado = pisoEnum; 
			}
		}
		
		return resultado;
	}
}
