package br.com.infowaypi.ecare.services.recurso.relatorio;


public class ResumoGuiaRecursoGlosa {
	
	private String autorizacao;
	private String dataAtendimento;
	private String segurado;
	private String situacao;
	private String dataSituacao;
	private String valorTotalFormatado;
	
	public ResumoGuiaRecursoGlosa(String autorizacao, String dataAtendimento,
			String segurado, String situacao, String dataSituacao, String valorTotalFormatado) {
		this.autorizacao = autorizacao;
		this.dataAtendimento = dataAtendimento;
		this.segurado = segurado;
		this.situacao = situacao;
		this.dataSituacao = dataSituacao;
		this.valorTotalFormatado = valorTotalFormatado;
	}
	
	public String getAutorizacao() {
		return autorizacao;
	}
	public void setAutorizacao(String autorizacao) {
		this.autorizacao = autorizacao;
	}
	public String getSegurado() {
		return segurado;
	}
	public void setSegurado(String segurado) {
		this.segurado = segurado;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public String getDataAtendimento() {
		return dataAtendimento;
	}
	public void setDataAtendimento(String dataAtendimento) {
		this.dataAtendimento = dataAtendimento;
	}
	public String getDataSituacao() {
		return dataSituacao;
	}
	public void setDataSituacao(String dataSituacao) {
		this.dataSituacao = dataSituacao;
	}
	public String getValorTotalFormatado() {
		return valorTotalFormatado;
	}
	public void setValorTotalFormatado(String valorTotalFormatado) {
		this.valorTotalFormatado = valorTotalFormatado;
	}
}
