package br.com.infowaypi.ecarebc.atendimentos.acordos;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public abstract class AbstractAcordo extends ImplColecaoSituacoesComponent implements Serializable{

	private static final long serialVersionUID = 1L;
	private Long IdAcordo;
	private Prestador prestador;
	
	public AbstractAcordo() {
		this(null);
	}
	
	public AbstractAcordo(UsuarioInterface usuario) {
		this.mudarSituacao(usuario, SituacaoEnum.ATIVO.descricao(), MotivoEnum.ACORDO_ATIVO.getMessage(), new Date());
	}
	
	public Long getIdAcordo() {
		return IdAcordo;
	}
	
	public void setIdAcordo(Long idAcordo) {
		IdAcordo = idAcordo;
	}
	
	public Prestador getPrestador() {
		return prestador;
	}
	
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
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

	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		this.getSituacao().setUsuario(usuario);
		return this.validate();
	}
	
	public abstract Boolean validate() throws ValidateException;
}
