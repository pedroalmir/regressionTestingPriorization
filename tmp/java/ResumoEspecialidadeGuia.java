package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;

/**
 * Resumo que apresenta as consultas eletivas e de urg�ncia de uma determinada especialidade.
 * @author benedito
 *
 */
public class ResumoEspecialidadeGuia {

	/**
	 * Descri��o da especialidade.
	 * Ex.: Cardiologia.
	 */
	private String especialidadeDescricao;
	
	/**
	 * Quantidade de consultas eletivas da especialidade.
	 */
	private int qtdeConsultasEletivas;
	
	/**
	 * Valor das consultas eletivas da especialidade.
	 */
	private BigDecimal vlrConsultasEletivas;
	
	/**
	 * Quantidade de consultas de urg�ncia da especialidade.
	 */
	private int qtdeConsultasUrgencia;
	
	/**
	 * Valor das consultas de urg�ncia da especialidade. 
	 */
	private BigDecimal vlrConsultasUrgencia;
	
	/**
	 * Quantidade de consultas eletivas e de urg�ncia da especialidade.
	 */
	private int qtdeTotalConsultas;
	
	/**
	 * Valor das consultas eletivas e de urg�ncia da especialidade.
	 */
	private BigDecimal vlrTotalConsultas;

	public ResumoEspecialidadeGuia() {
		this.vlrConsultasEletivas = BigDecimal.ZERO;
		this.vlrConsultasUrgencia = BigDecimal.ZERO;
		this.vlrTotalConsultas = BigDecimal.ZERO;
	}

	public ResumoEspecialidadeGuia(String especialidadeDescricao, int qtdeConsultasEletivas, BigDecimal vlrConsultasEletivas, int qtdeConsultasUrgencia, BigDecimal vlrConsultasUrgencia) {
		this.especialidadeDescricao = especialidadeDescricao;
		
		this.qtdeConsultasEletivas = qtdeConsultasEletivas;
		this.vlrConsultasEletivas = vlrConsultasEletivas;
		
		this.qtdeConsultasUrgencia = qtdeConsultasUrgencia;
		this.vlrConsultasUrgencia = vlrConsultasUrgencia;
		
		this.qtdeTotalConsultas = this.qtdeConsultasEletivas + this.qtdeConsultasUrgencia;
		this.vlrTotalConsultas = this.vlrConsultasEletivas.add(this.vlrConsultasUrgencia);
	}

	public String getEspecialidadeDescricao() {
		return especialidadeDescricao.toUpperCase();
	}

	public void setEspecialidadeDescricao(String especialidadeDescricao) {
		this.especialidadeDescricao = especialidadeDescricao;
	}

	public int getQtdeConsultasEletivas() {
		return qtdeConsultasEletivas;
	}

	public void setQtdeConsultasEletivas(int qtdeConsultasEletivas) {
		this.qtdeConsultasEletivas = qtdeConsultasEletivas;
	}

	public BigDecimal getVlrConsultasEletivas() {
		return vlrConsultasEletivas;
	}

	public void setVlrConsultasEletivas(BigDecimal vlrConsultasEletivas) {
		this.vlrConsultasEletivas = vlrConsultasEletivas;
	}

	public int getQtdeConsultasUrgencia() {
		return qtdeConsultasUrgencia;
	}

	public void setQtdeConsultasUrgencia(int qtdeConsultasUrgencia) {
		this.qtdeConsultasUrgencia = qtdeConsultasUrgencia;
	}

	public BigDecimal getVlrConsultasUrgencia() {
		return vlrConsultasUrgencia;
	}

	public void setVlrConsultasUrgencia(BigDecimal vlrConsultasUrgencia) {
		this.vlrConsultasUrgencia = vlrConsultasUrgencia;
	}

	public int getQtdeTotalConsultas() {
		return qtdeTotalConsultas;
	}

	public void setQtdeTotalConsultas(int qtdeTotalConsultas) {
		this.qtdeTotalConsultas = qtdeTotalConsultas;
	}

	public BigDecimal getVlrTotalConsultas() {
		return vlrTotalConsultas;
	}

	public void setVlrTotalConsultas(BigDecimal vlrTotalConsultas) {
		this.vlrTotalConsultas = vlrTotalConsultas;
	}

}
