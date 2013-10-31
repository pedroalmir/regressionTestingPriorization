package br.com.infowaypi.ecare.segurados.tuning;

import java.math.BigDecimal;

import br.com.infowaypi.ecare.financeiro.consignacao.PisoFinanciamentoDependente;
import br.com.infowaypi.ecare.segurados.SeguradoConsignacaoInterface;
import br.com.infowaypi.ecare.services.Faixa;
import br.com.infowaypi.ecare.services.FaixaValorFixo;

@SuppressWarnings("serial")
public class DependenteConsignacaoTuning extends SeguradoConsignacaoTuning implements SeguradoConsignacaoInterface{

	private TitularConsignacaoTuning titular;
	private int ordem;
	
	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public TitularConsignacaoTuning getTitular() {
		return titular;
	}
	
	public void setTitular(TitularConsignacaoTuning titular) {
		this.titular = titular;
	}
	

	/**
	 * Calcula o valor do financiamento do dependente dado o salário base
	 * @return um array de BigDecimal onde a primeira posição é referente
	 * à alíquota e a segunda posição do array é referente ao valor do 
	 * financiamento.
	 */
	public BigDecimal[] getValorFinanciamentoDependente(BigDecimal salarioBase){
		BigDecimal[] aliquotaValor = new BigDecimal[2];

		double aliquota = getFaixaFinanciamento().getAliquota();
		if(salarioBase.compareTo(BigDecimal.ZERO) > 0){
			aliquotaValor[0] = new BigDecimal(aliquota);
		} else {
			aliquotaValor[0] = BigDecimal.ZERO;
			aliquotaValor[1] = BigDecimal.ZERO;
			return aliquotaValor;
		}
		
		aliquotaValor[1] = salarioBase.multiply(new BigDecimal(aliquota/100)).setScale(2, BigDecimal.ROUND_HALF_UP);
		
		//verifica o teto mínimo para a contribuição do Dependente
		if(aliquotaValor[1].compareTo(PisoFinanciamentoDependente.getPisoPorFaixaSalarial(salarioBase)) < 0) {
			aliquotaValor[1] = PisoFinanciamentoDependente.getPisoPorFaixaSalarial(salarioBase);
		}
		
		return aliquotaValor;
	}
	

	private Faixa getFaixaFinanciamento(){
		Faixa faixaEncontrada = null;
		int idade = this.getPessoaFisica().getIdade();
		
		for (Faixa faixa : Faixa.values()) {
			if((idade >= faixa.getIdadeMinima()) && (idade <= faixa.getIdadeMaxima())){
				faixaEncontrada = faixa;
			}
		}
		
		return faixaEncontrada;
	}
	
	private FaixaValorFixo getFaixaFixaFinanciamento(){
		FaixaValorFixo faixaEncontrada = null;
		int idade = this.getPessoaFisica().getIdade();
		for (FaixaValorFixo faixa : FaixaValorFixo.values()) {
			if((idade >= faixa.getFaixaInicial()) && (idade <= faixa.getFaixaFinal()))
				faixaEncontrada = faixa;
		}
		return faixaEncontrada;
	}
	

}
