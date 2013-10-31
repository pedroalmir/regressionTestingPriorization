package br.com.infowaypi.ecarebc.procedimentos;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Transient;

import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.utils.SituacaoUtils;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

@SuppressWarnings( "serial")
public class ProcedimentoHonorario extends Procedimento {
	
	private boolean glosar = false;
	private String motivoGlosa;

	/**campo transiente utilizado no @ResumoGuiasHonorarioMedico*/
	@Transient
	private boolean jaAuditado;

	public ProcedimentoHonorario() {
		super();
		this.setAdicionarHonorario(Boolean.TRUE);
	}
	
	@Override
	public int getTipoProcedimento() {
		return ProcedimentoInterface.PROCEDIMENTO_HONORARIO;
	}
	
	@Override
	public Boolean validate(UsuarioInterface usuario) throws Exception {
		if (this.getProfissionalResponsavel() == null) {
			for (Honorario honorario : this.getHonorariosGuiaHonorarioMedico()) {
				this.setProfissionalResponsavel(honorario.getProfissional());
				break;
			}
		}
		return super.validate(usuario);
	}

	public String getMotivoGlosa() {
		return motivoGlosa;
	}

	public void setMotivoGlosa(String motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}

	public boolean isGlosar() {
		return glosar;
	}

	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
	}
	
	public void glosar(UsuarioInterface usuario) {
		Assert.isFalse(Utils.isStringVazia(this.getMotivoGlosa()), MensagemErroEnum.PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA.getMessage(this.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()));
		
		this.mudarSituacao(usuario, SituacaoEnum.GLOSADO.descricao(), this.getMotivoGlosa(), new Date());

		BigDecimal novoValor = this.getGuia().getValorTotal().subtract(this.getValorTotal());
		this.getGuia().setValorTotal(novoValor);
	}

	public boolean isJaAuditado() {
		return jaAuditado;
	}

	public void setJaAuditado(boolean jaAuditado) {
		this.jaAuditado = jaAuditado;
	}
	
	@Override
	public ItemGlosavel accept(ItemGlosavelVisitor visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public ItemGlosavel clone() {
		ProcedimentoHonorario clone = new ProcedimentoHonorario();

		for (Honorario honorario : this.getHonorariosGuiaOrigem()) {
			Honorario honorarioClonado = null;
			try {
				honorarioClonado = (Honorario) honorario.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			honorarioClonado.setProcedimento(clone);
			clone.getHonorariosGuiaOrigem().add(honorarioClonado);
		}
		
		clone.setProfissionalResponsavel(this.getProfissionalResponsavel());
		clone.setIncluiVideo(isIncluiVideo());
		clone.setPorcentagem(this.getPorcentagem());
		clone.setBilateral(this.getBilateral());
		clone.setHorarioEspecial(this.isHorarioEspecial());
		clone.setProcedimentoDaTabelaCBHPM(this.getProcedimentoDaTabelaCBHPM());
		clone.setColecaoSituacoes(SituacaoUtils.clone(this.getColecaoSituacoes()));
		clone.setSituacao(this.getSituacao());
		clone.setQuantidade(this.getQuantidade());
		clone.setValorAtualDaModeracao(this.getValorAtualDaModeracao());
		clone.setValorAtualDoProcedimento(this.getValorAtualDoProcedimento());
		clone.setValorCoParticipacao(this.getValorCoParticipacao());
		
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (glosar ? 1231 : 1237);
		result = prime * result + (jaAuditado ? 1231 : 1237);
		result = prime * result
				+ ((motivoGlosa == null) ? 0 : motivoGlosa.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.getSituacao().getDescricao() != null && !this.getSituacao().getDescricao().equals((SituacaoEnum.AUDITADO.descricao())) && 
			!this.getSituacao().getDescricao().equals((SituacaoEnum.AUTORIZADO.descricao())) &&
			!this.getSituacao().getDescricao().equals((SituacaoEnum.GLOSADO.descricao())))
			 {
			/**
			 * está retorna apenas false para permitir inserção de mais de um honorário clínico
			 * no registrar honorário individual.
			 * @author Luciano Rocha
			 * @since 01/02/2013
			 */
			return false;
		}
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof ProcedimentoHonorario))
			return false;
		ProcedimentoHonorario other = (ProcedimentoHonorario) obj;
		if (glosar != other.glosar)
			return false;
		if (jaAuditado != other.jaAuditado)
			return false;
		if (motivoGlosa == null) {
			if (other.motivoGlosa != null)
				return false;
		} else if (!motivoGlosa.equals(other.motivoGlosa))
			return false;
		
		return true;
	}

	
}
