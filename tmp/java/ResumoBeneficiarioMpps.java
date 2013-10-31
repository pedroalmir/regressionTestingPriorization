package br.com.infowaypi.ecare.programaPrevencao.fluxos;

import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;

/**
 * @author jefferson
 */
public class ResumoBeneficiarioMpps {
	
	private Long idResumoBeneficiarioMpps;
	
	private AbstractSegurado segurado;
	private ResumoGeralMpps resumoSegurado = new ResumoGeralMpps();
	
	public AbstractSegurado getSegurado() {
		return segurado;
	}
	public void setSegurado(AbstractSegurado segurado) {
		this.segurado = segurado;
	}
	public ResumoGeralMpps getResumoSegurado() {
		return resumoSegurado;
	}
	public void setResumoSegurado(ResumoGeralMpps resumoSegurado) {
		this.resumoSegurado = resumoSegurado;
	}
	public Long getIdResumoBeneficiarioMpps() {
		return idResumoBeneficiarioMpps;
	}
	public void setIdResumoBeneficiarioMpps(Long idResumoBeneficiarioMpps) {
		this.idResumoBeneficiarioMpps = idResumoBeneficiarioMpps;
	}
	
	
	
}
