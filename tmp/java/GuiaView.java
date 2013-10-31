package br.com.infowaypi.servlets;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;

public class GuiaView {

	private String autorizacao;
	private String tipoDeGuia;
	private String numeroCartaoSegurado;
	
	public GuiaView(GuiaSimples guia) {
		this.autorizacao = guia.getAutorizacao();
		this.tipoDeGuia = guia.getTipoDeGuia();
		this.numeroCartaoSegurado = guia.getSegurado().getNumeroDoCartao();
	}

	public String getAutorizacao() {
		return autorizacao;
	}

	public void setAutorizacao(String autorizacao) {
		this.autorizacao = autorizacao;
	}

	public String getTipoDeGuia() {
		return tipoDeGuia;
	}

	public void setTipoDeGuia(String tipoDeGuia) {
		this.tipoDeGuia = tipoDeGuia;
	}

	public String getNumeroCartaoSegurado() {
		return numeroCartaoSegurado;
	}

	public void setNumeroCartaoSegurado(String numeroCartaoSegurado) {
		this.numeroCartaoSegurado = numeroCartaoSegurado;
	}
	
}
