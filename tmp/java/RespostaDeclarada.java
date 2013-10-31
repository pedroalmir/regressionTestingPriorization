package br.com.infowaypi.ecarebc.produto;

import java.io.Serializable;
import java.util.Set;

public class RespostaDeclarada implements Serializable, Comparable<RespostaDeclarada>{

	private static final long serialVersionUID = 1L;

	private Long idRespostaDeclarada;

	/** Valor da resposta */
	private Boolean valor;

	/** Observacoes sobre a pergunta */
	private String observacoes;

	/** Pergunta a qual a resposta esta relacionada */
	private Pergunta pergunta;

	private DeclaracaoSaude declaracaoSaude;
	
	public Long getIdRespostaDeclarada() {
		return idRespostaDeclarada;
	}

	public void setIdRespostaDeclarada(Long idRespostaDeclarada) {
		this.idRespostaDeclarada = idRespostaDeclarada;
	}

	public Boolean getValor() {
		return valor;
	}

	public void setValor(Boolean valor) {
		this.valor = valor;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Pergunta getPergunta() {
		return pergunta;
	}

	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	public DeclaracaoSaude getDeclaracaoSaude() {
		return declaracaoSaude;
	}

	public void setDeclaracaoSaude(DeclaracaoSaude declaracaoSaude) {
		this.declaracaoSaude = declaracaoSaude;
	}

	@Override
	public int compareTo(RespostaDeclarada r) {
		if(this.getPergunta().getOrdem() > r.getPergunta().getOrdem()) {
			return 1;
		}else if(this.getPergunta().getOrdem() < r.getPergunta().getOrdem()){
			return -1;
		}
		return 0;
	}

	public Set<Pergunta> getPerguntasQuestionario(){
		return declaracaoSaude.getQuestionario().getPerguntas();
	}
	
}
