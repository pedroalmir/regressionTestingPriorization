package br.com.infowaypi.ecarebc.atendimentos.acordos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecarebc.procedimentos.Pacote;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * 
 * @author Dannylvan
 * 
 * Classe para representar um Acordo de Pacote de Honorario
 *
 */
public class AcordoPacoteHonorario extends AbstractAcordo{
	
	private static final long serialVersionUID = 1L;

	private Pacote pacote;
	
	/**
	 * Valor do pacote de honorario
	 */
	private Float valor;
	
	public AcordoPacoteHonorario() {
		super();
	}
	
	public Boolean validate() throws ValidateException {
		
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		if(this.getIdAcordo() == null){
			Long quant = (Long) HibernateUtil.currentSession().createCriteria(AcordoPacoteHonorario.class)
			.add(Expression.eq("pacote", this.getPacote()))
			.add(Expression.eq("prestador", this.getPrestador()))
			.setProjection(Projections.rowCount())
			.uniqueResult();
			
			if(quant > 0)
				throw new ValidateException("Já existe um acordo de Honorario com este Pacote.");
		}
		this.getPrestador().getAcordosPacoteHonorarios().add(this);
		return true;	
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AcordoPacote))
			return false;
		
		AcordoPacote acordoPacote = (AcordoPacote) obj;
		return new EqualsBuilder()
		.append(this.getPacote(), acordoPacote.getPacote())
		.append(this.getPrestador(), acordoPacote.getPrestador())
		.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getPacote())
			.append(this.getPrestador())
			.toHashCode();
	}
	
	public void tocarObjetos() {
		this.getPacote().getValorTotal();
		this.getPacote().getProcedimentosCBHPM().size();
		this.getValor();
	}

	public Pacote getPacote() {
		return pacote;
	}

	public void setPacote(Pacote pacote) {
		this.pacote = pacote;
	}

	public Float getValor() {
		return valor;
	}

	public void setValor(Float valor) {
		this.valor = valor;
	}
	
}
