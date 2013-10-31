/* 
 * Criado em 14/07/2006 - 11:43:43
 */
package br.com.infowaypi.ecarebc.planos;

import static br.com.infowaypi.ecarebc.planos.Faixa.NUMERO_FAIXAS;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * @author <a href="mailto:jonhnnyweslley@gmail.com">Jonhnny Weslley</a>
 */
public class ReajustadorFaixas extends ListaFaixa {

	private static final long serialVersionUID = 1L;
	public static final Float[] SEM_REAJUSTES = new Float[]{};
	private List<Float> reajustes;

	public ReajustadorFaixas() {
		reajustes = new ArrayList<Float>();
	}

	public void setReajustes(List<Float> reajustes) {
		this.reajustes = reajustes;
	}

	public List<Float> getReajustes() {
		return reajustes;
	}

	public void aplicarReajuste(float valor) {
		for (int i = 0; i < Faixa.values().length; i++) {
			reajustes.add(valor);
		}
	}

	public Float[] getReajustes(Faixa faixa) {
		int numeroDeReajustes = getNumeroReajustes();
		if(numeroDeReajustes == 0) {
			return SEM_REAJUSTES;
		}
		final int index = faixa.ordinal();
		Float[] ajustes = new Float[numeroDeReajustes];
		for (int i = 0; i < numeroDeReajustes; i++) {			
			ajustes[i] = reajustes.get(index + (NUMERO_FAIXAS * i));
		}
		return ajustes;
	}

	public float getValorReajustado(Faixa faixa) {
		final float valorFaixa = getValor(faixa);
		return MoneyCalculation.acumule(valorFaixa, getReajustes(faixa)).floatValue();
	}
	
	public float getUltimoReajuste(Faixa faixa) {
		int index = lastIndexFor(faixa);
		if(index < 0) {
			return 0;
		}
		return this.reajustes.get(index);
	}

	public void setUltimoReajuste(Faixa faixa, float valor) {
		int index = lastIndexFor(faixa);
		if(index < 0) {
			aplicarReajuste(0f);
			index = faixa.ordinal();
		}
		this.reajustes.set(index, valor);
	}

	private int lastIndexFor(Faixa faixa) {
		if(reajustes.isEmpty()) {
			return -1; 
		}
		int numeroDeReajustes = getNumeroReajustes();
		int index = faixa.ordinal() + (NUMERO_FAIXAS * --numeroDeReajustes);
		return index;
	}

	public int getNumeroReajustes() {
		return reajustes.size() / NUMERO_FAIXAS;
	}

}