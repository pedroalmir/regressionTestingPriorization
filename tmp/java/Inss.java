package br.com.infowaypi.ecarebc.financeiro.faturamento.retencao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings("serial")
public class Inss extends AbstractImposto implements InssInterface{

	private Long idInss;
	private float aliquota;
	private BigDecimal valorDescontoMinimo;
	private BigDecimal valorDescontoMaximo;
	
	public Inss(){
		valorDescontoMaximo = new BigDecimal(0f);
		valorDescontoMinimo = new BigDecimal(0f);
	}
	
	void setIdInss(Long idInss) {
		this.idInss = idInss;
	}
	public Long getIdInss() {
		return idInss;
	}

	public float getAliquota() {
		return aliquota;
	}
	public void setAliquota(float aliquota) {
		this.aliquota = aliquota;
	}

	public BigDecimal getValorDescontoMaximo() {
		return MoneyCalculation.rounded(valorDescontoMaximo);
	}
	public void setValorDescontoMaximo(BigDecimal valorDescontoMaximo) {
		this.valorDescontoMaximo = valorDescontoMaximo;
	}

	public BigDecimal getValorDescontoMinimo() {
		return MoneyCalculation.rounded(valorDescontoMinimo);
	}
	public void setValorDescontoMinimo(BigDecimal valorDescontoMinimo) {
		this.valorDescontoMinimo = valorDescontoMinimo;
	}

	public BigDecimal getValor() {
		BigDecimal multiplicante = MoneyCalculation.divide(new BigDecimal(aliquota), 100f);
		multiplicante.setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal valorCalculado = multiplicante.multiply(this.getValorBaseCalculo());
		if(valorCalculado.compareTo(valorDescontoMinimo) < 0){
			valorCalculado = BigDecimal.ZERO;
		}
		else if(valorCalculado.compareTo(valorDescontoMaximo) > 0){
			valorCalculado = valorDescontoMaximo;
		}
		return MoneyCalculation.rounded(valorCalculado);
	}

	public String getDescricao() {
		return "INSS";
	}

	public BigDecimal getValorDeducao() {
		return BigDecimal.ZERO;
	}

	@Override
	public void setValorBaseCalculo(BigDecimal valorBase) {
		if(valorBase.compareTo(this.valorDescontoMaximo) > 0)
			super.setValorBaseCalculo(this.valorDescontoMaximo);
		else
			super.setValorBaseCalculo(valorBase);
	}
	
	public Boolean validate() throws ValidateException {
		if((this.getIdInss() != null)){
			Inss imposto = ImplDAO.getFromBase(this);
			if(!(Utils.compararCompetencia(imposto.getCompetencia(), this.getCompetencia()) == 0))
				throw new ValidateException("Não é possível alterar a competência de um imposto.");
			
			if(Utils.compararCompetencia(this.getCompetencia(), new Date()) <= 0)
				throw new ValidateException("Não é possível alterar os valores deste imposto.");
			
		}
		else{
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("competencia", this.getCompetencia()));
			List<Inss> impostos = sa.list(Inss.class);
			if(!impostos.isEmpty())
				throw new ValidateException("Já existe um imposto cadastrado para esta competência.");
				
		}
		return super.validate();	
	}
	
	public Inss clone(){
		Inss imposto = new Inss();
		imposto.setValorDescontoMaximo(this.getValorDescontoMaximo());
		imposto.setValorDescontoMinimo(this.getValorDescontoMinimo());
		imposto.setAliquota(this.getAliquota());
		return imposto;
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof Inss)) {
			return false;
		}
		Inss otherObject = (Inss) object;
		return new EqualsBuilder()
				.append(this.getAliquota(), otherObject.getAliquota())
				.append(this.getValorDescontoMaximo(), otherObject.getValorDescontoMaximo())
				.append(this.getValorDescontoMinimo(), otherObject.getValorDescontoMinimo())
				.isEquals();
	}
	
}
