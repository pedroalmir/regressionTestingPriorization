package br.com.infowaypi.ecarebc.procedimentos;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
/**
 * Classe que representa uma doença do código internacional de doenças
 * @author root
 *
 */ 

public class CID implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long idCid;
	private String codigo;
	private String descricaoDaDoenca;
	private String codigoEDescricaoDaDoenca;
	private Set<GuiaCompleta> guias; 
	private Set<TabelaCBHPM> procedimentosTabelaCBHPM;
	private boolean liberadoParaUrgencia;
	public Set<TabelaCBHPM> getProcedimentosTabelaCBHPM() {
		return procedimentosTabelaCBHPM;
	}

	public void setProcedimentosTabelaCBHPM(
			Set<TabelaCBHPM> procedimentosTabelaCBHPM) {
		this.procedimentosTabelaCBHPM = procedimentosTabelaCBHPM;
	}

	public CID(){
		guias = new HashSet<GuiaCompleta>();
		
		this.procedimentosTabelaCBHPM = new HashSet<TabelaCBHPM>();
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricaoDaDoenca() {
		return descricaoDaDoenca;
	}

	public void setDescricaoDaDoenca(String descricaoDaDoenca) {
		this.descricaoDaDoenca = descricaoDaDoenca;
	}

	public String getCodigoEDescricaoDaDoenca() {
		return codigoEDescricaoDaDoenca;
	}

	public void setCodigoEDescricaoDaDoenca(String codigoEDescricaoDaDoenca) {
		this.codigoEDescricaoDaDoenca = codigoEDescricaoDaDoenca;
	}

	public Set<GuiaCompleta> getGuias() {
		return guias;
	}

	public void setGuias(Set<GuiaCompleta> guias) {
		this.guias = guias;
	}

	public Long getIdCid() {
		return idCid;
	}

	public void setIdCid(Long idCid) {
		this.idCid = idCid;
	}
	
	public boolean isLiberadoParaUrgencia() {
		return liberadoParaUrgencia;
	}
	
	public void setLiberadoParaUrgencia(boolean liberadoParaUrgencia) {
		this.liberadoParaUrgencia = liberadoParaUrgencia;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + 7;
		hash = 31 * hash + (null == getCodigo() ? 0 : getCodigo().hashCode());
		hash = 31 * hash + (null == getDescricaoDaDoenca() ? 0 : getDescricaoDaDoenca().hashCode());

		return hash;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CID))
			return false;
		
		CID cid = (CID) obj;
		return new EqualsBuilder()
		.append(this.getCodigo(), cid.getCodigo())
		.append(this.getDescricaoDaDoenca(), cid.getDescricaoDaDoenca())
		.isEquals();
		
	}
	
	public Boolean validate() {
		this.codigoEDescricaoDaDoenca = this.getCodigo()+" - "+this.getDescricaoDaDoenca();
		return true;
	}
	
	public void tocarObjetos() {
		this.getCodigo();
		this.getDescricaoDaDoenca();
	}
}
