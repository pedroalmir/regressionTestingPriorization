package br.com.infowaypi.ecarebc.atendimentos.acordos;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecarebc.atendimentos.Diaria;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class AcordoDiaria extends AbstractAcordo{

	private static final long serialVersionUID = 1L;
	
	private Diaria diaria;
	private BigDecimal valor;
	
	public AcordoDiaria() {
		super();
	}
	
	public AcordoDiaria(UsuarioInterface usuario) {
		super(usuario);
	}

	public Diaria getDiaria() {
		return diaria;
	}
	
	public void setDiaria(Diaria diaria) {
		this.diaria = diaria;
	}
	
	public String getDescricaoDiaria() {
		return this.diaria.getDescricao();
	}
	
	public BigDecimal getValor() {
		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	public void tocarObjetos(){
		this.getDiaria().getValor();
		this.getValor();
	}
	
	public Boolean validate() throws ValidateException {
		
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		if(this.getIdAcordo() == null){
			Long quant = (Long) HibernateUtil.currentSession().createCriteria(AcordoDiaria.class)
			.add(Expression.eq("diaria", this.getDiaria()))
			.add(Expression.eq("prestador", this.getPrestador()))
			.setProjection(Projections.rowCount())
			.uniqueResult();
			
			if(quant > 0)
				throw new ValidateException("Já existe um acordo com esta Diária.");
		}
		
		this.getPrestador().getAcordosDiaria().add(this);
		return true;	
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AcordoDiaria))
			return false;
		
		AcordoDiaria acordoDiaria = (AcordoDiaria) obj;
		return new EqualsBuilder()
		.append(this.getPrestador(), acordoDiaria.getPrestador())
		.append(this.getDiaria(), acordoDiaria.getDiaria())
		.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getPrestador())
			.append(this.getDiaria())
			.toHashCode();
	}
	
}
