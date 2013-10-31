package br.com.infowaypi.ecarebc.atendimentos;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

public class ResumoGuiasReguladas {

	private List<GuiaCompleta> guiasAutorizadas;
	private List<GuiaCompleta> guiasNaoAutorizadas;
	
	public ResumoGuiasReguladas(List<GuiaCompleta<ProcedimentoInterface>> list) {
		
		guiasAutorizadas = new ArrayList<GuiaCompleta>();
		guiasNaoAutorizadas = new ArrayList<GuiaCompleta>();
		
		for (GuiaCompleta<ProcedimentoInterface> guia : list) {
			if (guia.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())) {
				guiasAutorizadas.add(guia);
			} else {
				guiasNaoAutorizadas.add(guia);
			}
		}
	}

	public List<GuiaCompleta> getGuiasAutorizadas() {
		return guiasAutorizadas;
	}

	public void setGuiasAutorizadas(List<GuiaCompleta> guiasAutorizadas) {
		this.guiasAutorizadas = guiasAutorizadas;
	}

	public List<GuiaCompleta> getGuiasNaoAutorizadas() {
		return guiasNaoAutorizadas;
	}

	public void setGuiasNaoAutorizadas(List<GuiaCompleta> guiasNaoAutorizadas) {
		this.guiasNaoAutorizadas = guiasNaoAutorizadas;
	}

}
