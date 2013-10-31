package br.com.infowaypi.ecarebc.opme;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe que representa uma órtese, prótese ou material especial.
 * @author Luciano Infoway
 * @since 17/04/2013
 */
public class Opme extends ImplColecaoSituacoesComponent implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long idOpme;
	private String codigo;
	private String descricao;
	private FornecedorOpme fornecedor;
	
	private Set<ItemOpme> itensOpmes;
	
	public Opme() {
		mudarSituacao(null, SituacaoEnum.ATIVO.descricao(), "Cadastro de OPME.", new Date());
	}
	
	public Long getIdOpme() {
		return idOpme;
	}

	public void setIdOpme(Long idOpme) {
		this.idOpme = idOpme;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public FornecedorOpme getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(FornecedorOpme fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Set<ItemOpme> getItensOpmes() {
		return itensOpmes;
	}

	public void setItensOpmes(Set<ItemOpme> itensOpmes) {
		this.itensOpmes = itensOpmes;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getIdOpme())
			.append(this.getDescricao())
			.append(this.getCodigo())
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if (!(obj instanceof Opme))
			return false;
		
		Opme outro = (Opme)obj;
		
		return new EqualsBuilder()
			.append(this.descricao,outro.descricao)
			.append(this.codigo,outro.getCodigo())
			.isEquals();
	}

	/**
	 * @param motivo
	 * @param usuario
	 * @throws Exception
	 * Método responsável por mudar a situação da Diária para 'Inativo(a)' no seu cadastro.
	 */
	public void inativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (isSituacaoAtual(SituacaoEnum.INATIVO.descricao()))
			throw new Exception("Já se encontra inativado!");
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		mudarSituacao(usuario, SituacaoEnum.INATIVO.descricao(), motivo, new Date());
	}
	
	/**
	 * @param motivo
	 * @param usuario
	 * @throws Exception
	 * Método responsável por mudar a situação da Diária para 'Ativo(a)' no seu cadastro.
	 */
	public void reativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (isSituacaoAtual(SituacaoEnum.ATIVO.descricao()))
			throw new Exception("Já se encontra ativado!");
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, new Date());
	}
	
	
	/**
	 * @param usuario
	 * @return Boolean
	 * Método usado para setar a situação do objeto como 'Ativo(a)' no momento de seu cadastramento.
	 */
	public Boolean validate(UsuarioInterface usuario){
		return true;
	}

}
