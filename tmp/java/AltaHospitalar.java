package br.com.infowaypi.ecarebc.atendimentos.alta;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class AltaHospitalar implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long idAltaHospitalar;
	private Date dataDeAlta;
	private UsuarioInterface usuario;
	private MotivoAlta motivo;
	private GuiaInternacao guiaInternacao;
	
	public AltaHospitalar() {
	}
	
	public AltaHospitalar(Date dataDeAlta, UsuarioInterface usuario,
			MotivoAlta motivo, GuiaInternacao guiaInternacao){
		
		if(dataDeAlta == null) {
			throw new RuntimeException("Caro Usuário, informe a data de alta!");
		}
		if(usuario == null) {
			throw new RuntimeException("Erro durante a geração de alta: Usuário null");
		}
		if(motivo == null) {
			throw new RuntimeException("Caro Usuário, informe o motivo de alta!");
		}
		if(guiaInternacao == null) {
			throw new RuntimeException("Erro durante a geração de alta: Guia internação null");
		}
		
		this.dataDeAlta = dataDeAlta;
		this.usuario = usuario;
		this.motivo = motivo;
		this.guiaInternacao = guiaInternacao;
		this.guiaInternacao.setAltaHospitalar(this);
	}
	
	public GuiaInternacao getGuiaInternacao() {
		return guiaInternacao;
	}
	public void setGuiaInternacao(GuiaInternacao guiaInternacao) {
		this.guiaInternacao = guiaInternacao;
	}
	public Long getIdAltaHospitalar() {
		return idAltaHospitalar;
	}
	public void setIdAltaHospitalar(Long idAltaHospitalar) {
		this.idAltaHospitalar = idAltaHospitalar;
	}
	public Date getDataDeAlta() {
		return dataDeAlta;
	}
	public void setDataDeAlta(Date dataDeAlta) {
		this.dataDeAlta = dataDeAlta;
	}
	public UsuarioInterface getUsuario() {
		return usuario;
	}
	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}
	public MotivoAlta getMotivo() {
		return motivo;
	}
	public void setMotivo(MotivoAlta motivo) {
		this.motivo = motivo;
	}
	
	@Override
	public Object clone(){
		AltaHospitalar clone = new AltaHospitalar();
		
		clone.setDataDeAlta(this.getDataDeAlta());
		clone.setMotivo(this.getMotivo());
		clone.setUsuario(this.getUsuario());
		
		return clone;
	}
}
