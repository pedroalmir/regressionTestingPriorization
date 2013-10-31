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
public class Iss extends AbstractImposto implements IssInterface{

	private Long idIss;
	private float aliquota;
	private Integer tipoDePessoa;
	
	public Iss(){}
	
	void setIdIss(Long idIss) {
		this.idIss = idIss;
	}
	public Long getIdIss() {
		return idIss;
	}

	public float getAliquota() {
		return aliquota;
	}
	public void setAliquota(float aliquota) {
		this.aliquota = aliquota;
	}

	public Integer getTipoDePessoa() {
		return tipoDePessoa;
	}
	public void setTipoDePessoa(Integer tipoDePessoa) {
		this.tipoDePessoa = tipoDePessoa;
	}

	public BigDecimal getValor() {
		BigDecimal multiplicante = MoneyCalculation.divide(new BigDecimal(aliquota), 100f);
		multiplicante.setScale(2, BigDecimal.ROUND_HALF_UP);
		return MoneyCalculation.rounded(multiplicante.multiply(this.getValorBaseCalculo())); 
	}

	public String getDescricao() {
		return "ISS";
	}

	public BigDecimal getValorDeducao() {
		return BigDecimal.ZERO;
	}
	
	public Boolean validate() throws ValidateException {
		if((this.getIdIss() != null)){
			Iss imposto = ImplDAO.getFromBase(this);
			if(!(Utils.compararCompetencia(imposto.getCompetencia(), this.getCompetencia()) == 0))
				throw new ValidateException("Não é possível alterar a competência de um imposto.");
			
			if(Utils.compararCompetencia(this.getCompetencia(), new Date()) <= 0)
				throw new ValidateException("Não é possível alterar os valores deste imposto.");
			
		}
		else{
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("competencia", this.getCompetencia()));
			sa.addParameter(new Equals("tipoDePessoa", this.getTipoDePessoa()));
			List<Iss> impostos = sa.list(Iss.class);
			if(!impostos.isEmpty())
				throw new ValidateException("Já existe um imposto cadastrado para esta competência e para o tipo de pessoa informado.");
		}
		return super.validate();	
	}

	public Iss clone(){
		Iss imposto = new Iss();
		imposto.setAliquota(this.getAliquota());
		imposto.setTipoDePessoa(this.getTipoDePessoa());
		return imposto;
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof Iss)) {
			return false;
		}
		Iss otherObject = (Iss) object;
		return new EqualsBuilder()
				.append(this.getAliquota(), otherObject.getAliquota())
				.append(this.getTipoDePessoa(), otherObject.getTipoDePessoa())
				.isEquals();
	}
	
}
