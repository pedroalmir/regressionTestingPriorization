package br.com.infowaypi.ecarebc.procedimentos;

import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavel;
import br.com.infowaypi.ecarebc.atendimentos.honorario.Honorario;
import br.com.infowaypi.ecarebc.atendimentos.visitors.ItemGlosavelVisitor;
import br.com.infowaypi.ecarebc.utils.SituacaoUtils;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * @author Marcus bOolean
 * @changes Luciano Rocha: Reformulação da auditoria.
 */
public class ProcedimentoOutros extends Procedimento implements ItemGlosavel {

	private static final long serialVersionUID = 1L;
	private static String VISITA_HOSPITALAR_CREDENCIADO = "91001021";
	private static String VISITA_HOSPITALAR_NAO_CREDENCIADO = "10102019";
	
	private transient boolean isVisitaInseridaAutomaticamente;
	
	private ItemGlosavel itemGlosavelAnterior;
	
	public ProcedimentoOutros() {
		this(null);
	}
	
	public ProcedimentoOutros(UsuarioInterface usuario){
		super(usuario);
	}
	
	/**
	 * método utilizado nos relatórios de faturamento com competencia anterior ou igual a 04/2010.
	 * @return
	 */
	public BigDecimal getValorProfissionalResponsavelLegado() {
		return MoneyCalculation.rounded(this.getProcedimentoDaTabelaCBHPM().getValorModerado().multiply(new BigDecimal(this.getQuantidade())));
	}
	
	@Override
	public BigDecimal getValorProfissionalResponsavel() {
		return MoneyCalculation.rounded(this.getValorAtualDoProcedimento().multiply(new BigDecimal(this.getQuantidade())));
	}
	
	@Override
	public void tocarObjetos() {
		super.tocarObjetos();
		if (this.getProfissionalResponsavel() != null) {
			getProfissionalResponsavel().tocarObjetos();
		}
	}
	
	@Override
	public int getTipoProcedimento() {
		return PROCEDIMENTO_OUTROS;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProcedimentoOutros)){
			return false;
		}
		
		if (this.isPermiteHonorarioClinico()) {
			return false;
		}
		
//		ProcedimentoOutros procedimento = (ProcedimentoOutros) obj;
//		return new EqualsBuilder()
//		.append(this.getGuia(), procedimento.getGuia())
//		.append(this.getProcedimentoDaTabelaCBHPM(), procedimento.getProcedimentoDaTabelaCBHPM())
//		.append(this.getProfissionalResponsavel(), procedimento.getProfissionalResponsavel())
//		.append(this.getQuantidade(), procedimento.getQuantidade())
//		.append(this.getSituacao().getDescricao(), procedimento.getSituacao().getDescricao())
//		.isEquals();
		/**
		 * está retorna apenas false para permitir inserção de mais de um honorário clínico
		 * no fechar guia.
		 * @author Luciano Rocha
		 * @since 01/02/2013
		 */
		return false;
	}
	
	@Override
	public Boolean validate(UsuarioInterface usuario) throws Exception {
		boolean isProfissionalCredenciado = false;
		if(this.getProfissionalResponsavel() != null) {
			isProfissionalCredenciado = this.getProfissionalResponsavel().isCredenciado();
		}
//		boolean isProcedimentoVisitaCredenciado = this.getProcedimentoDaTabelaCBHPM().getCodigo().equals(VISITA_HOSPITALAR_CREDENCIADO);
//		boolean isProcedimentoVisitaNaoCredenciado = this.getProcedimentoDaTabelaCBHPM().getCodigo().equals(VISITA_HOSPITALAR_NAO_CREDENCIADO);
		
		return super.validate(usuario);
	}
	
	@Override
	public ItemGlosavel clone() {
		ProcedimentoOutros clone = (ProcedimentoOutros) newInstanceOthers();

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
		clone.setMotivoGlosaProcedimento(getMotivoGlosaProcedimento());
		clone.setPorcentagem(this.getPorcentagem());
		clone.setBilateral(this.getBilateral());
		clone.setHorarioEspecial(this.isHorarioEspecial());
		clone.setProcedimentoDaTabelaCBHPM(this.getProcedimentoDaTabelaCBHPM());
		clone.setColecaoSituacoes(SituacaoUtils.clone(this.getColecaoSituacoes()));
		clone.setSituacao(this.getSituacao());
		clone.setQuantidade(this.getQuantidade());
		clone.setDataRealizacao(this.getDataRealizacao());
		clone.setValorAtualDaModeracao(this.getValorAtualDaModeracao());
		clone.setValorAtualDoProcedimento(this.getValorAtualDoProcedimento());
		clone.setValorCoParticipacao(this.getValorCoParticipacao());
		
		return clone;
	}
	
	@Override
	public void setItemGlosavelAnterior(ItemGlosavel anterior) {
		itemGlosavelAnterior = anterior;
	}

	@Override
	public ItemGlosavel getItemGlosavelAnterior() {
		return itemGlosavelAnterior;
	}

	@Override
	public ItemGlosavel accept(ItemGlosavelVisitor visitor) {
		return visitor.visit(this);
	}

	public boolean isVisitaInseridaAutomaticamente() {
		return isVisitaInseridaAutomaticamente;
	}

	public void setVisitaInseridaAutomaticamente(
			boolean isVisitaInseridaAutomaticamente) {
		this.isVisitaInseridaAutomaticamente = isVisitaInseridaAutomaticamente;
	}	
	
}
