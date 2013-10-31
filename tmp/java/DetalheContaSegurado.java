package br.com.infowaypi.ecare.financeiro.conta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecare.segurados.Cartao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;

/**
 * 
 * @author Diogo Vinícius
 *
 */
public abstract class DetalheContaSegurado implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long idDetalheContaSegurado;
	private BigDecimal valorFinanciamento;
	private BigDecimal valorCoparticipacao;
	private BigDecimal valorSegundaViaCartao;
	private Date competencia;
	private Set<GuiaSimples> guias;
	private Set<Cartao> cartoes;

	DetalheContaSegurado(){}
	
	public DetalheContaSegurado(Date competencia) {
		this.valorFinanciamento = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.valorCoparticipacao = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.valorSegundaViaCartao = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.competencia = competencia;
		this.guias = new HashSet<GuiaSimples>();
		this.cartoes = new HashSet<Cartao>();
	}
	
	public void zerarValoresDetalhe(){
		this.valorFinanciamento = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.valorCoparticipacao = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.valorSegundaViaCartao = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public BigDecimal getValorTotal(){
		BigDecimal valor = BigDecimal.ZERO;
		valor = valor.add(this.getValorFinanciamento());
		valor = valor.add(this.getValorCoparticipacao());
		valor = valor.add(this.getValorSegundaViaCartao());
		return valor.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public Long getIdDetalheContaSegurado() {
		return idDetalheContaSegurado;
	}

	public void setIdDetalheContaSegurado(Long idDetalheContaSegurado) {
		this.idDetalheContaSegurado = idDetalheContaSegurado;
	}

	public BigDecimal getValorFinanciamento() {
		return valorFinanciamento;
	}

	public void setValorFinanciamento(BigDecimal valorFinanciamento) {
		this.valorFinanciamento = valorFinanciamento;
	}

	public BigDecimal getValorCoparticipacao() {
		return valorCoparticipacao;
	}

	public void setValorCoparticipacao(BigDecimal valorCoparticipacao) {
		this.valorCoparticipacao = valorCoparticipacao;
	}

	public BigDecimal getValorSegundaViaCartao() {
		return valorSegundaViaCartao;
	}

	public void setValorSegundaViaCartao(BigDecimal valorSegundaViaCartao) {
		this.valorSegundaViaCartao = valorSegundaViaCartao;
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public Set<GuiaSimples> getGuias() {
		return guias;
	}

	public void setGuias(Set<GuiaSimples> guias) {
		this.guias = guias;
	}

	public Set<Cartao> getCartoes() {
		return cartoes;
	}

	public void setCartoes(Set<Cartao> cartoes) {
		this.cartoes = cartoes;
	}

}
