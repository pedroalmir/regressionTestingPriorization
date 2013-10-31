package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavelLayer;
import br.com.infowaypi.ecarebc.enums.GrauDeParticipacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;

/**
 * Classe que representa uma camada intermediária entre os procedimentos e as guias;
 * @author Eduardo Vera Sousa
 *
 */

public class ProcedimentoCirurgicoLayer implements ItemGlosavelLayer{

	private Long idProcedimento;
	private Profissional profissionalResponsavelTemp;
	private Profissional profissionalAuxiliar1;
	private Profissional profissionalAuxiliar2;
	private Profissional profissionalAuxiliar3;
	private Date dataRealizacao;
	private BigDecimal porcentagem;
	private MotivoGlosa motivoGlosaProcedimento;
	private boolean glosar;	
	private ProcedimentoCirurgico procedimentoCirurgico;
	private BigDecimal valor;
	private boolean glosarAuxiliar1;
	private boolean glosarAuxiliar2;
	private boolean glosarAuxiliar3;
	private String motivoGlosaAuxiliar1;
	private String motivoGlosaAuxiliar2;
	private String motivoGlosaAuxiliar3;
	private BigDecimal valorTotalAuxiliar1;
	private BigDecimal valorTotalAuxiliar2;
	private BigDecimal valorTotalAuxiliar3;
	
	private String justificativaGlosa;
	
	/**
	 * Método que gera as camadas entre os procedimentos e as guias
	 * @param br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface 
	 * @return br.com.infowaypi.ecare.services.LayerProcedimento
	 */
	public ProcedimentoCirurgicoLayer(ProcedimentoCirurgico procedimento) {
		this.procedimentoCirurgico = procedimento;
		this.idProcedimento = procedimento.getIdProcedimento();
		procedimento.getGuia();
		this.profissionalResponsavelTemp = procedimento.getProfissionalResponsavel();
		this.profissionalAuxiliar1 = procedimento.getProfissionalAuxiliar1();
		this.profissionalAuxiliar2 = procedimento.getProfissionalAuxiliar2();
		this.profissionalAuxiliar3 = procedimento.getProfissionalAuxiliar3();
		this.porcentagem = procedimento.getPorcentagem();
		
		this.valor = procedimento.getValorCalculadoProcedimento();
		this.dataRealizacao = procedimento.getDataRealizacao();
		
		if (procedimento.getHonorarioInterno(GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR.getCodigo())!=null)
			this.valorTotalAuxiliar1 = procedimento.getHonorarioInterno(GrauDeParticipacaoEnum.PRIMEIRO_AUXILIAR.getCodigo()).getValorTotal();
		if (procedimento.getHonorarioInterno(GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR.getCodigo())!=null)
			this.valorTotalAuxiliar2 = procedimento.getHonorarioInterno(GrauDeParticipacaoEnum.SEGUNDO_AUXILIAR.getCodigo()).getValorTotal();
		if (procedimento.getHonorarioInterno(GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR.getCodigo())!=null)
			this.valorTotalAuxiliar3 = procedimento.getHonorarioInterno(GrauDeParticipacaoEnum.TERCEIRO_AUXILIAR.getCodigo()).getValorTotal();
	}

	public ProcedimentoCirurgico getProcedimentoCirurgicoNovo() {
		ProcedimentoCirurgico procedimento = (ProcedimentoCirurgico) procedimentoCirurgico.clone();
		procedimento.setProfissionalResponsavelTemp(profissionalResponsavelTemp);
		procedimento.setProfissionalAuxiliar1(profissionalAuxiliar1);
		procedimento.setProfissionalAuxiliar2(profissionalAuxiliar2);
		procedimento.setProfissionalAuxiliar3(profissionalAuxiliar3);
		procedimento.setPorcentagem(porcentagem);
		procedimento.setMotivoGlosaProcedimento(motivoGlosaProcedimento);
		procedimento.setDataRealizacao(this.dataRealizacao);

		return procedimento;
	}
	
	public Profissional getProfissionalResponsavelTemp() {
		return profissionalResponsavelTemp;
	}

	public void setProfissionalResponsavelTemp(
			Profissional profissionalResponsavelTemp) {
		this.profissionalResponsavelTemp = profissionalResponsavelTemp;
	}

	public Profissional getProfissionalAuxiliar1() {
		return profissionalAuxiliar1;
	}

	public void setProfissionalAuxiliar1(Profissional profissionalAuxiliar1) {
		this.profissionalAuxiliar1 = profissionalAuxiliar1;
	}

	public Profissional getProfissionalAuxiliar2() {
		return profissionalAuxiliar2;
	}

	public void setProfissionalAuxiliar2(Profissional profissionalAuxiliar2) {
		this.profissionalAuxiliar2 = profissionalAuxiliar2;
	}

	public Profissional getProfissionalAuxiliar3() {
		return profissionalAuxiliar3;
	}

	public void setProfissionalAuxiliar3(Profissional profissionalAuxiliar3) {
		this.profissionalAuxiliar3 = profissionalAuxiliar3;
	}

	public Date getDataRealizacao() {
		return dataRealizacao;
	}

	public void setDataRealizacao(Date dataRealizacao) {
		this.dataRealizacao = dataRealizacao;
	}

	public BigDecimal getPorcentagem() {
		return porcentagem;
	}

	public void setPorcentagem(BigDecimal porcentagem) {
		this.porcentagem = porcentagem;
	}

	public void setGlosar(Boolean glosar) {
		this.glosar = glosar;
	}

	public Long getIdProcedimento() {
		return idProcedimento;
	}

	public void setIdProcedimento(Long idProcedimento) {
		this.idProcedimento = idProcedimento;
	}

	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
	}

	public boolean isGlosar() {
		return glosar;
	}

	public ProcedimentoCirurgico getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(ProcedimentoCirurgico procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public MotivoGlosa getMotivoGlosaProcedimento() {
		return motivoGlosaProcedimento;
	}
	
	public void setMotivoGlosaProcedimento(MotivoGlosa motivoGlosaProcedimento) {
		this.motivoGlosaProcedimento = motivoGlosaProcedimento;
	}
	
	@Override
	public void setMotivoGlosa(MotivoGlosa motivoGlosaProcedimento) {
		this.motivoGlosaProcedimento = motivoGlosaProcedimento;
	}

	@Override
	public MotivoGlosa getMotivoGlosa() {
		return this.motivoGlosaProcedimento;
	}

	public boolean isGlosarAuxiliar1() {
		return glosarAuxiliar1;
	}

	public void setGlosarAuxiliar1(boolean glosarAuxiliar1) {
		this.glosarAuxiliar1 = glosarAuxiliar1;
	}

	public boolean isGlosarAuxiliar2() {
		return glosarAuxiliar2;
	}

	public void setGlosarAuxiliar2(boolean glosarAuxiliar2) {
		this.glosarAuxiliar2 = glosarAuxiliar2;
	}

	public boolean isGlosarAuxiliar3() {
		return glosarAuxiliar3;
	}

	public void setGlosarAuxiliar3(boolean glosarAuxiliar3) {
		this.glosarAuxiliar3 = glosarAuxiliar3;
	}

	public String getMotivoGlosaAuxiliar1() {
		return motivoGlosaAuxiliar1;
	}

	public void setMotivoGlosaAuxiliar1(String motivoGlosaAuxiliar1) {
		this.motivoGlosaAuxiliar1 = motivoGlosaAuxiliar1;
	}

	public String getMotivoGlosaAuxiliar2() {
		return motivoGlosaAuxiliar2;
	}

	public void setMotivoGlosaAuxiliar2(String motivoGlosaAuxiliar2) {
		this.motivoGlosaAuxiliar2 = motivoGlosaAuxiliar2;
	}

	public String getMotivoGlosaAuxiliar3() {
		return motivoGlosaAuxiliar3;
	}

	public void setMotivoGlosaAuxiliar3(String motivoGlosaAuxiliar3) {
		this.motivoGlosaAuxiliar3 = motivoGlosaAuxiliar3;
	}

	public BigDecimal getValorTotalAuxiliar1() {
		return valorTotalAuxiliar1;
	}

	public void setValorTotalAuxiliar1(BigDecimal valorTotalAuxiliar1) {
		this.valorTotalAuxiliar1 = valorTotalAuxiliar1;
	}

	public BigDecimal getValorTotalAuxiliar2() {
		return valorTotalAuxiliar2;
	}

	public void setValorTotalAuxiliar2(BigDecimal valorTotalAuxiliar2) {
		this.valorTotalAuxiliar2 = valorTotalAuxiliar2;
	}

	public BigDecimal getValorTotalAuxiliar3() {
		return valorTotalAuxiliar3;
	}

	public void setValorTotalAuxiliar3(BigDecimal valorTotalAuxiliar3) {
		this.valorTotalAuxiliar3 = valorTotalAuxiliar3;
	}

	@Override
	public void setJustificativaGlosa(String justificativaGlosa) {
		this.justificativaGlosa = justificativaGlosa;
	}

	@Override
	public String getJustificativaGlosa() {
		return justificativaGlosa;
	}
}