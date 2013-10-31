/* 
 * Criado em 13/07/2006 - 11:37:46
 */
package br.com.infowaypi.ecarebc.planos;

import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_01;
import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_02;
import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_03;
import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_04;
import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_05;
import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_06;
import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_07;
import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_08;
import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_09;
import static br.com.infowaypi.ecarebc.planos.Faixa.FAIXA_10;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jonhnnyweslley@gmail.com">Jonhnny Weslley</a>
 */
public class ListaFaixa implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Float> valores;

	public ListaFaixa() {
		this.valores = new FloatList();
	}

	public List<Float> getValores() {
		return valores;
	}

	public void setValores(List<Float> valores) {
		this.valores = valores;
	}

	public float getFaixa01() { return getValor(FAIXA_01); }
	public float getFaixa02() { return getValor(FAIXA_02); }
	public float getFaixa03() { return getValor(FAIXA_03); }
	public float getFaixa04() { return getValor(FAIXA_04); }
	public float getFaixa05() { return getValor(FAIXA_05); }
	public float getFaixa06() { return getValor(FAIXA_06); }
	public float getFaixa07() { return getValor(FAIXA_07); }
	public float getFaixa08() { return getValor(FAIXA_08); }
	public float getFaixa09() { return getValor(FAIXA_09); }
	public float getFaixa10() { return getValor(FAIXA_10); }

	public void setFaixa01(float valor) { setValor(FAIXA_01, valor); }	
	public void setFaixa02(float valor) { setValor(FAIXA_02, valor); }	
	public void setFaixa03(float valor) { setValor(FAIXA_03, valor); }	
	public void setFaixa04(float valor) { setValor(FAIXA_04, valor); }	
	public void setFaixa05(float valor) { setValor(FAIXA_05, valor); }	
	public void setFaixa06(float valor) { setValor(FAIXA_06, valor); }	
	public void setFaixa07(float valor) { setValor(FAIXA_07, valor); }	
	public void setFaixa08(float valor) { setValor(FAIXA_08, valor); }
	public void setFaixa09(float valor) { setValor(FAIXA_09, valor); }
	public void setFaixa10(float valor) { setValor(FAIXA_10, valor); }

	protected float getValor(Faixa faixa) {
		return this.valores.get(faixa.ordinal());
	}

	protected void setValor(Faixa faixa, float valor) {
		this.valores.set(faixa.ordinal(), valor);		
	}

	protected static class FloatList extends ArrayList<Float> {

		private static final long serialVersionUID = 1L;

		FloatList() {
			super(Faixa.values().length);
			for (int i = 0; i < Faixa.values().length; i++) {
				add(0f);
			}
		}

	}

}