package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;

/**
 * @author Eduardo Vera - 02.07.2012
 * 
 * Interface que define todos os itens glosáveis de uma guia 
 * (incluindo a própria guia) e seus métodos comuns.
 *
 */

public interface ItemGlosavel {
	
	public void setItemGlosavelAnterior(ItemGlosavel anterior);
	public ItemGlosavel getItemGlosavelAnterior();

	public ItemGlosavel accept(ItemGlosavelVisitor visitor);
	
	public boolean isTipoGuia();
	public boolean isTipoItemGuia();
	public boolean isTipoProcedimento();
	
	public BigDecimal getValorItem();
}