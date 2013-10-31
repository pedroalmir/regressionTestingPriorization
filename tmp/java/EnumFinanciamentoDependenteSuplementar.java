package br.com.infowaypi.ecare.segurados;

import java.math.BigDecimal;

/**
 * Enum que armzena o valor do financiamento para segurados do tipo Dependente Suplementar de acordo com a faixa
 * de idade
 * 
 * Retorna o valor individual do dependente de acordo com a faixa abaixo:
 * <table>
 * 	<tr><td>0-18 anos 67,18</td></tr>
 *  <tr><td>19-23 anos 78,92</td></tr>
 *  <tr><td>24-28 anos 97,65</td></tr>
 *  <tr><td>29-33 anos 112,76</td></tr>
 *  <tr><td>34-38 anos 137,36</td></tr>
 *  <tr><td>39-43 anos 165,71</td></tr>
 *  <tr><td>44-48 anos 197,55</td></tr>
 *  <tr><td>49-53 anos 244,51</td></tr>
 *  <tr><td>54-58 anos 322,43</td></tr>
 *  <tr><td>59 ou + anos 402,47</td></tr>
 * </table> 
 * @author Junior
 */
public enum EnumFinanciamentoDependenteSuplementar {
	
	FAIXA_0_18(0, 18, new BigDecimal(67.18)),
	FAIXA_19_23(19, 23, new BigDecimal(78.92)),
	FAIXA_24_28(24, 28, new BigDecimal(97.65)),
	FAIXA_29_33(29, 33, new BigDecimal(112.76)),
	FAIXA_34_38(34, 38, new BigDecimal(137.36)),
	FAIXA_39_43(39, 43, new BigDecimal(165.71)),
	FAIXA_44_48(44, 48, new BigDecimal(197.55)),
	FAIXA_49_53(49, 53, new BigDecimal(244.51)),
	FAIXA_54_58(54, 58, new BigDecimal(322.43)),
	FAIXA_59_OU_MAIS(59, Integer.MAX_VALUE, new BigDecimal(402.47));
	
	private int inicioFaixaIdade;
	private int finalFaixaIdade;
	private BigDecimal valorFinanciamento;
	
	private EnumFinanciamentoDependenteSuplementar(int inicioFaixaIdade, int finalFaixaIdade, BigDecimal financiamento) {
		this.inicioFaixaIdade = inicioFaixaIdade;
		this.finalFaixaIdade = finalFaixaIdade;
		this.valorFinanciamento = financiamento;
	}
	
	public boolean isContidoNaFaixaDeIdade(int idade){
		boolean resultado = false;
		
		if(idade >= this.inicioFaixaIdade && idade <= this.finalFaixaIdade){
			resultado = true;	
		}

		return resultado;
	}
	
	public static BigDecimal getValorFinanciamento(int idade){
		BigDecimal resultado = BigDecimal.ZERO;
		
		for (EnumFinanciamentoDependenteSuplementar faixaDeIdade : EnumFinanciamentoDependenteSuplementar.values()) {
			if(faixaDeIdade.isContidoNaFaixaDeIdade(idade)){
				resultado = faixaDeIdade.valorFinanciamento;
			}
		}
		
		return resultado;
	}
}
