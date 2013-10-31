package br.com.infowaypi.ecare.services.layer;

import java.math.BigDecimal;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavelLayer;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.procedimentos.Pacote;

/** @author SR Team - Marcos Roberto 21.05.2012 */
public class ItemPacoteLayer implements ItemGlosavelLayer {

	private Pacote pacote;
	private BigDecimal porcentagem;
	private MotivoGlosa motivoGlosa;
	private boolean glosar;
	private String justificativaGlosa;
	
	private ItemPacote itemPacote;

	public ItemPacoteLayer(ItemPacote item) {
		this.pacote = item.getPacote();
		this.itemPacote = item;
		this.porcentagem = item.getPorcentagem();
	}
	
	public Pacote getPacote() {
		return pacote;
	}

	public void setPacote(Pacote pacote) {
		this.pacote = pacote;
	}

	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
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

	public ItemPacote getItemPacote() {
		return itemPacote;
	}

	public void setItemPacote(ItemPacote itemPacote) {
		this.itemPacote = itemPacote;
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