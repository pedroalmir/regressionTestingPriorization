package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;

/**
 * @author Eduardo Vera - 02.07.2012
 * 
 * Implementação de alguns métodos e atributos
 * que trabalham em conjunto com a interface 
 * @br.com.infowaypi.msr.user.UsuarioInterface
 * na definição de processos comuns aos itens glosáveis.
 *
 */
public class ItemGuiaGlosavelImpl implements ItemGlosavel {

	private ItemGlosavel itemGlosavelAnterior;

	@Override
	public void setItemGlosavelAnterior(ItemGlosavel anterior) {
		this.itemGlosavelAnterior = anterior;
	}
	
	@Override
	public ItemGlosavel getItemGlosavelAnterior() {
		return itemGlosavelAnterior;
	}

	@Override
	public ItemGlosavel accept(ItemGlosavelVisitor visitor) {
		return null;
	}

	@Override
	public boolean isTipoGuia() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTipoItemGuia() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTipoProcedimento() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BigDecimal getValorItem() {
		// TODO Auto-generated method stub
		return null;
	}
}