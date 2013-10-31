/**
 * 
 */
package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;

/**
 * @author Idelvane
 *
 */
public class ResumoIncluidos {

	private String cpf;
	private String numeroDoCartao;
	private BigDecimal valorIndividual;
	private String descricao;
	private int idade;
	private BigDecimal valorBruto;
	private int ordem;
	private String cartaoDependente;

	public String getCartaoDependente() {
		return cartaoDependente;
	}

	public void setCartaoDependente(String cartaoDependente) {
		this.cartaoDependente = cartaoDependente;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public ResumoIncluidos() {
		this.valorIndividual = BigDecimal.ZERO;
		this.valorBruto = BigDecimal.ZERO;
	}
	
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public int getIdade() {
		return idade;
	}
	public void setIdade(int idade) {
		this.idade = idade;
	}
	public String getNumeroDoCartao() {
		return numeroDoCartao;
	}
	public void setNumeroDoCartao(String numeroDoCartao) {
		this.numeroDoCartao = numeroDoCartao;
	}
	public BigDecimal getValorIndividual() {
		return valorIndividual;
	}
	public void setValorIndividual(BigDecimal valorIndividual) {
		this.valorIndividual = valorIndividual;
	}

	public BigDecimal getValorBruto() {
		return valorBruto;
	}

	public void setValorBruto(BigDecimal valorBruto) {
		this.valorBruto = valorBruto;
	}
}
