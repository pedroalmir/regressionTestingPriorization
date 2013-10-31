package br.com.infowaypi.ecarebc.procedimentos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecarebc.atendimentos.enums.TipoProcedimentoEnum;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa um procedimento de um tratamento odontológico
 * @author root
 * @changes Danilo Nogueira Portela
 */
public class ProcedimentoOdonto<E extends EstruturaOdonto> extends Procedimento {
 
	private static final long serialVersionUID = 1L;
	private Boolean periciaInicial;
	private Boolean periciaFinal;
	private Set<E> estruturas;
	
	public ProcedimentoOdonto(){
		this(null);
		this.estruturas = new HashSet<E>();
	} 
	
	public ProcedimentoOdonto(UsuarioInterface usuario){
		super(usuario);
		this.estruturas = new HashSet<E>();
		this.periciaInicial = false;
		this.periciaFinal = false;
	}
	
	/**
	 * Método para validação no clique de insert do procedimento odontológico
	 */
	@Override
	public Boolean validate(UsuarioInterface usuario) throws Exception {
		super.validate(usuario);
		for (EstruturaOdonto e : this.getEstruturas()){ 
			e.getProcedimentos().add(this);
			e.validate(this);
		}
		return true;
	}
	
	@Override
	public int getTipoProcedimento() {
		return PROCEDIMENTO_ODONTO;
	}
	
	@Override
	public TipoProcedimentoEnum getTipoProcedimentoEnum() {
		return TipoProcedimentoEnum.PROCEDIMENTO_ODONTO;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object object) {
		super.equals(object);
		if (!(object instanceof ProcedimentoOdonto)) 
			return false;
		
		ProcedimentoOdonto otherObject = (ProcedimentoOdonto) object;
		return new EqualsBuilder()
			.append(this.getIdProcedimento(), otherObject.getIdProcedimento())
			.append(this.getProcedimentoDaTabelaCBHPM(), otherObject.getProcedimentoDaTabelaCBHPM())
			.append(this.getEstruturas(), otherObject.getEstruturas())
			.isEquals();
	}
	
	public String getDescricaoEstruturas(){
		String descricao = "";
		Map<Integer, Set<String>> elementos = new HashMap<Integer, Set<String>>();
		for (EstruturaOdonto e : getEstruturas()){
			Boolean isDenticao = e.getTipo().equals(EstruturaOdontoEnum.DENTICAO);
			Boolean isArcada = e.getTipo().equals(EstruturaOdontoEnum.ARCADA);
			Boolean isQuadrante = e.getTipo().equals(EstruturaOdontoEnum.QUADRANTE);
			Boolean isDente = e.getTipo().equals(EstruturaOdontoEnum.DENTE);
			Boolean isFace = e.getTipo().equals(EstruturaOdontoEnum.FACE);
					
			if(isDenticao || isArcada || isQuadrante)
				return e.getDescricao();
			
			if(isDente || isFace){
				Integer dente = e.getDente().getNumero();
				if(!elementos.containsKey(dente))
					elementos.put(dente, new HashSet<String>());
				
				if(isFace){
					Set<String> faces = elementos.get(dente);
					faces.add(e.getFace().getDescricaoFormatada());
					elementos.put(dente, faces);
				}
			}
		}
		
		for (Integer d : elementos.keySet()) {
			descricao += d + " ";
			for (String f : elementos.get(d)) 
				descricao += f;
			descricao += " ";
		}
		
		return descricao;
	}
	
	public Set<E> getEstruturas() {
		return estruturas;
	}

	public void setEstruturas(Set<E> estruturas) {
		this.estruturas = estruturas;
	}
	
	public void addEstrutura(E estrutura){
		this.getEstruturas().add(estrutura);
	}
	
	public Boolean getRealizarPericia(){
		return null;
	}
	
	public void setRealizarPericia(Boolean pericia){
		//Sem perícia
		if(pericia == null) {
			this.periciaInicial = false;
			this.periciaFinal = false;
		}else{
			//Pericia Inicial 
			if(pericia == true){
				this.periciaInicial = true;
				this.periciaFinal = false;
			}
			
			//Perícia Final 
			if(pericia == false){
				this.periciaInicial = false;
				this.periciaFinal = true;
			}
		}
	}
	
	public Boolean getPericiaInicial() {
		return periciaInicial;
	}

	public void setPericiaInicial(Boolean periciaInicial) {
		this.periciaInicial = periciaInicial;
	}

	public Boolean getPericiaFinal() {
		return periciaFinal;
	}

	public void setPericiaFinal(Boolean periciaFinal) {
		this.periciaFinal = periciaFinal;
	}
	
	public String getDescricaoPericia(){
		String pericia = "";
		pericia = (this.periciaInicial) ? "Inicial" : (this.periciaFinal) ? "Final" : "Não";
		return pericia;
	}
	
	public Boolean isForRestauracao(){
		return false;
	}
	
	@Override
	public void tocarObjetos(){
		super.tocarObjetos();
		this.getDescricaoEstruturas();
		this.getPericiaInicial();
		this.getPericiaFinal();
		if(this.getEstruturas() != null){
			for (EstruturaOdonto e : this.getEstruturas()) {
				e.tocarObjetos();
			}
		}
	}
	
}
