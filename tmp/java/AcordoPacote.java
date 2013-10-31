package br.com.infowaypi.ecarebc.atendimentos.acordos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.FlushMode;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Projections;

import br.com.infowaypi.ecarebc.procedimentos.Pacote;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class AcordoPacote extends AbstractAcordo{
	
	private static final long serialVersionUID = 1L;

	private Pacote pacote;
	
	private Float valor;
	private Boolean incluiTaxa;
	private Boolean incluiGasoterapia;
	private Boolean incluiDiaria;
	private boolean incluiHonorario;
	
	public AcordoPacote() {
		super();
	}
	
	public AcordoPacote(UsuarioInterface usuario) {
		super(usuario);
	}
	
	public Boolean getIncluiDiaria() {
		return incluiDiaria;
	}
	public void setIncluiDiaria(Boolean incluiDiaria) {
		this.incluiDiaria = incluiDiaria;
	}
	public Boolean getIncluiGasoterapia() {
		return incluiGasoterapia;
	}
	public void setIncluiGasoterapia(Boolean incluiGasoterapia) {
		this.incluiGasoterapia = incluiGasoterapia;
	}
	public Boolean getIncluiTaxa() {
		return incluiTaxa;
	}
	public void setIncluiTaxa(Boolean incluiTaxa) {
		this.incluiTaxa = incluiTaxa;
	}
	public Pacote getPacote() {
		return pacote;
	}
	public String getDescricaoPacote() {
		return pacote.getDescricao();
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
	
	public Boolean validate() throws ValidateException {
		
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		if(this.getIdAcordo() == null){
			Long quant = (Long) HibernateUtil.currentSession().createCriteria(AcordoPacote.class)
			.add(Expression.eq("pacote", this.getPacote()))
			.add(Expression.eq("prestador", this.getPrestador()))
			.setProjection(Projections.rowCount())
			.uniqueResult();
			
			if(quant > 0)
				throw new ValidateException("Já existe um acordo com este Pacote.");
		}
		this.getPrestador().getAcordosPacote().add(this);
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
	public boolean getIncluiHonorario() {
		return incluiHonorario;
	}
	public void setIncluiHonorario(boolean incluiHonorario) {
		this.incluiHonorario = incluiHonorario;
	}
	
}
