package br.com.infowaypi.ecarebc.atendimentos;

import java.io.Serializable;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * @author eduardo
 */
public class MotivoDevolucaoDeLote implements Serializable {

	private Long idMotivo;
	private String descricao;
	
	
	public Long getIdMotivo() {
		return idMotivo;
	}
	public void setIdMotivo(Long idMotivo) {
		this.idMotivo = idMotivo;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Boolean validate() throws ValidateException {
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("descricao", this.descricao));
		
		boolean possuiDescricaoIgual = sa.resultCount(MotivoDevolucaoDeLote.class) > 0;
		
		if (possuiDescricaoIgual) 
			throw new ValidateException(MensagemErroEnum.MOTIVO_DEVOLUCAO_JA_CADASTRADO.getMessage());
		
		return Boolean.TRUE;
	}
	
	@Override
	public String toString() {
		return descricao;
	}
}
