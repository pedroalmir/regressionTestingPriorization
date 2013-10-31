package br.com.infowaypi.ecare.resumos;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
/**
 * Classe que serve com parâmetro de entrada para métodos.
 * @author jefferson
 */

public class ResumoParametro {
	
	private GuiaSimples<ProcedimentoInterface> guia;
	private Date dataAtendimento;
	private Date dataTerminoAtendimento;
	private Date dataRecebimento;
	private String situacao;
	
	public ResumoParametro(GuiaSimples<ProcedimentoInterface> guia,
			Date dataAtendimento, Date dataTerminoAtendimento,
			Date dataRecebimento, String situacao) {
		this.guia = guia;
		this.dataAtendimento = dataAtendimento;
		this.dataTerminoAtendimento = dataTerminoAtendimento;
		this.dataRecebimento = dataRecebimento;
		this.situacao = situacao;
	}
	
	public GuiaSimples<ProcedimentoInterface> getGuia() {
		return guia;
	}
	public void setGuia(GuiaSimples<ProcedimentoInterface> guia) {
		this.guia = guia;
	}
	public Date getDataAtendimento() {
		return dataAtendimento;
	}
	public void setDataAtendimento(Date dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}
	public Date getDataTerminoAtendimento() {
		return dataTerminoAtendimento;
	}
	public void setDataTerminoAtendimento(Date dataTerminoAtendimento) {
		this.dataTerminoAtendimento = dataTerminoAtendimento;
	}
	public Date getDataRecebimento() {
		return dataRecebimento;
	}
	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

}
