package br.com.infowaypi.ecare.services.honorariomedico.adapter;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class AdapterHonorario {

	/**
	 * que sera manipulado na tela
	 */
	private HonorarioExterno honorario;
	private Profissional profissional;
	private BigDecimal 	porcentagem;
	
	private HonorarioExterno honorarioNovo;

	/**
	 * usado para comparacao
	 */
	private boolean auditado;
	
	public AdapterHonorario(HonorarioExterno honorario) {
		this.honorario 		= honorario;
		this.auditado 		= honorario.isAuditado();
		this.profissional 	= this.honorario.getProfissional();
		this.porcentagem = this.honorario.getPorcentagem();
	}

	public HonorarioExterno getHonorario() {
		return honorario;
	}

	public void setHonorario(HonorarioExterno honorario) {
		this.honorario = honorario;
	}

	public Profissional getProfissional() {
		return profissional;
	}

	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof AdapterHonorario)) {
			return false;
		}
		return this.honorario.equals(((AdapterHonorario) obj).honorario);
	}
	
	public HonorarioExterno getHonorarioNovo(UsuarioInterface usuario) {
		if (honorarioNovo == null){
			this.honorarioNovo = createHonorarioNovo(usuario);
		}
		
		return honorarioNovo;
	}

	public HonorarioExterno createHonorarioNovo(UsuarioInterface usuario) {
		HonorarioExterno honorarioNovo =  new HonorarioExterno(usuario);
		
		honorarioNovo.setGrauDeParticipacao(this.getHonorario().getGrauDeParticipacao());
		honorarioNovo.setProcedimento(this.getHonorario().getProcedimento());
		
		//setando na guia
		honorarioNovo.setGuiaHonorario(this.getHonorario().getGuiaHonorario());
		this.getHonorario().getGuiaHonorario().getHonorariosMedico().add(honorarioNovo);
		
		//resetando o profissional
		this.getHonorario().getProcedimento().setProfissionalResponsavel(this.getProfissional());
		
		this.getHonorario().getGuiaHonorario().setProfissional(this.getProfissional());
		honorarioNovo.setHorarioEspecial(this.getHonorario().isHorarioEspecial());
		honorarioNovo.setIncluiVideo(this.getHonorario().isIncluiVideo());
		honorarioNovo.setPorcentagem(this.getHonorario().getPorcentagem());
		honorarioNovo.setProfissional(this.getHonorario().getProfissional());
		honorarioNovo.setValorTotal(this.getHonorario().getValorTotal());
		honorarioNovo.setProfissional(this.getProfissional());
				
		return honorarioNovo;
	}

	public void setHonorarioNovo(HonorarioExterno honorarioExterno) {
		this.honorarioNovo = honorarioExterno;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(honorario).toHashCode();
	}
	
	@Override
	public String toString() {
		return new StringBuilder().append("[honorario: ")
									.append(honorario)
									.append("; profissional: ")
									.append(profissional)
									.append("]")
									.toString();
	}
	
	public HonorarioExterno getHonorarioNovo() {
		return honorarioNovo;
	}

	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}

	public boolean isAuditado() {
		return auditado;
	}

	public void setAuditado(boolean auditado) {
		this.auditado = auditado;
	}
}
