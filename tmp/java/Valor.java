package br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.utils.SituacaoUtils;
import br.com.infowaypi.msr.situations.Situacao;

/**
 * 
 * @author Marcus Boolean
 * @changes Danilo Nogueira Portela
 *
 */
public class Valor implements Serializable, Cloneable{
	
//	Constantes de horas
	public static final int H24 = 1;
	public static final int H48 = 2;
	public static final int H72 = 3;
	public static final int H96 = 4;
	public static final int H120 = 5;
	
	private static final long serialVersionUID = 1L;
	private Long idValor;
	private Situacao situacao;
	private int ordem;
	private BigDecimal valor;
	private int quantidade;
	private ComponentValores componentValores;
	
	
	public Valor() {
		this.situacao = null;
		this.valor = BigDecimal.ZERO;
	}
	
	
	public ComponentValores getComponentValores() {
		return componentValores;
	}

	public void setComponentValores(ComponentValores componentValores) {
		this.componentValores = componentValores;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	
	public int getOrdem() {
		return ordem;
	}
	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}
	public Situacao getSituacao() {
		return situacao;
	}
	public Long getIdValor() {
		return idValor;
	}

	public void setIdValor(Long idValor) {
		this.idValor = idValor;
	}

	public void setSituacao(Situacao situacao) {
		this.situacao = situacao;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	@Override
	public Object clone(){
		Valor clone = new Valor();
		
		clone.setOrdem(this.ordem);
		clone.setQuantidade(this.quantidade);
		clone.setSituacao((Situacao)SituacaoUtils.clone(this.situacao));
		clone.setValor(this.getValor());
		
		return valor;
	}

}
