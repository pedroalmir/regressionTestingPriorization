package br.com.infowaypi.ecare.resumos;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;

public class ResumoGuia extends ResumoGuias<GuiaSimples>{

	private Integer tipoGuia;
	
	public ResumoGuia(List<GuiaSimples> guias, String situacao, boolean filtrarTudo) {
		super(guias, situacao, filtrarTudo);
	}
	
	public Set<GuiaSimples> getGuiasInternacaoEmAndamento() {
		Set<GuiaSimples> guiasInternacao = new HashSet<GuiaSimples>();
		for (GuiaSimples guia: this.getGuias()) {
			if(guia.isInternacao()&& isGuiaEmAndamento(guia)) {
				guiasInternacao.add(guia);
			}
		}
		return guiasInternacao;
	}
	
	public Set<GuiaSimples> getGuiasComNecessidadeDeAuditoria() {
		Set<GuiaSimples> guiasInternacao = new HashSet<GuiaSimples>();
		for (GuiaSimples guia: this.getGuias()) {
			if(guiaPrecisaDeAuditoria(guia)) {
				guiasInternacao.add(guia);
			}
		}
		return guiasInternacao;
	}
	
	
	/**
	 * Método responsável por verificar se uma guia encontra-se em andamento (não finalizada).
	 * @param guia
	 * @return boolean
	 */
	public boolean isGuiaEmAndamento(GuiaSimples guia) {
		if (guia.getSituacao().getDescricao().equals(SITUACAO_CANCELADA) ||
			guia.getSituacao().getDescricao().equals(SITUACAO_AGENDADA) ||
			guia.getSituacao().getDescricao().equals(SITUACAO_FATURADA) ||
			guia.getSituacao().getDescricao().equals(SITUACAO_FECHADA)) {			
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * Método responsável por verificar se uma guia precisa de atenção por parte de um auditor (não finalizada).
	 * @param guia
	 * @return boolean
	 */
	public boolean guiaPrecisaDeAuditoria(GuiaSimples guia) {
		if(guia.getSituacao().getDescricao().equals(SITUACAO_FECHADA) ||
		  (guia.getSituacao().getDescricao().equals(SITUACAO_PRORROGADA)||
		   guia.getSituacao().getDescricao().equals(SituacaoEnum.AUDITADO.descricao()))){
			return true;
		}else {
			return false;
		}
	}
	
	public Integer getTipoGuia() {
		return tipoGuia;
	}
	
	public void setTipoGuia(Integer tipoGuia) {
		this.tipoGuia = tipoGuia;
	}

}
