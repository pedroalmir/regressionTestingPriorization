package br.com.infowaypi.ecarebc.procedimentos;


import java.math.BigDecimal;
import java.util.Date;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavelLayer;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;

/**
 * Classe que representa uma Layer da classe Procedimento
 * @author Silvano
 */
public class ProcedimentoHonorarioLayer extends ImplColecaoSituacoesComponent implements ItemGlosavelLayer{

	private static final long serialVersionUID = 1L;

	private Long idProcedimentoLayer;
	private TabelaCBHPM procedimentoDaTabelaCBHPM;
	private ProcedimentoHonorario procedimento;
	private MotivoGlosa motivoGlosa;
	private Profissional profissional;
	private boolean auditado;
	private String motivo;
	private Date dataRealizacao;
	private boolean glosar;
	private Integer quantidade;

	private String justificativaGlosa;
	
	public ProcedimentoHonorarioLayer(ProcedimentoHonorario p) {
		this.procedimento = p;
		this.profissional = p.getProfissionalResponsavel();
		this.procedimentoDaTabelaCBHPM = p.getProcedimentoDaTabelaCBHPM();
		this.glosar = p.isGlosar();
		this.dataRealizacao = p.getDataRealizacao();
		this.quantidade = p.getQuantidade();
		
	}

	public TabelaCBHPM getProcedimentoDaTabelaCBHPM() {
		return procedimentoDaTabelaCBHPM;
	}
	
	public void setProcedimentoDaTabelaCBHPM(TabelaCBHPM procedimentoDaTabelaCBHPM) {
		this.procedimentoDaTabelaCBHPM = procedimentoDaTabelaCBHPM;
	}

	public Long getIdProcedimentoLayer() {
		return idProcedimentoLayer;
	}

	public void setIdProcedimentoLayer(Long idProcedimentoLayer) {
		this.idProcedimentoLayer = idProcedimentoLayer;
	}

	public ProcedimentoHonorario getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(ProcedimentoHonorario procedimento) {
		this.procedimento = procedimento;
	}

	@Override
	public void setMotivoGlosa(MotivoGlosa motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}

	@Override
	public MotivoGlosa getMotivoGlosa() {
		return motivoGlosa;
	}
		
	@Override
	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
	}

	@Override
	public boolean isGlosar() {
		return glosar;
	}

	@Override
	public void setJustificativaGlosa(String justificativaGlosa) {
		this.justificativaGlosa = justificativaGlosa;
	}

	@Override
	public String getJustificativaGlosa() {
		return justificativaGlosa;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public boolean isAuditado() {
		return auditado;
	}

	public void setAuditado(boolean auditado) {
		this.auditado = auditado;
	}

	public Profissional getProfissional() {
		return profissional;
	}

	public void setProfissional(Profissional profissional) {
		this.profissional = profissional;
	}

	public Date getDataRealizacao() {
		return dataRealizacao;
	}

	public void setDataRealizacao(Date dataRealizacao) {
		this.dataRealizacao = dataRealizacao;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
}