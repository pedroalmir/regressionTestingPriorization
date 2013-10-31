package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.odonto.enums.DenticaoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.IndiceCPODEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.procedimentos.validators.AbstractEstruturaOdontoValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.EstruturaOdontoAplicabilidadeValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.EstruturaOdontoValidator;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

/**
 * Classe que representa uma estrutura em que um procedimento odontológico é aplicado
 * @author Danilo Nogueira Portela
 */
public class EstruturaOdonto implements Serializable{
 
	private static final long serialVersionUID = 1L;
	private Set<ProcedimentoOdonto> procedimentos;
	private Long idEstrutura;
	private Denticao denticao;
	private Arcada arcada;
	private Quadrante quadrante;
	private Dente dente;
	private Face face; 
	
	private IndiceCPODEnum indiceCPOEnum;
	private Odontograma<EstruturaOdonto> odontograma;
	
	public static Collection<AbstractEstruturaOdontoValidator> estruturaOdontoValidators = new ArrayList<AbstractEstruturaOdontoValidator>();
	static{
		estruturaOdontoValidators.add(new EstruturaOdontoValidator());
		estruturaOdontoValidators.add(new EstruturaOdontoAplicabilidadeValidator());
	}
	
	public EstruturaOdonto(){
		this.procedimentos = new HashSet<ProcedimentoOdonto>();
	}
	
	public Arcada getArcada() {
		return arcada;
	}

	public void setArcada(Arcada arcada) {
		this.arcada = arcada;
	}

	public Dente getDente() {
		return dente;
	}

	public void setDente(Dente dente) {
		this.dente = dente;
	}

	public Denticao getDenticao() {
		return denticao;
	}

	public void setDenticao(Denticao denticao) {
		this.denticao = denticao;
	}

	public Face getFace() {
		return face;
	}

	public void setFace(Face face) {
		this.face = face;
	}

	public Quadrante getQuadrante() {
		return quadrante;
	}

	public void setQuadrante(Quadrante quadrante) {
		this.quadrante = quadrante;
	}
	
	public Boolean isPossuiDenticao() {
		return (this.denticao == null || this.denticao.getDescricao().equals(DenticaoEnum.TODOS.getDescricao())) ? false : true;
	}
	
	public Boolean isPossuiArcada() {
		return (this.arcada == null || this.arcada.getDescricao().equals(DenticaoEnum.TODOS.getDescricao())) ? false : true;
	}
	
	public Boolean isPossuiQuadrante() {
		return (this.quadrante == null || this.quadrante.getDescricao().equals(DenticaoEnum.TODOS.getDescricao())) ? false : true;
	}

	public Boolean isPossuiDente() {
		return (this.dente == null || this.dente.getDescricao().equals(DenticaoEnum.TODOS.getDescricao())) ? false : true;
	}

	public Boolean isPossuiFace() {
		return (this.face == null || this.face.getDescricao().equals(DenticaoEnum.TODOS.getDescricao())) ? false : true; 
	}
	
	public EstruturaOdontoEnum getTipo(){
		if(isPossuiDenticao() && isPossuiArcada() && isPossuiQuadrante() && isPossuiDente() && isPossuiFace())
			return EstruturaOdontoEnum.FACE;
		else if(isPossuiDenticao() && isPossuiArcada() && isPossuiQuadrante() && isPossuiDente() && !isPossuiFace())
			return EstruturaOdontoEnum.DENTE;
		else if(isPossuiDenticao() && isPossuiArcada() && isPossuiQuadrante() && !isPossuiDente() && !isPossuiFace())
			return EstruturaOdontoEnum.QUADRANTE;
		else if(isPossuiDenticao() && isPossuiArcada() && !isPossuiQuadrante() && !isPossuiDente() && !isPossuiFace())
			return EstruturaOdontoEnum.ARCADA;
		else if(isPossuiDenticao() && !isPossuiArcada() && !isPossuiQuadrante() && !isPossuiDente() && !isPossuiFace())
			return EstruturaOdontoEnum.DENTICAO;
		else
			return EstruturaOdontoEnum.NENHUM;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EstruturaOdonto)) 
			return false;
		
		EstruturaOdonto otherObject = (EstruturaOdonto) object;
		return new EqualsBuilder()
			.append(this.getDenticao(), otherObject.getDenticao())
			.append(this.getArcada(), otherObject.getArcada())
			.append(this.getQuadrante(), otherObject.getQuadrante())
			.append(this.getDente(), otherObject.getDente())
			.append(this.getFace(), otherObject.getFace())
			.isEquals();
	}
	
	
	/**
	 * Método que busca todos os procedimentos nas situações informadas em uma estrutura odontológica por beneficiário
	 * @return Set<ProcedimentoOdonto>
	 * @param Collection<String> situacoes
	 */
	public  Set<ProcedimentoOdonto> getProcedimentosPorEstrutura(Collection<String> situacoes){
		AbstractSegurado s = null;
		for (ProcedimentoOdonto po : this.getProcedimentos()){ 
			s = po.getGuia().getSegurado();
			break;
		}
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("segurado",s));
		List<GuiaExameOdonto> guiasExameOdonto = sa.list(GuiaExameOdonto.class);
		Set<ProcedimentoOdonto> ps = new HashSet<ProcedimentoOdonto>();
		
		for (GuiaExameOdonto t : guiasExameOdonto){
			if(situacoes.contains(t.getSituacao().getDescricao()))
				
				for (ProcedimentoOdonto<EstruturaOdonto> p : t.getProcedimentos())
					for (EstruturaOdonto e : p.getEstruturas())
						if(e.equals(this))
							ps.addAll(e.getProcedimentos());
		}

		return ps; 
	}
	
	public Set<ProcedimentoOdonto> getProcedimentosRealizadosPorEstrutura() {
		return getProcedimentosPorEstrutura(GuiaExameOdonto.SITUACOES_TRATAMENTOS_REALIZADOS);
	}
	
	public Set<ProcedimentoOdonto> getProcedimentosNaoAutorizadosPorEstrutura() {
		return getProcedimentosPorEstrutura(GuiaExameOdonto.SITUACOES_TRATAMENTOS_NAO_AUTORIZADOS);
	}
	
	public Set<ProcedimentoOdonto> getProcedimentosSolicitadosPorEstrutura() {
		return getProcedimentosPorEstrutura(GuiaExameOdonto.SITUACOES_TRATAMENTOS_SOLICITADOS);
	}
	
	public Set<ProcedimentoOdonto> getProcedimentosPorEstrutura(){
		Collection<String> situacoes = new HashSet<String>();
		situacoes.addAll(GuiaExameOdonto.SITUACOES_TRATAMENTOS_REALIZADOS);
		situacoes.addAll(GuiaExameOdonto.SITUACOES_TRATAMENTOS_NAO_AUTORIZADOS);
		situacoes.addAll(GuiaExameOdonto.SITUACOES_TRATAMENTOS_SOLICITADOS);
		return getProcedimentosPorEstrutura(situacoes);
	}
	
	public Long getIdEstrutura() {
		return idEstrutura;
	}

	public void setIdEstrutura(Long idEstrutura) {
		this.idEstrutura = idEstrutura;
	}
	
	public Boolean validate() throws Exception {
		for (AbstractEstruturaOdontoValidator validator : estruturaOdontoValidators) 
			if(validator instanceof EstruturaOdontoValidator)
				validator.execute(this, null);
		return true;
	}
	
	public Boolean validate(ProcedimentoOdonto proc) throws Exception {
		for (AbstractEstruturaOdontoValidator validator : estruturaOdontoValidators) 
			validator.execute(this, proc);
		
		return true;
	}

	public Set<ProcedimentoOdonto> getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(Set<ProcedimentoOdonto> procedimentos) {
		this.procedimentos = procedimentos;
	}

	public String getDescricao() {
		if(getTipo().equals(EstruturaOdontoEnum.DENTICAO))
			return "Dent. " + this.getDenticao().getTipo();
		
		if(getTipo().equals(EstruturaOdontoEnum.ARCADA))
			return "Arc. " + this.getArcada().getPosicao().substring(0,3) + ". " + this.getDenticao().getTipo();
		
		if(getTipo().equals(EstruturaOdontoEnum.QUADRANTE))
			return "Hemiarcada " + this.getQuadrante().getNumero();
		
		if(getTipo().equals(EstruturaOdontoEnum.DENTE))
			return this.getDente().getDescricao();
		
		if(getTipo().equals(EstruturaOdontoEnum.FACE))
			return this.getDente().getNumero() + " - " + this.getFace().getTipoFace().getTipo();
		
		return null;
	}
	
	public void tocarObjetos(){
		
		if(this.getDenticao() != null)
			this.getDenticao().getDescricao();
		
		if(this.getArcada() != null)
			this.getArcada().getDescricao();
		
		if(this.getQuadrante() != null)
			this.getQuadrante().getDescricao();
		
		if(this.getDente() != null)
			this.getDente().getDescricao();
		
		if(this.getFace() != null)
			this.getFace().getDescricao();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(this.getDenticao()).
			append(this.getArcada()).
			append(this.getQuadrante()).
			append(this.getDente()).
			append(this.getFace()).
			toHashCode();
	}
	
	public Boolean isForRestauracao(){
		return false;
	}

	public IndiceCPODEnum getIndiceCPODEnum() {
		return indiceCPOEnum;
	}

	public void setIndiceCPODEnum(IndiceCPODEnum indice) {
		this.indiceCPOEnum = indice;
	}
	
	public String getIndiceCPOD() {
		if(indiceCPOEnum != null){
			return indiceCPOEnum.getDescricao();
		}
		return IndiceCPODEnum.DEFAULT.getDescricao();
	}
	
	public void setIndiceCPOD(String descricao) {
		this.indiceCPOEnum = IndiceCPODEnum.getIndiceByDescricao(descricao);
	}

	public Odontograma<EstruturaOdonto> getOdontograma() {
		return odontograma;
	}

	public void setOdontograma(Odontograma odontograma) {
		this.odontograma = odontograma;
	}
	
}
