package br.com.infowaypi.ecarebc.atendimentos.visitors;

import java.math.BigDecimal;

import br.com.infowaypi.ecare.services.ProcedimentoCirurgicoLayer;
import br.com.infowaypi.ecare.services.layer.ItemDiariaLayer;
import br.com.infowaypi.ecare.services.layer.ItemPacoteLayer;
import br.com.infowaypi.ecarebc.atendimentos.ItemGasoterapiaLayer;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavelLayer;
import br.com.infowaypi.ecarebc.atendimentos.ItemTaxaLayer;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ComponentValores;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorario;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoHonorarioLayer;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoLayer;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutros;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOutrosLayer;
import br.com.infowaypi.ecarebc.utils.SituacaoUtils;

/**
 * @author SR Team - Marcos Roberto, Eduardo Vera, Silvano Medeiros 12.07.2012
 * No processo do fluxo de "Auditar de Guias", caso seja feita alguma mudança em algum item, 
 * este será glosado e replicado com as novas características com situação Autorizado(a).
 * Este classe é responsável por fazer uma comparação dos atributos alteravéis contidos nos 
 * Itens de Guia e a clonagem destes.
 * @changes Luciano Rocha
 */
public class ItemGlosavelCompararClonarVisitor implements ItemGlosavelVisitor {

	private ItemGlosavelLayer layerReferencia;
	
	public ItemGlosavelCompararClonarVisitor(ItemGlosavelLayer layer) {
		this.layerReferencia = layer;
	}
	
	@Override
	public ItemGlosavel visit(ItemPacote item) {
		ItemPacoteLayer layer = (ItemPacoteLayer) layerReferencia;
		
		boolean isPorcentagemDiferente = !(item.getPorcentagem().equals(layer.getPorcentagem()));
		boolean isPacoteDiferente = !(item.getPacote().getDescricao().equals(layer.getPacote().getDescricao()));

		ItemPacote itemPacote = null;
		if(isPorcentagemDiferente || isPacoteDiferente) {			
			itemPacote = new ItemPacote();
			itemPacote.setItemGlosavelAnterior(item);
			itemPacote.setGuia(item.getGuia());
			itemPacote.setColecaoSituacoes(SituacaoUtils.clone(item.getColecaoSituacoes()));
			itemPacote.setSituacao(item.getSituacao());
			itemPacote.setProfissionalResponsavel(item.getProfissionalResponsavel());
			itemPacote.setProfissionalAuxiliar1(item.getProfissionalAuxiliar1());
			itemPacote.setProfissionalAuxiliar2(item.getProfissionalAuxiliar2());
			itemPacote.setProfissionalAuxiliar3(item.getProfissionalAuxiliar3());
			itemPacote.setAnestesista(item.getAnestesista());
			itemPacote.setItemGlosavelAnterior(item);
			
			/**
			 * Atribuindo os novos valores do item anterior. 
			 */
			itemPacote.setPorcentagem(layer.getPorcentagem());
			itemPacote.setPacote(layer.getPacote());
			
			/**
			 * Justificativa do motivo de glosa.
			 */
			itemPacote.setJustificativaGlosa(layer.getJustificativaGlosa());
		}
		
		return itemPacote;
	}

	@Override
	public ItemGlosavel visit(ItemDiaria item) {
		ItemDiariaLayer layer = (ItemDiariaLayer) layerReferencia;
		boolean isDiariasDiferentes = !(item.getDiaria().getDescricao().equals(layer.getDiaria().getDescricao()));
		boolean isQuantidadeDiferentes = !(item.getValor().getQuantidade() == layer.getValor().getQuantidade());

		ItemDiaria itemDiaria = null;
		if(isDiariasDiferentes || isQuantidadeDiferentes) {			
			itemDiaria = new ItemDiaria();
			itemDiaria.setItemGlosavelAnterior(item);
			itemDiaria.setAutorizado(item.isAutorizado());
			itemDiaria.setAutorizarDiaria(item.getAutorizarDiaria());
			itemDiaria.setColecaoSituacoes(SituacaoUtils.clone(item.getColecaoSituacoes()));
			itemDiaria.setSituacao(item.getSituacao());
			itemDiaria.setDataInicial(item.getDataInicial());
			itemDiaria.setGuia(item.getGuia());
			itemDiaria.setJustificativa(item.getJustificativa());
			itemDiaria.setJustificativaNaoAutorizacao(item.getJustificativaNaoAutorizacao());
			itemDiaria.setComponentValores(item.getComponentValores());
			itemDiaria.setItemGlosavelAnterior(item);
			
			/**
			 * Atribuindo os novos valores do item anterior. 
			 */
			itemDiaria.setDiaria(layer.getDiaria());
			itemDiaria.setValor(layer.getValor());
			itemDiaria.getValor().setValor(layer.getItemDiaria().getValor().getValor());
			itemDiaria.setQuantidadeAutorizada(layer.getValor().getQuantidade());
			itemDiaria.setQuantidadeSolicitada(layer.getValor().getQuantidade());
			
			itemDiaria.setJustificativaGlosa(layer.getJustificativaGlosa());
		}
		
		return itemDiaria;
	}	

	@Override
	public ItemGlosavel visit(ItemGasoterapia item) {
		ItemGasoterapiaLayer layer = (ItemGasoterapiaLayer) layerReferencia;
		ItemGasoterapia itemOriginal = layer.getItemGasoterapia();
		
		boolean isQuantidadeDiferente = !(item.getQuantidadeFormatada().equals(layer.getQuantidadeFormatada()));
		boolean isGasoterapiaDiferente = !(item.getGasoterapia().equals(layer.getGasoterapia()));
		
		ItemGasoterapia itemGasoterapia = null;
		if (isQuantidadeDiferente || isGasoterapiaDiferente) {
			itemGasoterapia = new ItemGasoterapia();
			itemGasoterapia.setItemGlosavelAnterior(item);
			itemGasoterapia.setGuia(itemOriginal.getGuia());
			itemGasoterapia.setColecaoSituacoes(SituacaoUtils.clone(itemOriginal.getColecaoSituacoes()));
			itemGasoterapia.setComponentValores(itemOriginal.getComponentValores());
			itemGasoterapia.setSituacao(itemOriginal.getSituacao());
			
			/**
			 * Atribuindo os novos valores do item anterior. 
			 */
			itemGasoterapia.setGasoterapia(layer.getGasoterapia());
			itemGasoterapia.setHoras(layer.getHoras());
			itemGasoterapia.setMinutos(layer.getMinutos());
			itemGasoterapia.getValor().setValor(BigDecimal.valueOf(layer.getGasoterapia().getValor()));
			
			itemGasoterapia.setJustificativaGlosa(layer.getJustificativaGlosa());
		}
		
		return itemGasoterapia;
	}

	@Override
	public ItemGlosavel visit(ItemTaxa item) {
		ItemTaxaLayer layer = (ItemTaxaLayer) layerReferencia;
		ItemTaxa itemOriginal = layer.getItemTaxa();
		
		boolean isQuantidadeDiferente = item.getValor().getQuantidade() != layer.getValor().getQuantidade();
		boolean isTaxaDiferente = !item.getTaxa().equals(layer.getTaxa());
		
		ItemTaxa itemTaxa = null;
		if (isQuantidadeDiferente || isTaxaDiferente) {
			itemTaxa = new ItemTaxa();
			itemTaxa.setItemGlosavelAnterior(item);
			itemTaxa.setGuia(itemOriginal.getGuia());
			itemTaxa.setColecaoSituacoes(SituacaoUtils.clone(itemOriginal.getColecaoSituacoes()));
			itemTaxa.setComponentValores(itemOriginal.getComponentValores());
			itemTaxa.setSituacao(itemOriginal.getSituacao());
			
			/**
			 * Atribuindo os novos valores do item anterior. 
			 */
			itemTaxa.setTaxa(layer.getTaxa());
			itemTaxa.setValor(layer.getValor());
			itemTaxa.getValor().setValor(BigDecimal.valueOf(layer.getTaxa().getValor()));
			itemTaxa.setJustificativaGlosa(layer.getJustificativaGlosa());
		}
		return itemTaxa;
	}

	@Override
	public ItemGlosavel visit(ProcedimentoCirurgico procedimentoCirurgico) {
		ProcedimentoCirurgicoLayer layer = (ProcedimentoCirurgicoLayer) layerReferencia;
		ProcedimentoCirurgico procedimentoOriginal = layer.getProcedimentoCirurgico();
		
		if (procedimentoCirurgico.getDataRealizacao() == null) {
			if (procedimentoCirurgico.getSituacao(SituacaoEnum.REALIZADO.descricao()) != null) {
				procedimentoCirurgico.setDataRealizacao(procedimentoCirurgico.getSituacao(SituacaoEnum.REALIZADO.descricao()).getDataSituacao());
			}
			if (procedimentoCirurgico.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null) {
				procedimentoCirurgico.setDataRealizacao(procedimentoCirurgico.getSituacao(SituacaoEnum.AUTORIZADO.descricao()).getDataSituacao());
			}
		}
		
		boolean isPorcentagemDiferente = !(procedimentoCirurgico.getPorcentagem().equals(layer.getPorcentagem()));
		boolean isDataRealizacaoDiferente = !(procedimentoCirurgico.getDataRealizacao().equals(layer.getDataRealizacao()));
		
		ProcedimentoCirurgico procedimento = null;
		if (isPorcentagemDiferente || isDataRealizacaoDiferente) {
			procedimento = (ProcedimentoCirurgico) procedimentoOriginal.clone();
			procedimento.setItemGlosavelAnterior(procedimentoCirurgico);
			procedimento.setGuia(procedimentoOriginal.getGuia());
			procedimento.setPorcentagem(layer.getPorcentagem());
			procedimento.setDataRealizacao(layer.getDataRealizacao());
			procedimento.setValorAtualDoProcedimento(procedimento.getValorCalculadoProcedimento());
			procedimento.setJustificativaGlosa(layer.getJustificativaGlosa());
		}
		
		return procedimento;
	}
	
	@Override
	public ItemGlosavel visit(Procedimento procedimento) {
		ProcedimentoLayer layer = (ProcedimentoLayer) layerReferencia;
		
		boolean isProcedimentoDiferente = !(procedimento.getProcedimentoDaTabelaCBHPM().equals(layer.getProcedimentoDaTabelaCBHPM()));
		boolean isQuantidadeDiferente = !(procedimento.getQuantidade().equals(layer.getQuantidadeProcedimentoLayer()));
		boolean isBilateralDiferente = !(procedimento.getBilateral()==layer.getBilateralProcedimentoLayer());
		
		Procedimento procedimentoClone = null;
		if (isProcedimentoDiferente || isQuantidadeDiferente || isBilateralDiferente) {
			procedimentoClone = (Procedimento) procedimento.clone();
			procedimentoClone.setItemGlosavelAnterior(procedimento);
			procedimentoClone.setGuia(procedimento.getGuia());
			procedimentoClone.setProcedimentoDaTabelaCBHPM(layer.getProcedimentoDaTabelaCBHPM());
			procedimentoClone.setQuantidade(layer.getQuantidadeProcedimentoLayer());
			procedimentoClone.setBilateral(layer.getBilateralProcedimentoLayer());
			procedimentoClone.setValorAtualDoProcedimento(BigDecimal.ZERO);
			procedimentoClone.setValorAtualDaModeracao(0f);
			procedimentoClone.setJustificativaGlosa(layer.getJustificativaGlosa());
		}
		
		return procedimentoClone;
	}

	@Override
	public ItemGlosavel visit(ProcedimentoOutros procedimentoOutros) {
		ProcedimentoOutrosLayer layer = (ProcedimentoOutrosLayer) layerReferencia;
		
		if (procedimentoOutros.getDataRealizacao() == null) {
			if (procedimentoOutros.getSituacao(SituacaoEnum.SOLICITADO.descricao()) != null) {
				procedimentoOutros.setDataRealizacao(procedimentoOutros.getSituacao(SituacaoEnum.SOLICITADO.descricao()).getDataSituacao());
			}
			if (procedimentoOutros.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null) {
				procedimentoOutros.setDataRealizacao(procedimentoOutros.getSituacao(SituacaoEnum.AUTORIZADO.descricao()).getDataSituacao());
			}
		}
		
		boolean isProcedimentoDiferente = !(procedimentoOutros.getProcedimentoDaTabelaCBHPM().equals(layer.getProcedimentoOutrosDaTabelaCBHPM()));
		boolean isQuantidadeDiferente = !(procedimentoOutros.getQuantidade().equals(layer.getQuantidadeOutros()));
		boolean isDataRealizacaoDiferente = !(procedimentoOutros.getDataRealizacao().equals(layer.getDataRealizacaoOutros()));
		
		boolean isProfissionalDiferente = false;
		
		if(procedimentoOutros.getProfissionalResponsavel() != null && layer.getProfissionalResponsavelOutros() != null){
			isProfissionalDiferente = !(procedimentoOutros.getProfissionalResponsavel().equals(layer.getProfissionalResponsavelOutros()));
		}else if(!(procedimentoOutros.getProfissionalResponsavel() == null && layer.getProfissionalResponsavelOutros() == null)){
			isProfissionalDiferente = true;
		}
		
		ProcedimentoOutros procedimentoOutrosClone = null;
		if (isProcedimentoDiferente || isQuantidadeDiferente || isDataRealizacaoDiferente || isProfissionalDiferente) {
			procedimentoOutrosClone = (ProcedimentoOutros) procedimentoOutros.clone();
			procedimentoOutrosClone.setItemGlosavelAnterior(procedimentoOutros);
			procedimentoOutrosClone.setGuia(procedimentoOutros.getGuia());
			procedimentoOutrosClone.setProcedimentoDaTabelaCBHPM(layer.getProcedimentoOutrosDaTabelaCBHPM());
			procedimentoOutrosClone.setQuantidade(layer.getQuantidadeOutros());
			procedimentoOutrosClone.setDataRealizacao(layer.getDataRealizacaoOutros());
			procedimentoOutrosClone.setProfissionalResponsavel(layer.getProfissionalResponsavelOutros());
			procedimentoOutrosClone.setValorAtualDoProcedimento(BigDecimal.ZERO);
			procedimentoOutrosClone.setValorAtualDaModeracao(0f);
			procedimentoOutrosClone.setJustificativaGlosa(layer.getJustificativaGlosa());
		}
		return procedimentoOutrosClone;
	}

	@Override
	public ItemGlosavel visit(ProcedimentoHonorario procedimentoHonorario) {
		ProcedimentoHonorarioLayer layer = (ProcedimentoHonorarioLayer) layerReferencia;
		
		boolean isProcedimentoDiferente = !(procedimentoHonorario.getProcedimentoDaTabelaCBHPM().equals(layer.getProcedimentoDaTabelaCBHPM()));
		boolean isProfissionalDiferente = !(procedimentoHonorario.getProfissionalResponsavel().equals(layer.getProfissional()));
		boolean isDataDiferente = !(procedimentoHonorario.getDataRealizacao().equals(layer.getDataRealizacao()));
		boolean isQuantidadeDiferente = !(procedimentoHonorario.getQuantidade().equals(layer.getQuantidade()));
		
		ProcedimentoHonorario procedimentoClone = null;
			
		if (isProcedimentoDiferente || isProfissionalDiferente || isDataDiferente || isQuantidadeDiferente) {
			System.out.println(procedimentoHonorario instanceof ProcedimentoHonorario);
			procedimentoClone = (ProcedimentoHonorario) procedimentoHonorario.clone();
			procedimentoClone.setItemGlosavelAnterior(procedimentoHonorario);
			procedimentoClone.setProcedimentoDaTabelaCBHPM(layer.getProcedimentoDaTabelaCBHPM());
			procedimentoClone.setProfissionalResponsavel(layer.getProfissional());
			procedimentoClone.setGuia(procedimentoHonorario.getGuia());
			procedimentoClone.setDataRealizacao(layer.getDataRealizacao());
			procedimentoClone.setQuantidade(layer.getQuantidade());
			procedimentoClone.setValorAtualDoProcedimento(layer.getProcedimentoDaTabelaCBHPM().getValorModerado());
			procedimentoClone.setBilateral(procedimentoHonorario.getBilateral());
			procedimentoClone.setAuditado(layer.isAuditado());
		}
		
		return procedimentoClone;
	}
}