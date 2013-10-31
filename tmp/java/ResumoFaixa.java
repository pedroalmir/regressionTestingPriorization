package br.com.infowaypi.ecare.resumos;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecare.services.Faixa;


/**
 * 
 * @author Josino Rodrigues
 * Este resumo armazena uma faixa etária,quantidade de BENEFICIÁRIOS nessa faixa,
 * valor da receita  e despeza dessa faixa.
 *
 */
public class ResumoFaixa implements Comparable<ResumoFaixa>{
	
	private Faixa faixa;
	private int quantidadeDeSegurados;
	private BigDecimal receita;
	private BigDecimal despesa;
	
	public ResumoFaixa() {
		this(null);
	}
	
	public ResumoFaixa(Faixa faixa) {
		this.faixa = faixa;
		receita = BigDecimal.ZERO;
		despesa = BigDecimal.ZERO;
	}
	
	public BigDecimal getSaldo(){
		return receita.subtract(despesa);
	}

	public Faixa getFaixa() {
		return faixa;
	}

	public void setFaixa(Faixa faixa) {
		this.faixa = faixa;
	}

	public BigDecimal getReceita(){
		return receita;
	}

	public void setReceita(BigDecimal receita){
		this.receita = receita;
	}

	public BigDecimal getDespesa(){
		return despesa;
	}

	public void setDespesa(BigDecimal despesa){
		this.despesa = despesa;
	}
	
	public int getQuantidadeDeSegurados() {
		return quantidadeDeSegurados;
	}
	
	public void setQuantidadeDeSegurados(int quantidadeDeSegurados) {
		this.quantidadeDeSegurados = quantidadeDeSegurados;
	}
	
	public String getDescricaoFaixa(){
		
		return faixa.getDescricaoFaixa();
	}

	
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof ResumoFaixa))
			return false;
		ResumoFaixa resumo =  (ResumoFaixa) obj;
		return new EqualsBuilder()
			.append(this.getFaixa().getIdadeMinima(), resumo.getFaixa().getIdadeMinima())
			.append(this.getFaixa().getIdadeMaxima(), resumo.getFaixa().getIdadeMaxima())
			.isEquals();
	}

	public int compareTo(ResumoFaixa resumoFaixa) {
		if(this.getFaixa().getIdadeMinima() < resumoFaixa.getFaixa().getIdadeMinima())
			return -1;
		else if (this.getFaixa().getIdadeMinima() == resumoFaixa.getFaixa().getIdadeMinima())
			return 0;
		else return 1;
	}
	
}
