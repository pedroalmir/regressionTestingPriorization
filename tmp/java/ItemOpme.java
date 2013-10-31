package br.com.infowaypi.ecarebc.opme;

import java.io.Serializable;
import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa uma órtese, prótese ou material especial associado à guia.
 * @author Luciano Infoway
 * @since 17/04/2013
 */
public class ItemOpme extends ImplColecaoSituacoesComponent implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long idItemOpme;
	@SuppressWarnings("rawtypes")
	private GuiaSimples guia;
	private Opme opme;
	private FornecedorOpme fornecedor;
	
	private BigDecimal valorUnitario;
	private BigDecimal valorTotal;
	
	private int quantidadeSolicitada;
	private int quantidadeRegulada;
	private int quantidadeAuditada;
	
	private Boolean autorizado;
	private Boolean glosar;

	private String observacaoSolicitacao;
	private String observacaoRegulacao;
	private String observacaoAuditoria;
	
	private Boolean auditado;
	
	public ItemOpme() {
		this.quantidadeSolicitada = 0; 
		this.quantidadeAuditada = 0;
		this.quantidadeRegulada = 0;
		this.valorUnitario = BigDecimal.ZERO;
	}
	
	public Long getIdItemOpme() {
		return idItemOpme;
	}

	public void setIdItemOpme(Long idItemOpme) {
		this.idItemOpme = idItemOpme;
	}

	public GuiaSimples getGuia() {
		return guia;
	}

	public void setGuia(GuiaSimples guia) {
		this.guia = guia;
	}

	public Opme getOpme() {
		return opme;
	}

	public void setOpme(Opme opme) {
		this.opme = opme;
	}
	
	public FornecedorOpme getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(FornecedorOpme fornecedor) {
		this.fornecedor = fornecedor;
	}

	public BigDecimal getValorTotal() {
		valorTotal = BigDecimal.ZERO;
		if (this.isAuditado() != null && this.isAuditado()){
			if(this.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())) {
				valorTotal = valorTotal.add(this.valorUnitario.multiply(new BigDecimal(this.quantidadeAuditada)));
			}
		}
		else {
			if (this.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao())){
				valorTotal = valorTotal.add(this.valorUnitario.multiply(new BigDecimal(this.quantidadeSolicitada)));
			}
			if(this.isSituacaoAtual(SituacaoEnum.AUTORIZADO.descricao())) {
				valorTotal = valorTotal.add(this.valorUnitario.multiply(new BigDecimal(this.quantidadeRegulada)));
			}
		}
		
		return valorTotal;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public int getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}

	public void setQuantidadeSolicitada(int quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}

	public int getQuantidadeRegulada() {
		return quantidadeRegulada;
	}

	public void setQuantidadeRegulada(int quantidadeRegulada) {
		this.quantidadeRegulada = quantidadeRegulada;
	}

	public int getQuantidadeAuditada() {
		return quantidadeAuditada;
	}

	public void setQuantidadeAuditada(int quantidadeAuditada) {
		this.quantidadeAuditada = quantidadeAuditada;
	}

	public Boolean getAutorizado(){
		return autorizado;
	}
	
	public Boolean isAutorizado() {
		return autorizado;
	}

	public void setAutorizado(Boolean autorizado) {
		this.autorizado = autorizado;
	}
	
	public Boolean getGlosar(){
		return glosar;
	}
	
	public Boolean isGlosar() {
		return glosar;
	}

	public void setGlosar(Boolean glosar) {
		this.glosar = glosar;
	}

	public String getObservacaoSolicitacao() {
		return observacaoSolicitacao;
	}
	
	public void setObservacaoSolicitacao(String observacaoSolicitacao) {
		this.observacaoSolicitacao = observacaoSolicitacao;
	}
	
	public String getObservacaoRegulacao() {
		return observacaoRegulacao;
	}
	
	public void setObservacaoRegulacao(String observacaoRegulacao) {
		this.observacaoRegulacao = observacaoRegulacao;
	}
	
	public String getObservacaoAuditoria() {
		return observacaoAuditoria;
	}

	public void setObservacaoAuditoria(String observacaoAuditoria) {
		this.observacaoAuditoria = observacaoAuditoria;
	}
	
	public Boolean isAuditado() {
		return auditado;
	}

	public void setAuditado(Boolean auditado) {
		this.auditado = auditado;
	}

	public void tocarObjetos(){
		this.getOpme().getCodigo();
		if (this.getFornecedor() != null) {
			this.getFornecedor().getNomeFornecedor();
		}
		this.getSituacoes().size();
	}

	/**
	 * @param usuario
	 * @return Boolean
	 * Método usado para setar a situação do objeto como 'Ativo(a)' no momento de seu cadastramento.
	 */
	public Boolean validate(UsuarioInterface usuario){
		return true;
	}
	
	/**
	 * Método que busca a situação atual no componente coleção situações.
	 * Usado para mostrar um drill-down de situação em opmes.tag.
	 * @return
	 */
	public SituacaoInterface getSituacaoAtual() {
		String descricao = this.getSituacao().getDescricao();
		return this.getColecaoSituacoes().getSituacao(descricao);
	}
}
