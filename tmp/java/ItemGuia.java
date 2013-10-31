package br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecare.services.recurso.ItemRecursoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.utils.SituacaoUtils;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.situations.SituacaoInterface;

/**
 * @author Marcus Boolean
 *
 */
public class ItemGuia extends ImplColecaoSituacoesComponent implements Cloneable, ItemGlosavel {
	
	private static final long serialVersionUID = 1L;
	private Long idItemGuia;
	private ComponentValores componentValores;
	private Valor valor;
	private ItemRecursoGlosa itemRecurso;
	private String justificativaGlosa;
	
	/** 
	 * Descrição do motivo da glosa do item informado e Responsavel pela marcacao, 
	 * se o item informado sera ou nao glosado utilizados na auditar guia.
	 */
	private MotivoGlosa motivoGlosa;
	
	/**
	 * Justificativa do motivo de glosa.
	 */
	public ItemGuia() {
		this.valor = new Valor();
	}
	
	public ComponentValores getComponentValores() {
		return componentValores;
	}

	public void setComponentValores(ComponentValores componentValores) {
		this.componentValores = componentValores;
	}

	public Valor getValor() {
		return valor;
	}
	
	public String getValorTotalFormatado() {
		BigDecimal valorFomatado = new BigDecimal(this.getValorTotal());
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00").format(valorFomatado.setScale(2,BigDecimal.ROUND_HALF_UP)), 9, " "); 
	}

	public void setValor(Valor valor) {
		this.valor = valor;
	}

	public Long getIdItemGuia() {
		return idItemGuia;
	}

	public void setIdItemGuia(Long idItemGuia) {
		this.idItemGuia = idItemGuia;
	}
	
	public void tocarObjetos(){
		this.getSituacoes().size();
	}

	public float getValorTotal() {	
		return this.getValor().getValor().multiply(new BigDecimal(this.getValor().getQuantidade())).floatValue();
	}
	
	public MotivoGlosa getMotivoGlosa() {
		return motivoGlosa;
	}

	public void setMotivoGlosa(MotivoGlosa motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}

	public SituacaoInterface getSituacaoAnterior(SituacaoInterface situacao) {
		int ordemSituacao = situacao.getOrdem();
		if (ordemSituacao == 1)
			return null;

		for (SituacaoInterface situacaoAtual : this.getSituacoes()) {
			if (situacaoAtual.getOrdem() == ordemSituacao - 1)
				return situacaoAtual;
		}
		return situacao;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		ItemGuia clone = new ItemGuia();
		
		clone.setColecaoSituacoes(SituacaoUtils.clone(this.getColecaoSituacoes()));
		clone.setComponentValores(this.getComponentValores());
		clone.setSituacao(this.getSituacao());
		clone.setValor(this.getValor());
		
		return clone;
	}
	
	public boolean isTipoItemGuia() {
		return true;
	}

	public ItemRecursoGlosa getItemRecurso() {
		return itemRecurso;
	}

	public void setItemRecurso(ItemRecursoGlosa itemRecurso) {
		this.itemRecurso = itemRecurso;
	}
	
	public boolean isItemDiaria() {
		return false;
	}
	public boolean isItemGasoterapia() {
		return false;
	}
	public boolean isItemTaxa() {
		return false;
	}
	public boolean isItemPacote() {
		return false;
	}

	public ItemGasoterapia getItemGuiaGasoterapia() {
		return null;
	}

	public ItemTaxa getItemGuiaTaxa() {
		return null;
	}
	
	public ItemDiaria getItemGuiaDiaria() {
		return null;
	}
	
	public ItemPacote getItemGuiaPacote() {
		return null;
	}
	
	public ItemGlosavel getItemGlosavelAnterior() {
		return null;
	}

	public String getJustificativaGlosa() {
		return justificativaGlosa;
	}

	public void setJustificativaGlosa(String justificativaGlosa) {
		this.justificativaGlosa = justificativaGlosa;
	} 
	public BigDecimal getValorItem() {
		return new BigDecimal(this.getValorTotal());
	}

	@Override
	public void setItemGlosavelAnterior(ItemGlosavel anterior) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemGlosavel accept(ItemGlosavelVisitor visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTipoGuia() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTipoProcedimento() {
		// TODO Auto-generated method stub
		return false;
	}
	
}