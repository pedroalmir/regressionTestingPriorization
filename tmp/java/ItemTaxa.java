package br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.Taxa;
import br.com.infowaypi.ecarebc.atendimentos.acordos.AcordoTaxa;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.situations.SituacaoInterface;

@SuppressWarnings({"rawtypes"})
public class ItemTaxa extends ItemGuia implements Cloneable {
	
	private static final long serialVersionUID = 1L;
	private Long idItemTaxa;
	private Taxa taxa;
	private GuiaCompleta guia;
	private ItemGlosavel itemGlosavelAnterior;
	
	public ItemTaxa(){
		super();
		mudarSituacao(null, SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.INCLUSAO_ITEM.getMessage(), new Date());
	}
	
	public GuiaCompleta getGuia() {
		return guia;
	}
	public void setGuia(GuiaCompleta guia) {
		this.guia = guia;
	}
	public Long getIdItemTaxa() {
		return idItemTaxa;
	}
	public void setIdItemTaxa(Long idItemTaxas) {
		this.idItemTaxa = idItemTaxas;
	}
	
	public Taxa getTaxa() {
		return taxa;
	}
	
	public void setTaxa(Taxa taxa) {
		this.taxa = taxa;
	}

	public Boolean validate(){
		this.getValor().setValor(BigDecimal.valueOf(taxa.getValor()));
		return true;
	}
	
	public void recalcularCampos(){
		if(this.guia != null){
			for (AcordoTaxa acordo : guia.getPrestador().getAcordosTaxaAtivos()) {
				if(acordo.getTaxa().equals(this.taxa)){
					this.getValor().setValor(acordo.getValor());
				}
			}
		}
	}
	
	public void tocarObjetos(){
		super.tocarObjetos();
		this.getSituacao();
		this.getTaxa().getDescricao();
		for (SituacaoInterface situacao : getSituacoes()) {
			situacao.getUsuario();
		}
	}
	
	@Override
	public void setItemGlosavelAnterior(ItemGlosavel anterior) {
		itemGlosavelAnterior = anterior;
	}

	@Override
	public ItemGlosavel getItemGlosavelAnterior() {
		return itemGlosavelAnterior;
	}

	@Override
	public ItemGlosavel accept(ItemGlosavelVisitor visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public boolean isItemDiaria() {
		return false;
	}
	
	@Override
	public boolean isItemGasoterapia() {
		return false;
	}
	
	@Override
	public boolean isItemTaxa() {
		return true;
	}
	
	@Override
	public boolean isItemPacote() {
		return false;
	}
	
	@Override
	public ItemTaxa getItemGuiaTaxa() {
		return ImplDAO.findById(getIdItemGuia(), ItemTaxa.class);
	}

	@Override
	public boolean isTipoGuia() {
		return false;
	}

	@Override
	public boolean isTipoItemGuia() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isTipoProcedimento() {
		// TODO Auto-generated method stub
		return false;
	}
}
