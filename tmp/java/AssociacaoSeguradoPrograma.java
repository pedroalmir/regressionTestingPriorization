package br.com.infowaypi.ecare.programaPrevencao;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
/**
 * classe que faz a associa��o entre segurado e programa de preven��o
 */
public class AssociacaoSeguradoPrograma extends ImplColecaoSituacoesComponent implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Long idAssociacaoSeguradoPrograma;
	private ProgramaDePrevencao programa;
	private AbstractSegurado segurado;
	private Date dataInsercao;
		
	public AbstractSegurado getSegurado() {
		return segurado;
	}
	
	public Date getDataInsercao() {
		return dataInsercao;
	}

	public void setDataInsercao(Date dataInsercao) {
		this.dataInsercao = dataInsercao;
	}

	public void setSegurado(AbstractSegurado segurado) {
		this.segurado = segurado;
	}
	
	public Long getIdAssociacaoSeguradoPrograma() {
		return idAssociacaoSeguradoPrograma;
	}
	
	public void setIdAssociacaoSeguradoPrograma(Long idAssociacaoSeguradoPrograma) {
		this.idAssociacaoSeguradoPrograma = idAssociacaoSeguradoPrograma;
	}
	
	public ProgramaDePrevencao getPrograma() {
		return programa;
	}
	
	public void setPrograma(ProgramaDePrevencao programa) {
		this.programa = programa;
	}
}
