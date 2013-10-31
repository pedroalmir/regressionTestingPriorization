package br.com.infowaypi.ecarebc.procedimentos;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.atendimentos.ManagerGuia;
import br.com.infowaypi.ecarebc.utils.ProcedimentoUtils;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class ProcedimentoAnestesico extends Procedimento{
	
	private BigDecimal porcentagemProcedimentoAnestesico;
	private int index;
	
	public ProcedimentoAnestesico() {
		super();
		index= 0;
		porcentagemProcedimentoAnestesico = BigDecimal.ZERO;
	}
	
	public ProcedimentoAnestesico(UsuarioInterface usuario) {
		super(usuario);
		porcentagemProcedimentoAnestesico = BigDecimal.ZERO;
	}

	public BigDecimal getPorcentagemProcedimentoAnestesico() {
		return porcentagemProcedimentoAnestesico;
	}

	public void setPorcentagemProcedimentoAnestesico(
			BigDecimal porcentagemProcedimentoAnestesico) {
		this.porcentagemProcedimentoAnestesico = porcentagemProcedimentoAnestesico;
	}

	/**
	 * O index é usado para diferenciar o hash dos ProcedimentoAnestesicos's durante sua criação no GeradorGuiaAcompanhamentoAnestesico.
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Retorna a porcetagem acrescida do caractere especial '%' 
	 * @return String
	 */
	public String getPorcentagemFormatada() {
		if(porcentagemProcedimentoAnestesico == null)
			return "";
		
		return porcentagemProcedimentoAnestesico.intValue() + "%";
	}
	
	public void calcularCampos(){
		super.calcularCampos();
		ProcedimentoUtils.calcularVideoEHorarioEspecial(this);
		
		boolean naoEZero = this.getValorCoParticipacao().compareTo(BigDecimal.ZERO) != 0;
		if (!ManagerGuia.isZeraCoparticipacaoDeAcordoComAGuiaOrigem(this.getGuia()) || naoEZero){
			this.calculaCoParticipacao();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProcedimentoAnestesico))
			return false;

		ProcedimentoAnestesico procedimento = (ProcedimentoAnestesico) obj;

		return new EqualsBuilder()
			.append(this.getIdProcedimento(), procedimento.getIdProcedimento())
			.append(this.getProcedimentoDaTabelaCBHPM(), procedimento.getProcedimentoDaTabelaCBHPM())
			.append(this.getGuia(), procedimento.getGuia())
			.append(this.getPorcentagem(), procedimento.getPorcentagem())
		    .append(this.getIndex(), procedimento.getIndex())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getIdProcedimento())
			.append(this.getProcedimentoDaTabelaCBHPM())
			.append(this.getGuia())
			.append(this.getPorcentagem())
			.append(this.getIndex())
			.toHashCode();
	}
	
}