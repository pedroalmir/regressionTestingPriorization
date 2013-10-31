package br.com.infowaypi.ecarebc.atendimentos;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemTaxa;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.Valor;

/**
 * Esta classe é apenas para receber os dados editáveis dos itensTaxa da guia.
 * 
 * @author Luciano Rocha
 *
 */
public class ItemTaxaLayer implements ItemGlosavelLayer{
	
	/**
	 * atributo que recebe a descrição da Taxa do ItemTaxa da guia.
	 */
	private Taxa taxa;
	
	/**
	 * atributo que recebe o quantidade da classe Valor do ItemTaxa da guia.
	 */
	private Valor valor;
	/**
	 * atributo que recebe a porcentagem que será glosada do ItemTaxa da guia.
	 */
	private ItemTaxa itemTaxa;
	
	private MotivoGlosa motivoGlosa;
	private boolean glosar;
	
	private String justificativaGlosa;
	
	public ItemTaxaLayer(ItemTaxa itemTaxa) {
		this.taxa = itemTaxa.getTaxa();
		this.valor = clonaValor(itemTaxa.getValor());
		this.itemTaxa = itemTaxa;
	}
	
	public Taxa getTaxa() {
		return taxa;
	}

	public void setTaxa(Taxa taxa) {
		this.taxa = taxa;
	}

	public ItemTaxa getItemTaxa() {
		return itemTaxa;
	}

	public void setItemTaxa(ItemTaxa itemTaxa) {
		this.itemTaxa = itemTaxa;
	}

	@Override
	public MotivoGlosa getMotivoGlosa() {
		return motivoGlosa;
	}

	@Override
	public void setMotivoGlosa(MotivoGlosa motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}

	@Override
	public boolean isGlosar() {
		return glosar;
	}

	@Override
	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
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
