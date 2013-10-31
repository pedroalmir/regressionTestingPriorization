package br.com.infowaypi.ecarebc.atendimentos.honorario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Porte;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

/**
 * @author Marcus bOolean/Josino Rodrigues.
 * </br>
 * Classe que representa um honorário médico no sistema.
 * Um honorário é único para um profissional, procedimento e função desempenhada
 * por esse profissional nesse procedimento.
 * @changes Patrícia Fontinele
 */
public abstract class Honorario extends ImplColecaoSituacoesComponent implements Serializable, Constantes, Cloneable {

	public static int CIRURGIAO = 0;
	public static int AUXILIAR_1 = 1;
	public static int AUXILIAR_2 = 2;
	public static int AUXILIAR_3 = 3;
	public static int ANESTESISTA = 4;
	public static int AUXILIAR_ANESTESISTA = 5;
	
	private static final long serialVersionUID = 1L;
	private Long idHonorario;
	private Profissional profissional;
	private ProcedimentoInterface procedimento;
	/**
	 * @see GrauDeParticipacaoEnum
	 */
	private int grauDeParticipacao;
	private BigDecimal valorTotal;
	
	private BigDecimal porcentagem = Constantes.PORCENTAGEM_100;
	private boolean incluiVideo;
	private boolean horarioEspecial;
	
	private Boolean glosado;
	private String motivoDeGlosa;
	
	public boolean transiente;
	
	private boolean auditado;
	
	public Honorario() {
		super();
		this.glosado = false;
	}
	
	public Honorario(UsuarioInterface usuario){
		this.mudarSituacao(usuario, SituacaoEnum.GERADO.descricao(), MotivoEnum.GERACAO_DE_HONORARIO.getMessage(), new Date());
	}
	
	public Profissional getProfissional() {
		return profissional;
	}
	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}
	public ProcedimentoInterface getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(ProcedimentoInterface procedimento) {
		this.procedimento = procedimento;
	}
	public String getGrauDeParticipacaoFormatado() {
		return GrauDeParticipacaoEnum.getEnum(grauDeParticipacao).getDescricao();
	}
	public int getGrauDeParticipacao() {
		return grauDeParticipacao;
	}
	public void setGrauDeParticipacao(int grauDeParticipacao) {
		this.grauDeParticipacao = grauDeParticipacao;
	}
	public Long getIdHonorario() {
		return idHonorario;
	}
	public void setIdHonorario(Long idHonorario) {
		this.idHonorario = idHonorario;
	}
	public BigDecimal getValorTotal() {
		if (valorTotal == null) {
			return BigDecimal.ZERO;
		}
		return MoneyCalculation.rounded(valorTotal);
	}
	
	public String getValorTotalFormatado() {
		return StringUtils.leftPad(new DecimalFormat("###,###,##0.00").format(getValorTotal().setScale(2,BigDecimal.ROUND_HALF_UP)), 9, " "); 
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public BigDecimal getPorcentagem() {
		return porcentagem;
	}
	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}
	public boolean isIncluiVideo() {
		return incluiVideo;
	}
	public void setIncluiVideo(boolean incluiVideo) {
		this.incluiVideo = incluiVideo;
	}
	public boolean isHorarioEspecial() {
		return horarioEspecial;
	}
	public void setHorarioEspecial(boolean horarioEspecial) {
		this.horarioEspecial = horarioEspecial;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Honorario)) {
			return false;
		}
		Honorario honorario = (Honorario) object;
		return new EqualsBuilder()
					.append(this.getProfissional(), honorario.getProfissional())
					.append(this.getProcedimento(), honorario.getProcedimento())
					.append(this.getGrauDeParticipacao(), honorario.getGrauDeParticipacao())
					.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(1308791639, 309108973)
								.append(this.profissional)
								.append(this.procedimento)
								.append(this.grauDeParticipacao)
								.toHashCode();
	}
	
	public boolean isHonorarioAnestesista() {
		if (this.grauDeParticipacao == GrauDeParticipacaoEnum.ANESTESISTA.getCodigo() || this.grauDeParticipacao == GrauDeParticipacaoEnum.AUXILIAR_ANESTESISTA .getCodigo()) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isHonorarioCirurgiao() {
		if (this.grauDeParticipacao == GrauDeParticipacaoEnum.RESPONSAVEL.getCodigo()) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * @see {@link Procedimento#atualizaValorAnestesista()}, {@link Procedimento#getValorAuxiliarAnestesista()}
	 * 
	 */
	public void recalculaValorTotal() {
		BigDecimal valorHonorarioRecalculado 	= BigDecimal.ZERO;
			
		GrauDeParticipacaoEnum grauEnum = GrauDeParticipacaoEnum.getEnum(this.getGrauDeParticipacao());
		
		if (grauEnum.equals(GrauDeParticipacaoEnum.RESPONSAVEL)) {
			BigDecimal porcentagem = this.getPorcentagem().divide(new BigDecimal(100)); 
			BigDecimal valorDoProcedimento100PorCento = this.getProcedimento().getValorDoProcedimento100PorCento();
			valorHonorarioRecalculado = MoneyCalculation.multipla(valorDoProcedimento100PorCento, porcentagem);
		} else {
			//AKI N LEVA EM CONTA A PORCENTAGEM informada no fluxo, ms sim do proc da guia origem ou do honorario do responsavel
			valorHonorarioRecalculado = grauEnum.getValorHonorario(procedimento);
		}
		
		TabelaCBHPM procedimentoDaTabelaCBHPM = this.getProcedimento().getProcedimentoDaTabelaCBHPM();
		Porte porteAnestesico = procedimentoDaTabelaCBHPM.getPorteAnestesico();
		
		if(this.isHonorarioAnestesista() && porteAnestesico != null) {
			BigDecimal valorAnestesista = BigDecimal.ZERO;
			
			if (grauEnum.equals(GrauDeParticipacaoEnum.ANESTESISTA)){
				BigDecimal valorPorte = new BigDecimal(porteAnestesico.getValorPorte());
				BigDecimal porcentagem = this.getPorcentagem().divide(new BigDecimal(100));
				
				valorPorte = valorPorte.multiply(porcentagem);
				
				if(incluiVideo) {
					valorPorte = valorPorte.multiply(new BigDecimal(1.5));
				}
				
				if(horarioEspecial) {
					valorPorte = valorPorte.multiply(new BigDecimal(1.3));
				}
				valorAnestesista =  MoneyCalculation.rounded(valorPorte);
			} else {
				valorAnestesista = grauEnum.getValorHonorario(procedimento);
			}
			
			valorHonorarioRecalculado = valorAnestesista;
		}
		
		this.setValorTotal(valorHonorarioRecalculado);
	}

	public String getPorcentagemFormatada() {
		return this.porcentagem.setScale(0) + "%";
	}
	
	/**
	 * Cria um honorário externo, na situação GERADO
	 * 
	 * @param grauDeParticipacao
	 * @param horarioEspecial
	 * @param incluiVideo
	 * @param porcentagem
	 * @param procedimento
	 * @param profissional
	 * @param guiaHonorario
	 * @param usuario
	 * @return
	 */
	public static HonorarioExterno criarHonorarioExterno(int grauDeParticipacao, boolean horarioEspecial, boolean incluiVideo, BigDecimal porcentagem, ProcedimentoInterface procedimento, Profissional profissional, GuiaHonorarioMedico guiaHonorario, UsuarioInterface usuario) {
		HonorarioExterno honorario = new HonorarioExterno();
		
		honorario.setGrauDeParticipacao(grauDeParticipacao);
		honorario.setHorarioEspecial(horarioEspecial);
		honorario.setIncluiVideo(incluiVideo);
		honorario.setPorcentagem(porcentagem);
		honorario.setProcedimento(procedimento);
		honorario.setProfissional(profissional);
		honorario.setGuiaHonorario(guiaHonorario);
		honorario.mudarSituacao(usuario, SituacaoEnum.GERADO.descricao(), MotivoEnum.GERACAO_DE_HONORARIO.getMessage(), new Date());
		
		honorario.recalculaValorTotal();
		
		return honorario;
	}
	
	public abstract boolean isHonorarioExterno();
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public boolean isHonorarioPacote() {
		try {
			HonorarioExterno he = (HonorarioExterno) this;
			if (he.getItemPacoteHonorario() != null){
				return true;
			}
		} catch (ClassCastException e) {}
		return false;
	}

	public boolean isAuditado() {
		return auditado;
	}

	public void setAuditado(boolean auditado) {
		this.auditado = auditado;
	}

	public String getMotivoDeGlosa() {
		return motivoDeGlosa;
	}

	public void setMotivoDeGlosa(String motivoDeGlosa) {
		this.motivoDeGlosa = motivoDeGlosa;
	}

	public Boolean getGlosado() {
		return glosado;
	}

	public void setGlosado(Boolean glosado) {
		this.glosado = glosado;
	}
}
