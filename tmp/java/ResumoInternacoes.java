package br.com.infowaypi.ecare.resumos;

import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
/**
 * @author Marcos Roberto/Marcus Vinicius
 */

public class ResumoInternacoes {

	private List<GuiaCompleta> guiasDeInternacao;
	
	public ResumoInternacoes(List<GuiaCompleta> guias) {
		this.guiasDeInternacao = guias;
	}

	public List<GuiaCompleta> getGuiasDeInternacao() {
		return guiasDeInternacao;
	}

	public void setGuiasDeInternacao(List<GuiaCompleta> guiasDeInternacao) {
		this.guiasDeInternacao = guiasDeInternacao;
	}
}