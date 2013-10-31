package br.com.infowaypi.ecarebc.atendimentos.acordos;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecarebc.atendimentos.Gasoterapia;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class AcordoGasoterapia extends AbstractAcordo{

	private Gasoterapia gasoterapia;
	private BigDecimal valor;
	
	public AcordoGasoterapia() {
		super();
	}
	
	public AcordoGasoterapia(UsuarioInterface usuario) {
		super(usuario);
	}
	
	public Gasoterapia getGasoterapia() {
		return gasoterapia;
	}
	
	public String getDescricaoGasoterapia() {
		return gasoterapia.getDescricao();
	}
	
	public void setGasoterapia(Gasoterapia gasoterapia) {
		this.gasoterapia = gasoterapia;
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
			Long quant = (Long) HibernateUtil.currentSession().createCriteria(AcordoGasoterapia.class)
			.add(Expression.eq("gasoterapia", this.getGasoterapia()))
			.add(Expression.eq("prestador", this.getPrestador()))
			.setProjection(Projections.rowCount())
			.uniqueResult();
			
			if(quant > 0)
				throw new ValidateException("Já existe um acordo com esta Gasoterapia.");
		}
		this.getPrestador().getAcordosGasoterapia().add(this);
		return true;	
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AcordoGasoterapia))
			return false;
		
		AcordoGasoterapia acordoGasoterapia = (AcordoGasoterapia) obj;
		return new EqualsBuilder()
		.append(this.getPrestador(), acordoGasoterapia.getPrestador())
		.append(this.getGasoterapia(), acordoGasoterapia.getGasoterapia())
		.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getPrestador())
			.append(this.getGasoterapia())
			.toHashCode();
	}

	public void tocarObjetos() {
		this.getGasoterapia().getValor();
		this.getValor();
	}	

	@Override
	public String toString() {
		return new StringBuilder()
			.append(this.getPrestador().getPessoaJuridica().getFantasia())
			.append(": ")
			.append(this.getGasoterapia().getDescricao())
			.toString();
	}
	
}
