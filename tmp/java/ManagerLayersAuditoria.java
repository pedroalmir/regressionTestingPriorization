package br.com.infowaypi.ecarebc.service.autorizacoes;

import java.util.Date;
import java.util.HashSet;

import br.com.infowaypi.ecare.resumos.ResumoGuiasHonorarioMedico;
import br.com.infowaypi.ecare.services.ProcedimentoCirurgicoLayer;
import br.com.infowaypi.ecare.services.layer.ItemDiariaLayer;
import br.com.infowaypi.ecare.services.layer.ItemPacoteLayer;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ItemGasoterapiaLayer;
import br.com.infowaypi.ecarebc.atendimentos.ItemTaxaLayer;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorarioLayer;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoLayer;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutrosLayer;
import br.com.infowaypi.molecular.ImplDAO;


public class ManagerLayersAuditoria {

	private GuiaCompleta<ProcedimentoInterface> guiaCompleta;
	private ResumoGuiasHonorarioMedico resumoGHMs;
	
	public ManagerLayersAuditoria(GuiaSimples<ProcedimentoInterface> guia) {
		if (guia.isCompleta()) {
			guiaCompleta = (GuiaCompleta<ProcedimentoInterface>) guia;
			fillLayersGuiaCompleta();
		}
	}

	public ManagerLayersAuditoria(ResumoGuiasHonorarioMedico resumo) {
		resumoGHMs = resumo;
		fillLayersGHM();
	}

	private void fillLayersGHM() {
		resumoGHMs.setProcedimentosLayer(new HashSet<ProcedimentoHonorarioLayer>());
		
		for (ProcedimentoHonorario procedimento : resumoGHMs.getProcedimentosVisitaAtuais()) {
			boolean cancelado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
			boolean glosado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao());
			boolean negado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao());
			if (!cancelado && !glosado && !negado) {
				ProcedimentoHonorarioLayer procedimentoLayer = new ProcedimentoHonorarioLayer(procedimento);
				resumoGHMs.getProcedimentosLayer().add(procedimentoLayer);
			}
		}
	}
	
	
	/**
	 * Preenche a lista de ItemGasoterapia para edição.
	 */
	private void fillLayerItemGasoterapia() {
		guiaCompleta.setItensGasoterapiaLayer(new HashSet<ItemGasoterapiaLayer>());
		
		for (ItemGasoterapia itemGas : guiaCompleta.getItensGasoterapia()) {
			boolean cancelado = itemGas.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
			boolean glosado = itemGas.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao());
			boolean negado = itemGas.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao());
			if (!cancelado && !glosado && !negado) {
				ItemGasoterapiaLayer itemLayer = new ItemGasoterapiaLayer(itemGas);
				guiaCompleta.getItensGasoterapiaLayer().add(itemLayer);
			}
		}
	}

	/**
	 *  Preenche a lista de ItemTaxa para edição.
	 */
	private void fillLayerItemTaxa() {
		guiaCompleta.setItensTaxaLayer(new HashSet<ItemTaxaLayer>());
		
		for (ItemTaxa itemTaxa : guiaCompleta.getItensTaxa()) {
			boolean cancelado = itemTaxa.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
			boolean glosado = itemTaxa.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao());
			boolean negado = itemTaxa.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao());
			if (!cancelado && !glosado && !negado) {
				ItemTaxaLayer itemLayer = new ItemTaxaLayer(itemTaxa);
				guiaCompleta.getItensTaxaLayer().add(itemLayer);
			}
		}
	}
	
	/**
	 *  Preenche a lista de ItemDiaria para edição.
	 */
	private void fillLayerItemDiaria() {
		guiaCompleta.setItensDiariaAuditoriaLayer(new HashSet<ItemDiariaLayer>());
		
		for (ItemDiaria itemDiaria : guiaCompleta.getItensDiaria()) {
			boolean cancelado = itemDiaria.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
			boolean glosado = itemDiaria.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao());
			boolean negado = itemDiaria.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao());
			if (!cancelado && !glosado && !negado) {
				ItemDiariaLayer itemLayer = new ItemDiariaLayer(itemDiaria);
				guiaCompleta.getItensDiariaAuditoriaLayer().add(itemLayer);
			}
		}
	}

	/**
	 *  Preenche a lista de ItemPacote para edição.
	 */
	private void fillLayerItemPacote() {
		guiaCompleta.setItensPacoteAuditoriaLayer(new HashSet<ItemPacoteLayer>());
		
		for (ItemPacote itemPacote : guiaCompleta.getItensPacote()) {
			
			if (itemPacote.getSituacao() == null || (itemPacote.getSituacao() != null && itemPacote.getSituacao().getDescricao() == null)) {
				itemPacote.mudarSituacao(null, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.INCLUSAO_ITEM.getMessage(), new Date());
			}
			
			boolean cancelado = itemPacote.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
			boolean glosado = itemPacote.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao());
			boolean negado = itemPacote.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao());
			if (!cancelado && !glosado && !negado) {
				ItemPacoteLayer itemLayer = new ItemPacoteLayer(itemPacote);
				guiaCompleta.getItensPacoteAuditoriaLayer().add(itemLayer);
			}
		}
	}

	private void fillLayerProcedimento() {
		guiaCompleta.setProcedimentosCirurgicosLayer(new HashSet<ProcedimentoCirurgicoLayer>());
		
		for (ProcedimentoCirurgico procedimento : guiaCompleta.getProcedimentosCirurgicos()) {
			boolean cancelado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
			boolean glosado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao());
			boolean negado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao());
			if (!cancelado && !glosado && !negado) {
				ProcedimentoCirurgicoLayer itemLayer = new ProcedimentoCirurgicoLayer(procedimento);
				guiaCompleta.getProcedimentosCirurgicosLayer().add(itemLayer);
			}
		}
	}
	
	private void fillLayerProcedimentoExame() {
		guiaCompleta.setProcedimentoLayer(new HashSet<ProcedimentoLayer>());
		
		for (ProcedimentoInterface procedimento : guiaCompleta.getProcedimentosSimplesNaoCanceladosOrdenado()) {
			boolean glosado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao());
			boolean negado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao());
			if (!glosado && !negado) {
				ProcedimentoLayer procedimentoLayer = new ProcedimentoLayer(procedimento);
				guiaCompleta.getProcedimentoLayer().add(procedimentoLayer);
			}
		}
	}
	
	private void fillLayerProcedimentoOutros() {
		guiaCompleta.setProcedimentoOutrosLayer(new HashSet<ProcedimentoOutrosLayer>());
		
		for (ProcedimentoInterface procedimentoOutros : guiaCompleta.getProcedimentosOutrosNaoCanceladosOrdenado()) {
			boolean glosado = procedimentoOutros.getSituacao().getDescricao().equals(SituacaoEnum.GLOSADO.descricao());
			boolean negado = procedimentoOutros.getSituacao().getDescricao().equals(SituacaoEnum.NEGADO.descricao());
			if (!glosado && !negado) {
				ProcedimentoOutrosLayer procedimentoOutrosLayer = new ProcedimentoOutrosLayer(procedimentoOutros);
				guiaCompleta.getProcedimentoOutrosLayer().add(procedimentoOutrosLayer);
			}
		}
	}
	
	private void fillLayersGuiaCompleta() {
		fillLayerProcedimentoOutros();
		fillLayerProcedimentoExame();
		fillLayerProcedimento();
		fillLayerItemDiaria();
		fillLayerItemGasoterapia();
		fillLayerItemPacote();
		fillLayerItemTaxa();
	}
}
