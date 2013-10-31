package br.com.infowaypi.ecarebc.procedimentos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.msr.utils.MoneyCalculation;

public class Porte implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long idPorte;
	private String descricao;
	private Float valorPorte;
	private Float valorPorteCalculado;
	private Float valorTaxaSala;
	private Set<TabelaCBHPM> tabelaCBHPM;
	
	public Porte() {
		tabelaCBHPM = new HashSet<TabelaCBHPM>();
		this.valorPorte = 0f;
		this.valorPorteCalculado = 0f;
		this.valorTaxaSala = 0f;
	}
	
	public Long getIdPorte() {
		return idPorte;
	}
	
	public void setIdPorte(Long idPorte) {
		this.idPorte = idPorte;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Set<TabelaCBHPM> getTabelaCBHPM() {
		return tabelaCBHPM;
	}
	
	public void setTabelaCBHPM(Set<TabelaCBHPM> tabelaCBHPM) {
		this.tabelaCBHPM = tabelaCBHPM;
	}
	
	public Float getValorPorte() {
		return valorPorte;
	}

	public void setValorPorte(Float valorPorte) {
		this.valorPorte = valorPorte;
	}

	public Float getValorPorteCalculado() {
		return valorPorteCalculado;
	}

	public void setValorPorteCalculado(Float valorPorteCalculado) {
		this.valorPorteCalculado = valorPorteCalculado;
	}

	public Float getValorTaxaSala() {
		return valorTaxaSala;
	}

	public void setValorTaxaSala(Float valorTaxaSala) {
		this.valorTaxaSala = valorTaxaSala;
	}

	public Float aplicaDescontoValorPorte(Float desconto){
		return  MoneyCalculation.rounded(MoneyCalculation.acumule(valorPorte.floatValue(),new Float(desconto))).floatValue();
	}
	
	
	public Boolean validate(){
		return true;
	}
	
	public void tocarObjetos(){
		
		for (TabelaCBHPM element : this.getTabelaCBHPM()) {
			element.getCarencia();
		}
	}
	
}
