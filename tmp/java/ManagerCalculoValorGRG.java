package br.com.infowaypi.ecare.services.recurso;

import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.utils.GuiaUtils;

public class ManagerCalculoValorGRG {

	private GuiaRecursoGlosa guiaRecursoGlosa;
	
	public ManagerCalculoValorGRG(GuiaRecursoGlosa guiaRecursoGlosa) {
		this.guiaRecursoGlosa = guiaRecursoGlosa;
	}
	
	/**
	 * A oredem das chamadas devem ser respeitadas.
	 */
	public void recalcularValorGRG() {
		somar();
		aplicarMultaAtrazoEntrega();
	}

	private void somar() {
		BigDecimal valorTotalGRG = BigDecimal.ZERO;
		
		if (guiaRecursoGlosa.getSituacao().getDescricao().equals(SituacaoEnum.RECURSADO.descricao())) {
			for (ItemRecursoGlosa item : guiaRecursoGlosa.getItensRecurso()) {
				valorTotalGRG = valorTotalGRG.add(item.getItemGlosavel().getValorItem());
			}
		} else if (guiaRecursoGlosa.getSituacao().getDescricao().equals(SituacaoEnum.DEFERIDO.descricao())) {
			for (ItemRecursoGlosa item : guiaRecursoGlosa.getItensRecurso()) {
				if (item.getSituacao().getDescricao().equals(SituacaoEnum.DEFERIDO.descricao())) {
					valorTotalGRG = valorTotalGRG.add(item.getItemGlosavel().getValorItem());
				}
			}
		}
		guiaRecursoGlosa.setValorTotal(valorTotalGRG);
	}
	
	private void aplicarMultaAtrazoEntrega(){
		guiaRecursoGlosa.setValorTotal(GuiaUtils.aplicarMultaPorAtraso(guiaRecursoGlosa.getValorTotal(),guiaRecursoGlosa.getMultaPorAtrasoDeEntrega()));
	}
}
