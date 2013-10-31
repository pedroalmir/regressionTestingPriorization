package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

public class QuadroClinico implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long idQuadroClinico;
	/**
	 * Data na qual o quadro clínico foi registrado.
	 */
	private Date dataJustificativa;
	/**
	 * É a descrição do quadro clínico do paciente.
	 */
	private String justificativa;
	/**
	 * Guia na qual o quadro clínico foi  inserido.
	 */
	private GuiaSimples<ProcedimentoInterface> guia;

	public QuadroClinico() {
		dataJustificativa = new Date();
	}
	public Date getDataJustificativa() {
		return dataJustificativa;
	}
	public void setDataJustificativa(Date dataJustificativa) {
		this.dataJustificativa = dataJustificativa;
	}
	public GuiaSimples getGuia() {
		return guia;
	}
	public void setGuia(GuiaSimples guia) {
		this.guia = guia;
	}
	public Long getIdQuadroClinico() {
		return idQuadroClinico;
	}
	public void setIdQuadroClinico(Long idQuadroClinico) {
		this.idQuadroClinico = idQuadroClinico;
	}
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		QuadroClinico clone = new QuadroClinico();
		clone.setDataJustificativa(dataJustificativa);
		clone.setJustificativa(justificativa);
		return clone;
	}
	
}
