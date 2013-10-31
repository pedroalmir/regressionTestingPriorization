package br.com.infowaypi.ecarebc.procedimentos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.odonto.EstruturaOdontoEF;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdontoEFLayer;
import br.com.infowaypi.msr.user.UsuarioInterface;


/**
 * Classe que representa um procedimento de um tratamento odontológico
 * @author Danilo Nogueira Portela
 */
public class ProcedimentoOdontoRestauracaoLayer implements Serializable {
  
	private static final long serialVersionUID = 1L;
	
	private Long idProcedimentoOD;
	private TabelaCBHPM procedimentoDaTabelaCBHPM;
	
	private Set<EstruturaOdontoEFLayer> estruturas;
	
	private ProcedimentoOdontoRestauracao procedimentoOR;
	
	public ProcedimentoOdontoRestauracaoLayer(){
		this.estruturas = new HashSet<EstruturaOdontoEFLayer>();
		this.procedimentoOR = new ProcedimentoOdontoRestauracao();
	}
	
	public TabelaCBHPM getProcedimentoDaTabelaCBHPM() {
		return procedimentoDaTabelaCBHPM;
	}

	public void setProcedimentoDaTabelaCBHPM(TabelaCBHPM procedimentoDaTabelaCBHPM) {
		this.procedimentoDaTabelaCBHPM = procedimentoDaTabelaCBHPM;
	}

	public Set<EstruturaOdontoEFLayer> getEstruturas() {
		return estruturas;
	}

	public void setEstruturas(Set<EstruturaOdontoEFLayer> estruturas) {
		this.estruturas = estruturas;
	}

	public Long getIdProcedimentoOD() {
		return idProcedimentoOD;
	}

	public void setIdProcedimentoOD(Long idProcedimentoOD) {
		this.idProcedimentoOD = idProcedimentoOD;
	}
	
	public ProcedimentoOdontoRestauracao getProcedimentoOR() {
		return procedimentoOR;
	}

	public void setProcedimentoOR(ProcedimentoOdontoRestauracao procedimentoOR) {
		this.procedimentoOR = procedimentoOR;
	}

	public String getDescricaoEstruturas() {
		String denteEFace = "";
		for (EstruturaOdontoEFLayer estrutura : this.estruturas) {
			denteEFace += estrutura.getDescricaoDenteEFace() + " ";
		}
		return denteEFace;
	}
	
	public Boolean validate(UsuarioInterface usuario) throws Exception {
		this.procedimentoOR.setProcedimentoDaTabelaCBHPM(this.procedimentoDaTabelaCBHPM);
		
		for (EstruturaOdontoEFLayer estruturaLayer : this.estruturas) {
			for (EstruturaOdontoEF estrutura : estruturaLayer.getEstruturasEF()) {
				this.procedimentoOR.addEstrutura(estrutura);
			}
		}
		
		this.procedimentoOR.validate(usuario);
		
		return true;
	}
}