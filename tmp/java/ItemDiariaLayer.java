package br.com.infowaypi.ecare.services.layer;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.Diaria;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavelLayer;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.Valor;

/** @author SR Team - Marcos Roberto 21.05.2012 */
public class ItemDiariaLayer implements ItemGlosavelLayer {

	private Diaria diaria;
	private MotivoGlosa motivoGlosa;
	private boolean glosar;
	private Valor valor;
	private String justificativaGlosa;
	
	private ItemDiaria itemDiaria;
	
	public ItemDiariaLayer(ItemDiaria item) {
		this.diaria = item.getDiaria();
		this.itemDiaria = item;
		this.valor = clonaValor(item.getValor());
	}
	
	public Diaria getDiaria() {
		return diaria;
	}

	public void setDiaria(Diaria diaria) {
		this.diaria = diaria;
	}

	public MotivoGlosa getMotivoGlosa() {
		return motivoGlosa;
	}

	public void setMotivoGlosa(MotivoGlosa motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}

	public boolean isGlosar() {
		return glosar;
	}

	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
	}

	public ItemDiaria getItemDiaria() {
		return itemDiaria;
	}

	public void setItemDiaria(ItemDiaria itemDiaria) {
		this.itemDiaria = itemDiaria;
	}
	
	public Valor getValor() {
		return valor;
	}

	public void setValor(Valor valor) {
		this.valor = valor;
	}

	private Valor clonaValor(Valor valor) {
		Valor v = new Valor();
		v.setComponentValores(valor.getComponentValores());
		v.setOrdem(valor.getOrdem());
		v.setQuantidade(valor.getQuantidade());
		v.setSituacao(valor.getSituacao());
		v.setValor(valor.getValor());
		return v;
	}

	@Override
	public void setJustificativaGlosa(String justificativaGlosa) {
		this.justificativaGlosa = justificativaGlosa;
	}
	
	@Override
	public String getJustificativaGlosa() {
		return justificativaGlosa;
	}
}