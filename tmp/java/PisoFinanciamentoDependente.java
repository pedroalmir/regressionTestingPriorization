package br.com.infowaypi.ecare.financeiro.consignacao;

import java.math.BigDecimal;

import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * Classe que encerra um conjunto de valores de contribuição para um dependente dada uma faixa salarial
 * do seu Titular. 
 * @author Marcus BOolean
 * @Changes Francisco Júnior 
 */
public enum PisoFinanciamentoDependente {
	/**
	 * A primeira faixa engloba duas faixas com o mesmo valor no piso.
	 * 	Incio   Fim       Piso
	 *  0,01	545,00	  10,00
	 *	545,01	1.090,00  10,00
	 */
	SALARIO_0_1090 (BigDecimal.ZERO, new BigDecimal(1090.0), new BigDecimal(10.0)),
	SALARIO_1090_2180 (new BigDecimal(1090.01), new BigDecimal(2180.00), new BigDecimal(20.0)),
	SALARIO_2180_OU_MAIS (new BigDecimal(2180.01), new BigDecimal(1000000.0), new BigDecimal(30.00));
	
	private BigDecimal salarioMinimo;
	private BigDecimal salarioMaximo;
	private BigDecimal piso;
	
	PisoFinanciamentoDependente(BigDecimal salarioMinimo, BigDecimal salarioMaximo, BigDecimal piso) {
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
		PisoFinanciamentoDependente faixa = getFaixaPorSalario(salario);
				
		return faixa.getPiso();
	}

	private static PisoFinanciamentoDependente getFaixaPorSalario(BigDecimal salario) {
		PisoFinanciamentoDependente resultado = null;
		
		for(PisoFinanciamentoDependente pisoEnum: PisoFinanciamentoDependente.values()){
			if(salario.compareTo(pisoEnum.getSalarioMinimo()) >= 0 && salario.compareTo(pisoEnum.getSalarioMaximo()) <= 0){
				resultado = pisoEnum; 
			}
		}
		
		return resultado;
	}
}
