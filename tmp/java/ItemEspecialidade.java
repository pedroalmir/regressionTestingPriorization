package br.com.infowaypi.ecarebc.associados;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author jefferson
 */

public class ItemEspecialidade extends ImplColecaoSituacoesComponent implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idItemEspecialidade;
	private Especialidade especialidade;
	private Prestador prestador;
	private boolean urgencia;
	private boolean eletivo;
	
	public ItemEspecialidade() {
		this.mudarSituacao(null, SituacaoEnum.ATIVO.descricao(), "Cadastramento", new Date());
	}

	/** Motivo da alteração do cadastro de um acordo */
	private transient String motivoAlteracoes;

	/** Usuário responsável pelas alterações do cadastro de um acordo */
	private transient UsuarioInterface usuarioAlteracoes;

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}

	public Long getIdItemEspecialidade() {
		return idItemEspecialidade;
	}

	public void setIdItemEspecialidade(Long idItemEspecialidade) {
		this.idItemEspecialidade = idItemEspecialidade;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public boolean isUrgencia() {
		return urgencia;
	}

	public void setUrgencia(boolean urgencia) {
		this.urgencia = urgencia;
	}

	public boolean isEletivo() {
		return eletivo;
	}

	public void setEletivo(boolean eletivo) {
		this.eletivo = eletivo;
	}

	public void setMotivoAlteracoes(String motivoAlteracoes) {
		this.motivoAlteracoes = motivoAlteracoes;
	}

	public void setUsuarioAlteracoes(UsuarioInterface usuarioAlteracoes) {
		this.usuarioAlteracoes = usuarioAlteracoes;
	}

	@Override
	public String toString() {
		return getEspecialidade().getDescricao();
	}
		
	public Boolean validate (UsuarioInterface usuario) throws ValidateException{
		this.usuarioAlteracoes = usuario;
		
		boolean naoMarcou = !this.urgencia && !this.eletivo;
		if (naoMarcou ){
			throw new ValidateException(MensagemErroEnum.NAO_MARCOU_MODALIDADE.getMessage());
		}
		this.getSituacao().setUsuario(usuario);
		return true;
	}
	
	public void ativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		if (this.isSituacaoAtual(SituacaoEnum.ATIVO.descricao()))
			throw new Exception("Este acordo já está ativo.");
		
		this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), motivo, new Date());
	}
	
	public void inativar(String motivo, UsuarioInterface usuario) throws Exception {
		if (Utils.isStringVazia(motivo))
			throw new Exception("O motivo deve ser preenchido!");
		
		if (this.isSituacaoAtual(SituacaoEnum.INATIVO.descricao()))
			throw new Exception("Este acordo já está inativo.");
		
		this.mudarSituacao(usuario, SituacaoEnum.INATIVO.descricao(), motivo, new Date());
	}
}