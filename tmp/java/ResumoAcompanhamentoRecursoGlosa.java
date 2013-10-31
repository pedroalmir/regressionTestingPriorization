package br.com.infowaypi.ecare.services.recurso.relatorio;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Resumo para o relatório de acompanhamento de GRGs.
 * @author Luciano Rocha
 * @since 23/11/2012
 */
public class ResumoAcompanhamentoRecursoGlosa {

	private List<GuiaRecursoGlosa> guias;
	private List<ResumoGuiaRecursoGlosa> resumos;
	
	public ResumoAcompanhamentoRecursoGlosa() {
		
	}
	
	public ResumoAcompanhamentoRecursoGlosa(List<GuiaRecursoGlosa> guias) {
		this.resumos = new ArrayList<ResumoGuiaRecursoGlosa>();
		
		for (GuiaRecursoGlosa guia : guias) {
			this.resumos.add(new ResumoGuiaRecursoGlosa(guia.getAutorizacao(),
					Utils.format(guia.getDataAtendimento()), guia.getSegurado()
							.getPessoaFisica().getNome(), guia.getSituacao()
							.getDescricao(), Utils.format(guia.getSituacao()
							.getDataSituacao()), guia.getValorTotalFormatado()));
		}
	}
	
	public List<GuiaRecursoGlosa> getGuias() {
		return guias;
	}
	
	public void setGuias(List<GuiaRecursoGlosa> guias) {
		this.guias = guias;
	}
	
	public List<ResumoGuiaRecursoGlosa> getResumos() {
		return resumos;
	}
	
	public void setResumos(List<ResumoGuiaRecursoGlosa> resumos) {
		this.resumos = resumos;
	}
}
