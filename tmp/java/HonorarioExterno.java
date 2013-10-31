package br.com.infowaypi.ecarebc.atendimentos.honorario;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacoteHonorario;
import br.com.infowaypi.ecarebc.utils.SituacaoUtils;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

@SuppressWarnings("serial")
public class HonorarioExterno extends Honorario {

	private GuiaHonorarioMedico guiaHonorario;
	
	/**
	 * Pacote de honorário. 
	 * Abordagem inserida no SR com o credenciamento de cooperativas cardiologicas em 02/2011
	 */
	private ItemPacoteHonorario itemPacoteHonorario;

	/**
	 * Atributo Transiente para auditoria de honorário de pacote
	 */
	private transient boolean glosar;
	private transient String motivoGlosaTransiente;
	
	public HonorarioExterno(){
		super();
	}
	
	public HonorarioExterno(UsuarioInterface usuario){
		super(usuario);
	}
	
	public GuiaHonorarioMedico getGuiaHonorario() {
		return guiaHonorario;
	}
	public void setGuiaHonorario(GuiaHonorarioMedico guiaHonorario) {
		this.guiaHonorario = guiaHonorario;
	}

	public ItemPacoteHonorario getItemPacoteHonorario() {
		return itemPacoteHonorario;
	}

	public void setItemPacoteHonorario(ItemPacoteHonorario itemPacoteHonorario) {
		this.itemPacoteHonorario = itemPacoteHonorario;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Honorario)) {
			return false;
		}
		
		HonorarioExterno honorario = (HonorarioExterno) object;
		EqualsBuilder equalsBuilder = new EqualsBuilder().append(this.getProfissional(), honorario.getProfissional())
														.append(this.getProcedimento(), honorario.getProcedimento())
														.append(this.getGrauDeParticipacao(), honorario.getGrauDeParticipacao())
														.append(this.getSituacao(), honorario.getSituacao());
		
		if (this.getGuiaHonorario() != null) {
			equalsBuilder.append(this.getGuiaHonorario().getAutorizacao(), honorario.getGuiaHonorario().getAutorizacao());
		}
		
		return equalsBuilder.isEquals();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(1308791639, 309108973)
									.append(this.getProfissional())
									.append(this.getProcedimento())
									.append(this.getGrauDeParticipacao())
									.append(this.getSituacao());
		
		if (this.getGuiaHonorario() != null) {
			hcb.append(this.getGuiaHonorario().getAutorizacao());
		}
		
		return hcb.toHashCode();
	}
	
	@Override
	public boolean isHonorarioExterno() {
		return true;
	}
	
	public void glosar(UsuarioInterface usuario, Date date) {
		this.guiaHonorario.glosar(this, usuario, date);
	}
	
	public void glosar(UsuarioInterface usuario, Date date, String motivo) {
		this.setMotivoDeGlosa(motivo);
		this.guiaHonorario.glosar(this, usuario, date, motivo);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		HonorarioExterno clone = new HonorarioExterno();
		
		clone.setColecaoSituacoes(SituacaoUtils.clone(this.getColecaoSituacoes()));
		clone.setGrauDeParticipacao(this.getGrauDeParticipacao());
		clone.setMotivoDeGlosa(this.getMotivoDeGlosa());
		clone.setPorcentagem(this.getPorcentagem());
		clone.setProfissional(this.getProfissional());
		clone.setValorTotal(this.getValorTotal());
		clone.setSituacao(this.getSituacao());
		
		return clone;
	}
	
	public void recalculaValorTotal() {
		if (this.isHonorarioPacote()){
			recalculaValorTotalHonorarioPacote();
		} else {
			super.recalculaValorTotal();
		}
	}
	
	private void recalculaValorTotalHonorarioPacote() {
		BigDecimal valorHonorarioRecalculado = BigDecimal.ZERO;
		BigDecimal porcentagem = this.getPorcentagem().divide(new BigDecimal(100)); 
		BigDecimal valorDoPacote100PorCento = this.getItemPacoteHonorario().getValorAcordo();
		valorHonorarioRecalculado = MoneyCalculation.multipla(valorDoPacote100PorCento, porcentagem);
		this.setValorTotal(valorHonorarioRecalculado);
	}

	public boolean isGlosar() {
		return glosar;
	}

	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
	}

	public String getMotivoGlosaTransiente() {
		return motivoGlosaTransiente;
	}

	public void setMotivoGlosaTransiente(String motivoGlosaTransiente) {
		this.motivoGlosaTransiente = motivoGlosaTransiente;
	}

}
