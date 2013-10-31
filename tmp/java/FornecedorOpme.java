package br.com.infowaypi.ecarebc.opme;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;

/**
 * Classe que representa um fornecedor de uma órtese, prótese ou material especial.
 * @author Luciano Infoway
 * @since 17/04/2013
 */
public class FornecedorOpme extends ImplColecaoSituacoesComponent implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long idFornecedorOpme;
	private String nomeFornecedor;
	
	private Set<Opme> opmes;
	
	public FornecedorOpme() {
		mudarSituacao(null, SituacaoEnum.ATIVO.descricao(), "Cadastro de fornecedor.", new Date());
	}
	
	public Long getIdFornecedorOpme() {
		return idFornecedorOpme;
	}
	
	public void setIdFornecedorOpme(Long idFornecedorOpme) {
		this.idFornecedorOpme = idFornecedorOpme;
	}
	
	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}

	public Set<Opme> getOpmes() {
		return opmes;
	}

	public void setOpmes(Set<Opme> opmes) {
		this.opmes = opmes;
	}
}
