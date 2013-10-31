package br.com.infowaypi.ecare.services.recurso.relatorio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.services.recurso.ItemRecursoLayer;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.utils.Utils;

public class ResumoRecursoGlosa {
	
	private List<GuiaCompleta<ProcedimentoInterface>> guias;
	private List<ResumoGuiaRecursoGlosa> resumos;
	
	public ResumoRecursoGlosa() {
	}
	
	public ResumoRecursoGlosa(List<GuiaCompleta<ProcedimentoInterface>> guias) {
		this.resumos = new ArrayList<ResumoGuiaRecursoGlosa>();
		Set<ItemRecursoLayer> iRL;
		
		for (GuiaCompleta<ProcedimentoInterface> guia : guias) {
			iRL = new HashSet<ItemRecursoLayer>();
			guia.fillLayersRecurso();
			if (guia.getLayersRecursoItensDiaria()!= null
					&& guia.getLayersRecursoItensGasoterapia() != null
					&& guia.getLayersRecursoItensPacote() != null
					&& guia.getLayersRecursoItensTaxa() != null
					&& guia.getLayersRecursoProcedimentoCirurgico() != null
					&& guia.getLayersRecursoProcedimentosExame() != null
					&& guia.getLayersRecursoProcedimentosOutros() != null) {
				iRL.addAll(guia.getLayersRecursoItensDiaria());
				iRL.addAll(guia.getLayersRecursoItensGasoterapia());
				iRL.addAll(guia.getLayersRecursoItensPacote());
				iRL.addAll(guia.getLayersRecursoItensTaxa());
				iRL.addAll(guia.getLayersRecursoProcedimentoCirurgico());
				iRL.addAll(guia.getLayersRecursoProcedimentosExame());
				iRL.addAll(guia.getLayersRecursoProcedimentosOutros());
				if (guia.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao()) && guia.getItemRecurso()==null) {
					this.resumos.add(new ResumoGuiaRecursoGlosa(guia.getAutorizacao(), Utils.format(guia.getDataAtendimento()), guia.getSegurado().getPessoaFisica().getNome(), guia.getSituacao().getDescricao(), Utils.format(guia.getSituacao().getDataSituacao()), guia.getValorTotalFormatado()));
				} else if (iRL.size()>0) {
					this.resumos.add(new ResumoGuiaRecursoGlosa(guia.getAutorizacao(), Utils.format(guia.getDataAtendimento()), guia.getSegurado().getPessoaFisica().getNome(), guia.getSituacao().getDescricao(), Utils.format(guia.getSituacao().getDataSituacao()), guia.getValorTotalFormatado()));
				}
			}
		}
	}

	public List<GuiaCompleta<ProcedimentoInterface>> getGuias() {
		return guias;
	}
	public void setGuias(List<GuiaCompleta<ProcedimentoInterface>> guias) {
		this.guias = guias;
	}
	public List<ResumoGuiaRecursoGlosa> getResumos() {
		return resumos;
	}
	public void setResumos(List<ResumoGuiaRecursoGlosa> resumos) {
		this.resumos = resumos;
	}
}
