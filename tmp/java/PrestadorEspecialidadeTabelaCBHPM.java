package br.com.infowaypi.ecarebc.associados;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class PrestadorEspecialidadeTabelaCBHPM extends ImplColecaoSituacoesComponent implements Serializable{
	
	private static final long serialVersionUID = -3473225207872890832L;
	
	private Long idPrestadorEspecialidadeTabelaCBHPM ; 
	private Prestador prestador;
	private TabelaCBHPM tabelaCBHPM;
	private Especialidade especialidade;
	private boolean liberarCoParticipacao ;
	private boolean urgencia;
	private boolean eletivo;
	
	public PrestadorEspecialidadeTabelaCBHPM() {
		this.mudarSituacao(null, SituacaoEnum.ATIVO.descricao(), "Cadastramento", new Date());
	}
	
	public Long getIdPrestadorEspecialidadeTabelaCBHPM() {
		return idPrestadorEspecialidadeTabelaCBHPM;
	}
	
	public void setIdPrestadorEspecialidadeTabelaCBHPM(
			Long idPrestadorEspecialidadeTabelaCBHPM) {
		this.idPrestadorEspecialidadeTabelaCBHPM = idPrestadorEspecialidadeTabelaCBHPM;
	}
	
	public Prestador getPrestador() {
		return prestador;
	}
	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	public TabelaCBHPM getTabelaCBHPM() {
		return tabelaCBHPM;
	}
	public void setTabelaCBHPM(TabelaCBHPM tabelaCBHPM) {
		this.tabelaCBHPM = tabelaCBHPM;
	}
	public Especialidade getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}
	public boolean isLiberarCoParticipacao() {
		return liberarCoParticipacao;
	}
	public void setLiberarCoParticipacao(boolean liberarCoParticipacao) {
		this.liberarCoParticipacao = liberarCoParticipacao;
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
	
	public Boolean validate(UsuarioInterface usuario) throws ValidateException{
		if(urgencia && eletivo){
			throw new ValidateException("Não é permitido cadastrar consulta para a modalidade Urgência e Eletivo ao mesmo tempo, Escolha apenas uma das modalidades.");
		}
		this.getSituacao().setUsuario(usuario);
		return Boolean.TRUE;
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

	@Override
	public String toString() {
		return getTabelaCBHPM().getDescricao();
	}
}