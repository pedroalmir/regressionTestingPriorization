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
public class ImpostoDeRenda extends AbstractImposto implements ImpostoDeRendaInterface {

	private Long idImpostoDeRenda;
	private BigDecimal valorFaixaDe;
	private BigDecimal valorFaixaAte;
	private float aliquota;
	private BigDecimal valorDeducao;
	private Integer tipoDePessoa;
	
	public ImpostoDeRenda(){
		valorFaixaDe = new BigDecimal(0f);
		valorFaixaAte = new BigDecimal(0f);
		valorDeducao = new BigDecimal(0f);
	}
		
	void setIdImpostoDeRenda(Long idImpostoDeRenda) {
		this.idImpostoDeRenda = idImpostoDeRenda;
	}
	public Long getIdImpostoDeRenda() {
		return idImpostoDeRenda;
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

	public BigDecimal getValorDeducao() {
		return MoneyCalculation.rounded(valorDeducao);
	}
	public void setValorDeducao(BigDecimal valorDeducao) {
		this.valorDeducao = valorDeducao;
	}

	public BigDecimal getValorFaixaAte() {
		return MoneyCalculation.rounded(valorFaixaAte);
	}
	public void setValorFaixaAte(BigDecimal valorFaixaAte) {
		this.valorFaixaAte = valorFaixaAte;
	}

	public BigDecimal getValorFaixaDe() {
		return MoneyCalculation.rounded(valorFaixaDe);
	}
	public void setValorFaixaDe(BigDecimal valorFaixaDe) {
		this.valorFaixaDe = valorFaixaDe;
	}

	public BigDecimal getValor() {
		BigDecimal valorCalculado = this.getValorBaseCalculo().multiply(new BigDecimal(aliquota));
		valorCalculado = MoneyCalculation.divide(valorCalculado, 100f);
		return MoneyCalculation.rounded(valorCalculado.subtract(valorDeducao)); 
	}

	public String getDescricao() {
		return "IR";
	}
	
	public Boolean validate() throws ValidateException {
		if((this.getIdImpostoDeRenda() != null)){
			ImpostoDeRenda imposto = ImplDAO.getFromBase(this);
			if(!(Utils.compararCompetencia(imposto.getCompetencia(), this.getCompetencia()) == 0))
				throw new ValidateException("Não é possível alterar a competência de um imposto.");
			
			if(Utils.compararCompetencia(this.getCompetencia(), new Date()) <= 0)
				throw new ValidateException("Não é possível alterar os valores deste imposto.");
			
		}
		else{
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("competencia", this.getCompetencia()));
			sa.addParameter(new Equals("tipoDePessoa", this.getTipoDePessoa()));
			List<ImpostoDeRenda> impostos = sa.list(ImpostoDeRenda.class);
			if(this.getTipoDePessoa().equals(AbstractImposto.PESSOA_JURIDICA)){
				if(!impostos.isEmpty())
					throw new ValidateException("Já existe um imposto cadastrado para esta competência para Pessoas Jurídicas.");
			}
			else if(this.getTipoDePessoa().equals(AbstractImposto.PESSOA_FISICA)){
				if(impostos.size() > 2)
					throw new ValidateException("Já existem impostos cadastrados para esta competência para Pessoas Físicas.");
			}
		}		
		return super.validate();	
	}

	public ImpostoDeRenda clone(){
		ImpostoDeRenda imposto = new ImpostoDeRenda();
		imposto.setValorFaixaDe(this.getValorFaixaDe());
		imposto.setValorFaixaAte(this.getValorFaixaAte());
		imposto.setValorDeducao(this.getValorDeducao());
		imposto.setTipoDePessoa(this.getTipoDePessoa());
		imposto.setAliquota(this.getAliquota());
		return imposto;
	}
	
	public boolean equals(Object object) {
		if (!(object instanceof ImpostoDeRenda)) {
			return false;
		}
		ImpostoDeRenda otherObject = (ImpostoDeRenda) object;
		return new EqualsBuilder()
				.append(this.getAliquota(), otherObject.getAliquota())
				.append(this.getValorFaixaDe(), otherObject.getValorFaixaDe())
				.append(this.getValorFaixaAte(), otherObject.getValorFaixaAte())
				.append(this.getValorDeducao(), otherObject.getValorDeducao())
				.append(this.getTipoDePessoa(), otherObject.getTipoDePessoa())
				.isEquals();
	}
	
}
