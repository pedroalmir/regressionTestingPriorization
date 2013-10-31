package br.com.infowaypi.ecarebc.atendimentos.acordos;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.FlushMode;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.IsNull;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class AcordoCBHPM extends AbstractAcordo {
	
	private static final long serialVersionUID = 1L;
	
	private TabelaCBHPM tabelaCBHPM;
	private BigDecimal valor;
	private BigDecimal valorUco;
	private Especialidade especialidade;
	private boolean liberarCoParticipacao;

	public AcordoCBHPM(){
		super();
	}
	
	public AcordoCBHPM(UsuarioInterface usuario) {
		super(usuario);
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}
	
	public TabelaCBHPM getTabelaCBHPM() {
		return tabelaCBHPM;
	}
	
	public void setTabelaCBHPM(TabelaCBHPM tabelaCBHPM) {
		this.tabelaCBHPM = tabelaCBHPM;
	}
	
	public String getDescricaoTabelaCBHPM() {
		return tabelaCBHPM.getDescricaoFormatado();
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getValorUco() {
		return valorUco;
	}

	public void setValorUco(BigDecimal valorUco) {
		this.valorUco = valorUco;
	}
	
	public boolean isLiberarCoParticipacao() {
		return liberarCoParticipacao;
	}

	public void setLiberarCoParticipacao(boolean liberarCoParticipacao) {
		this.liberarCoParticipacao = liberarCoParticipacao;
	}
	
	public Boolean validate() throws ValidateException {
		
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		if(especialidade != null && !tabelaCBHPM.getCodigo().startsWith("1010")) {
			throw new ValidateException("Só é permitido informar ESPECIALIDADE para consultas.");
		}
		
		if(this.getIdAcordo() == null){
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("tabelaCBHPM", this.getTabelaCBHPM()));
			if(especialidade == null) {
				sa.addParameter(new IsNull("especialidade"));
			} else {
				sa.addParameter(new Equals("especialidade", this.getEspecialidade()));
			}
			
			sa.addParameter(new Equals("prestador", this.getPrestador()));
			int quant = sa.resultCount(AcordoCBHPM.class);
			
			if(quant > 0) {
				throw new ValidateException("Já existe um acordo com este Procedimento.");
			}
		}	
		this.getPrestador().getAcordosCBHPM().add(this);
		return true;	
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AcordoCBHPM))
			return false;
		
		AcordoCBHPM acordoCBHPM = (AcordoCBHPM) obj;
		return new EqualsBuilder()
			.append(this.getTabelaCBHPM(), acordoCBHPM.getTabelaCBHPM())
			.append(this.getEspecialidade(), acordoCBHPM.getEspecialidade())
			.append(this.getPrestador(), acordoCBHPM.getPrestador())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getTabelaCBHPM())
			.append(this.getEspecialidade())
			.append(this.getPrestador())
			.toHashCode();
	}
	
	public void tocarObjetos(){
		this.getTabelaCBHPM().getValor();
		this.getValor();
	}
	
}
