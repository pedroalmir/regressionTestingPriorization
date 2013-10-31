package br.com.infowaypi.ecare.contrato;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.produto.Produto;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;

/**
 * @author DANNYLVAN
 *
 */
public class ContratoSR extends ImplColecaoSituacoesComponent  implements Serializable{

	private static final long serialVersionUID = -3677224321521323869L;

	private Long idContrato;
	
	private Segurado segurado;
	
	private Date dataEfetivacao;
	
	private boolean beneficiario;
	
	private String numeroCartaoAtual;
	
	private String numeroProposta;
	
	private Long idSegurado;
	
	private Produto produto;

	public Long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(Long idContrato) {
		this.idContrato = idContrato;
	}

	public Segurado getSegurado() {
		return segurado;
	}

	public void setSegurado(Segurado segurado) {
		this.segurado = segurado;
	}

	public Date getDataEfetivacao() {
		return dataEfetivacao;
	}

	public void setDataEfetivacao(Date dataEfetivacao) {
		this.dataEfetivacao = dataEfetivacao;
	}
	
	public boolean isBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(boolean beneficiario) {
		this.beneficiario = beneficiario;
	}

	public String getNumeroCartaoAtual() {
		return numeroCartaoAtual;
	}

	public void setNumeroCartaoAtual(String numeroCartaoAtual) {
		this.numeroCartaoAtual = numeroCartaoAtual;
	}

	
	public Long getIdSegurado() {
		return idSegurado;
	}

	public void setIdSegurado(Long idSegurado) {
		this.idSegurado = idSegurado;
	}
	
	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public String getNumeroProposta() {
		return numeroProposta;
	}

	public void setNumeroProposta(String numeroProposta) {
		this.numeroProposta = numeroProposta;
	}

}
