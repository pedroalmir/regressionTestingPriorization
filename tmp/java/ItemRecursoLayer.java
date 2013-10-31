package br.com.infowaypi.ecare.services.recurso;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGasoterapia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;

public class ItemRecursoLayer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ItemGlosavel item;
	private ItemGlosavel itemApresentadoPeloPrestador;
	private String motivoGlosa;
	private String justificativa;
	private boolean recursar;
	
	public ItemRecursoLayer(ItemGlosavel item, ItemGlosavel itemApresentadoPeloPrestador, String motivoGlosa) {
		this.item = item;
		this.itemApresentadoPeloPrestador = itemApresentadoPeloPrestador;
		this.motivoGlosa = motivoGlosa;
	}
	
	public ItemGlosavel getItem() {
		return item;
	}
	public void setItem(ItemGlosavel item) {
		this.item = item;
	}
	public String getJustificativa() {
		return justificativa;
	}
	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}
	public boolean getRecursar() {
		return recursar;
	}

	public boolean isRecursar() {
		return recursar;
	}

	public void setRecursar(boolean recursar) {
		this.recursar = recursar;
	}

	public ItemGlosavel getItemApresentadoPeloPrestador() {
		return itemApresentadoPeloPrestador;
	}

	public void setItemApresentadoPeloPrestador(
			ItemGlosavel itemApresentadoPeloPrestador) {
		this.itemApresentadoPeloPrestador = itemApresentadoPeloPrestador;
	}
	
	public BigDecimal getDiferencaValorTotalGasoterapia() {
		ItemGasoterapia itemGasoterapia = (ItemGasoterapia) this.item;
		ItemGasoterapia itemGasoterapiaApresentado = (ItemGasoterapia) this.itemApresentadoPeloPrestador;
		BigDecimal diferencaValor = new BigDecimal(itemGasoterapiaApresentado.getValorTotal()-itemGasoterapia.getValorTotal());
		diferencaValor = diferencaValor.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal(itemGasoterapia.getValorTotal()) : diferencaValor;
		return diferencaValor;
	}
	
	public BigDecimal getDiferencaValorTotalTaxa() {
		ItemTaxa itemTaxa = (ItemTaxa) this.item;
		ItemTaxa itemTaxaApresentado = (ItemTaxa) this.itemApresentadoPeloPrestador;
		BigDecimal diferencaValor = new BigDecimal(itemTaxaApresentado.getValorTotal()-itemTaxa.getValorTotal());
		diferencaValor = diferencaValor.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal(itemTaxa.getValorTotal()) : diferencaValor;
		return diferencaValor;
	}
	
	public BigDecimal getDiferencaValorTotalDiaria() {
		ItemDiaria itemDiaria = (ItemDiaria) this.item;
		ItemDiaria itemDiariaApresentado = (ItemDiaria) this.itemApresentadoPeloPrestador;
		BigDecimal diferencaValor = new BigDecimal(itemDiariaApresentado.getValorTotal()-itemDiaria.getValorTotal());
		diferencaValor = diferencaValor.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal(itemDiaria.getValorTotal()) : diferencaValor;
		return diferencaValor;
	}
	
	public BigDecimal getDiferencaValorTotalPacote() {
		ItemPacote itemPacote = (ItemPacote) this.item;
		ItemPacote itemPacoteApresentado = (ItemPacote) this.itemApresentadoPeloPrestador;
		BigDecimal diferencaValor = new BigDecimal(itemPacoteApresentado.getValorTotal()-itemPacote.getValorTotal());
		diferencaValor = diferencaValor.compareTo(BigDecimal.ZERO) == 0 ? new BigDecimal(itemPacote.getValorTotal()) : diferencaValor;
		return diferencaValor;
	}
	
	public BigDecimal getDiferencaValorTotalProcedimento() {
		Procedimento procedimento = (Procedimento) this.item;
		Procedimento procedimentoApresentado = (Procedimento) this.itemApresentadoPeloPrestador;
		BigDecimal diferencaValor = procedimentoApresentado.getValorTotal().subtract(procedimento.getValorTotal());
		diferencaValor = diferencaValor.compareTo(BigDecimal.ZERO) == 0 ? procedimento.getValorTotal() : diferencaValor;
		return diferencaValor;
	}

	public String getMotivoGlosa() {
		return motivoGlosa;
	}

	public void setMotivoGlosa(String motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}
		
}
