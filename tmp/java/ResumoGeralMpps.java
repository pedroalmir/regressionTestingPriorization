package br.com.infowaypi.ecare.programaPrevencao.fluxos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
/**
 * @author jefferson
 */
public class ResumoGeralMpps implements Serializable {
	
	private Long idResumoGeralMpps;
	private BigDecimal nConsultasRealizadasPeloPrograma;
	private BigDecimal nConsultasDeUrgencia;
	private BigDecimal nInteracoes;
	private BigDecimal nCoparticipacoesLiberadas;
	private BigDecimal nLimitesUltrapassadosDePacientesInscritosEmProgramas;
	
	private Set<ResumoBeneficiarioMpps> resumosBeneficiarios = new HashSet<ResumoBeneficiarioMpps>();
	
	public Long getIdResumoGeralMpps() {
		return idResumoGeralMpps;
	}
	public void setIdResumoGeralMpps(Long idResumoGeralMpps) {
		this.idResumoGeralMpps = idResumoGeralMpps;
	}
	public BigDecimal getnConsultasRealizadasPeloPrograma() {
		return nConsultasRealizadasPeloPrograma;
	}
	public void setnConsultasRealizadasPeloPrograma(
			BigDecimal nConcultasRealizadasPeloPrograma) {
		this.nConsultasRealizadasPeloPrograma = nConcultasRealizadasPeloPrograma;
	}
	public BigDecimal getnConsultasDeUrgencia() {
		return nConsultasDeUrgencia;
	}
	public void setnConsultasDeUrgencia(BigDecimal nConsultasDeUrgencia) {
		this.nConsultasDeUrgencia = nConsultasDeUrgencia;
	}
	public BigDecimal getnInteracoes() {
		return nInteracoes;
	}
	public void setnInteracoes(BigDecimal nInteracoes) {
		this.nInteracoes = nInteracoes;
	}
	public BigDecimal getnCoparticipacoesLiberadas() {
		return nCoparticipacoesLiberadas;
	}
	public void setnCoparticipacoesLiberadas(BigDecimal nCoparticipacoesLiberadas) {
		this.nCoparticipacoesLiberadas = nCoparticipacoesLiberadas;
	}
	public BigDecimal getnLimitesUltrapassadosDePacientesInscritosEmProgramas() {
		return nLimitesUltrapassadosDePacientesInscritosEmProgramas;
	}
	public void setnLimitesUltrapassadosDePacientesInscritosEmProgramas(
			BigDecimal nLimitesUltrapassadosDePacientesInscritosEmProgramas) {
		this.nLimitesUltrapassadosDePacientesInscritosEmProgramas = nLimitesUltrapassadosDePacientesInscritosEmProgramas;
	}
	public Set<ResumoBeneficiarioMpps> getResumosBeneficiarios() {
		return resumosBeneficiarios;
	}
	public void setResumosBeneficiarios(
			Set<ResumoBeneficiarioMpps> resumosBeneficiarios) {
		this.resumosBeneficiarios = resumosBeneficiarios;
	}
	
}
