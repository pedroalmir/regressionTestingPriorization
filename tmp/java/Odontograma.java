package br.com.infowaypi.ecarebc.odonto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.odonto.enums.IndiceCPODEnum;
import br.com.infowaypi.ecarebc.odonto.enums.TipoDenteEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.NotEquals;

/**
 * Classe que representa o odontograma de um segurado do sistema
 * @author Danilo Nogueira Portela
 */
public class Odontograma<E extends EstruturaOdonto> implements Serializable {

	private static final long serialVersionUID = 9213403192333313364L;
	
	private Long idOdontograma;
	private AbstractSegurado beneficiario;
	private Set<E> estruturas;
	
	public Odontograma(){
		this.estruturas = new HashSet<E>();
	}
	
	public Odontograma(AbstractSegurado segurado){
		this.beneficiario = segurado;
		this.estruturas = buscarEstruturas();
	}
	
	public Long getIdOdontograma() {
		return idOdontograma;
	}
	public void setIdOdontograma(Long idOdontograma) {
		this.idOdontograma = idOdontograma;
	}

	public boolean validate(){
		return true;
	}

	public AbstractSegurado getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(AbstractSegurado beneficiario) {
		this.beneficiario = beneficiario;
	}
	
	/**
	 * Cria uma lista com todas as estruturas odontológicas das duas dentições de um segurado
	 */
	public List<E> getDefaultOdontograma(){
		List<E> estruturas = new ArrayList<E>();
		SearchAgent sa = new SearchAgent();
		List<Denticao> denticoes = sa.list(Denticao.class);
		
		for (Denticao denticao : denticoes) {
			sa.clearAllParameters();
			sa.addParameter(new Equals("denticao", denticao));
			List<Arcada> arcadas = sa.list(Arcada.class);
			
			EstruturaOdonto eDt = new EstruturaOdonto();
			eDt = criarEstrutura(eDt, denticao, null, null, null, null);
			estruturas.add((E)eDt);
			
			for (Arcada arcada : arcadas) {
				sa.clearAllParameters();
				sa.addParameter(new Equals("arcada", arcada));
				List<Quadrante> quadrantes = sa.list(Quadrante.class);
				
				EstruturaOdonto eA = new EstruturaOdonto();
				eA = criarEstrutura(eA, denticao, arcada, null, null, null);
				estruturas.add((E)eA);
				
				for (Quadrante quadrante : quadrantes) {
					sa.clearAllParameters();
					sa.addParameter(new Equals("quadrante", quadrante));
					List<Dente> dentes = sa.list(Dente.class);
					
					EstruturaOdonto eQ = new EstruturaOdonto();
					eQ = criarEstrutura(eQ, denticao, arcada, quadrante, null, null);
					estruturas.add((E)eQ);
					
					for (Dente dente : dentes) {
						sa.clearAllParameters();
						sa.addParameter(new Equals("dente", dente));
						List<Face> faces = sa.list(Face.class);
						
						EstruturaOdonto eD = new EstruturaOdonto();
						eD = criarEstrutura(eD, denticao, arcada, quadrante, dente, null);
						estruturas.add((E)eD);
						
						for (Face face : faces) {
							EstruturaOdontoEF eF = new EstruturaOdontoEF();
							eF = criarEstrutura(eF, denticao, arcada, quadrante, dente, face);
							estruturas.add((E)eF);
						}
					}
				}
			}
		}
		
		return estruturas;
	}
	
	/**
	 * Cria uma nova estrutura setando seus componentes
	 * @param <E>
	 * @param e
	 * @param denticao
	 * @param arcada
	 * @param quadrante
	 * @param dente
	 * @param face
	 * @return
	 */
	private <E extends EstruturaOdonto> E criarEstrutura(E e, Denticao denticao, Arcada arcada, Quadrante quadrante, Dente dente, Face face){
		e.setDenticao(denticao);
		e.setArcada(arcada);
		e.setQuadrante(quadrante);
		e.setDente(dente);
		e.setFace(face);
		return e;
	}
	
	/**
	 * Busca as estruturas odontológicas do segurado que já sofreram a aplicação de algum procedimento odontológico
	 */
	public Set<E> buscarEstruturas(){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("segurado",this.getBeneficiario()));
		List<GuiaExameOdonto> tratamentos = sa.list(GuiaExameOdonto.class);
		
		Set<E> estruturas = new HashSet<E>();
		Set<String> situacoes = new HashSet<String>();
		situacoes.addAll(GuiaExameOdonto.SITUACOES_TRATAMENTOS_NAO_AUTORIZADOS);
		situacoes.addAll(GuiaExameOdonto.SITUACOES_TRATAMENTOS_REALIZADOS);
		situacoes.addAll(GuiaExameOdonto.SITUACOES_TRATAMENTOS_SOLICITADOS);
		for (GuiaExameOdonto t : tratamentos){ 
			if(situacoes.contains(t.getSituacao().getDescricao())){
				for (ProcedimentoOdonto<E> p : t.getProcedimentos()){
					for (E e : p.getEstruturas()){
						e.setProcedimentos(e.getProcedimentosPorEstrutura());
						estruturas.add(e);
					}
				}
			}
		}	
		return estruturas; 
	}
	
	/**
	 * Povoa o odontograma com todas as estruturas odonto.
	 */
	public void construirOdontograma(){
		List<E> estDef = getDefaultOdontograma();
		Set<E> est = buscarEstruturas();

		for (E eProc : est) {
			if(estDef.contains(eProc)){
				Integer index = estDef.indexOf(eProc);
				estDef.set(index, eProc);
			}
		}
		
		Set<E> ests = new HashSet<E>();
		ests.addAll(estDef);
		
		this.setEstruturas(ests);
	}
	
	/**
	 * Povoa o odontograma com todas as estruturas dentárias.
	 */
	public void construirOdontogramaDente() {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new NotEquals("tipoDente.tipo", TipoDenteEnum.TODOS.getDescricao()));
		
		List<Dente> dentes = sa.list(Dente.class);
		for (Dente dente : dentes) {
			EstruturaOdonto eOdonto = new EstruturaOdonto();
			criarEstruturaOdonto(eOdonto, dente);
			eOdonto.setOdontograma(this);
			this.getEstruturas().add((E)eOdonto);
		}
	}
	
	private void criarEstruturaOdonto(EstruturaOdonto estruturaOdonto, Dente dente) {
		
		Quadrante quadrante = dente.getQuadrante();
		Arcada arcada = quadrante.getArcada();
		Denticao denticao = arcada.getDenticao();
		
		estruturaOdonto.setDenticao(denticao);
		estruturaOdonto.setArcada(arcada);
		estruturaOdonto.setQuadrante(quadrante);
		estruturaOdonto.setDente(dente);
		estruturaOdonto.setIndiceCPODEnum(IndiceCPODEnum.HIGIDO);
	}
	
	public Set<E> getEstruturas() {
		return estruturas;
	}

	public void setEstruturas(Set<E> estruturas) {
		this.estruturas = estruturas;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Odontograma)) {
			return false;
		}
		Odontograma odontogama = (Odontograma) object;
		return new EqualsBuilder()
			.append(this.getBeneficiario(), odontogama.getBeneficiario())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getBeneficiario()).toHashCode();
	}
	
}
