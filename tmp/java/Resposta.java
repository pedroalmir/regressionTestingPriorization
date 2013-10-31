package br.com.infowaypi.ecare.questionarioqualificado;

import java.io.Serializable;

public class Resposta implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long idResposta;
	private Pergunta pergunta;
	private boolean assertiva;
	private String observacoes;

	public Long getIdResposta() {
		return idResposta;
	}

	public void setIdResposta(Long idResposta) {
		this.idResposta = idResposta;
	}

	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	public boolean isAssertiva() {
		return assertiva;
	}

	public void setAssertiva(boolean assertiva) {
		this.assertiva = assertiva;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

}
