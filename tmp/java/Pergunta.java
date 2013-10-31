package br.com.infowaypi.ecarebc.produto;

import java.io.Serializable;

import br.com.infowaypi.ecare.enums.TipoDePerguntaEnum;

/**
 * Representa uma entidade Pergunta para ser adicionada em Questionario.
 * @author eCare Team
 */
public class Pergunta implements Serializable {

	private static final long serialVersionUID = 1L;

	/** Identificador unico para Pergunta */
	private Long idPergunta;
	
	private Integer ordem;
	
	/** Descricao da pergunta */
	private String descricao;

	private Questionario questionario;
	
	private String tipoDePergunta;
	
	public Pergunta() {
	}
	
	public Pergunta(int ordem, String descricao, TipoDePerguntaEnum tipoDePergunta){
		this.setOrdem(ordem);
		this.setDescricao(descricao);
		this.setTipoDePergunta(tipoDePergunta.getDescricao());
	}
	
	public Long getIdPergunta() {
		return idPergunta;
	}

	public void setIdPergunta(Long idPergunta) {
		this.idPergunta = idPergunta;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public String getTipoDePergunta() {
		return tipoDePergunta;
	}

	public void setTipoDePergunta(String tipoDePergunta) {
		this.tipoDePergunta = tipoDePergunta;
	} 
	
}