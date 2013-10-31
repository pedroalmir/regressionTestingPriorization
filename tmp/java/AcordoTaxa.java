package br.com.infowaypi.ecarebc.atendimentos.acordos;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecarebc.atendimentos.Taxa;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class AcordoTaxa extends AbstractAcordo{

	private Taxa taxa;
	private BigDecimal valor; 
	
	public AcordoTaxa() {
		super();
	}
	
	public AcordoTaxa(UsuarioInterface usuario) {
		super(usuario);
	}
	
	public Taxa getTaxa() {
		return taxa;
	}
	
	public String getDescricaoTaxa() {
		return taxa.getDescricao();
	}
	
	public void setTaxa(Taxa taxa) {
		this.taxa = taxa;
	}
	
	public BigDecimal getValor() {
		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	public Boolean validate() throws ValidateException {
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		if(this.getIdAcordo() == null){
			Long quant = (Long) HibernateUtil.currentSession().createCriteria(AcordoTaxa.class)
			.add(Expression.eq("taxa", this.getTaxa()))
			.add(Expression.eq("prestador", this.getPrestador()))
			.setProjection(Projections.rowCount())
			.uniqueResult();
			
			if(quant > 0)
				throw new ValidateException("Já existe um acordo com esta Taxa.");
			
		}
		this.getPrestador().getAcordosTaxa().add(this);
		return true;	
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AcordoTaxa))
			return false;
		
		AcordoTaxa acordoTaxa = (AcordoTaxa) obj;
		return new EqualsBuilder()
		.append(this.getPrestador(), acordoTaxa.getPrestador())
		.append(this.getTaxa(), acordoTaxa.getTaxa())
		.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getPrestador())
			.append(this.getTaxa())
			.toHashCode();
	}
	public void tocarObjetos() {
		this.getTaxa().getValor();
		this.getValor();
	}	
}
