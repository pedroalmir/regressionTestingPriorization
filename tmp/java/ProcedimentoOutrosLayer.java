package br.com.infowaypi.ecarebc.procedimentos;


import java.util.Date;

import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavelLayer;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;

/**
 * Classe que representa uma Layer da classe Procedimento
 * @author Silvano
 * @changes Luciano Rocha: reformulação da auditoria.
 */
public class ProcedimentoOutrosLayer extends ImplColecaoSituacoesComponent implements ItemGlosavelLayer {

	private static final long serialVersionUID = 1L;

	private Long idProcedimentoOutrosLayer;
	private TabelaCBHPM procedimentoOutrosDaTabelaCBHPM;
	private Profissional profissionalResponsavelOutros;
	private ProcedimentoInterface procedimentoOutros;
	private MotivoGlosa motivoGlosa;
	private boolean glosar;
	private String justificativaGlosa;
	
	private Date dataRealizacaoOutros;
	private Integer quantidadeOutros;

	public ProcedimentoOutrosLayer(ProcedimentoInterface p) {
		this.procedimentoOutros = p;
		this.procedimentoOutrosDaTabelaCBHPM = p.getProcedimentoDaTabelaCBHPM();
		this.profissionalResponsavelOutros = p.getProfissionalResponsavel();
		this.quantidadeOutros = p.getQuantidade();
		this.glosar = p.isGlosar();
		this.dataRealizacaoOutros = ((Procedimento) p).getDataRealizacao();
	}

	public Long getIdProcedimentoOutrosLayer() {
		return idProcedimentoOutrosLayer;
	}

	public void setIdProcedimentoOutrosLayer(Long idProcedimentoOutrosLayer) {
		this.idProcedimentoOutrosLayer = idProcedimentoOutrosLayer;
	}

	public TabelaCBHPM getProcedimentoOutrosDaTabelaCBHPM() {
		return procedimentoOutrosDaTabelaCBHPM;
	}

	public void setProcedimentoOutrosDaTabelaCBHPM(
			TabelaCBHPM procedimentoOutrosDaTabelaCBHPM) {
		this.procedimentoOutrosDaTabelaCBHPM = procedimentoOutrosDaTabelaCBHPM;
	}

	public ProcedimentoInterface getProcedimentoOutros() {
		return procedimentoOutros;
	}

	public void setProcedimentoOutros(ProcedimentoInterface procedimentoOutros) {
		this.procedimentoOutros = procedimentoOutros;
	}
	
	public Profissional getProfissionalResponsavelOutros() {
		return profissionalResponsavelOutros;
	}

	public void setProfissionalResponsavelOutros(
			Profissional profissionalResponsavelOutros) {
		this.profissionalResponsavelOutros = profissionalResponsavelOutros;
	}
	
	@Override
	public MotivoGlosa getMotivoGlosa() {
		return motivoGlosa;
	}
	
	@Override
	public void setMotivoGlosa(MotivoGlosa motivoGlosa) {
		this.motivoGlosa = motivoGlosa;
	}

	@Override
	public boolean isGlosar() {
		return glosar;
	}

	@Override
	public void setGlosar(boolean glosar) {
		this.glosar = glosar;
	}

	@Override
	public void setJustificativaGlosa(String justificativaGlosa) {
		this.justificativaGlosa = justificativaGlosa;
	}

	@Override
	public String getJustificativaGlosa() {
		return justificativaGlosa;
	}

	public Date getDataRealizacaoOutros() {
		return dataRealizacaoOutros;
	}

	public void setDataRealizacaoOutros(Date dataRealizacaoOutros) {
		this.dataRealizacaoOutros = dataRealizacaoOutros;
	}

	public Integer getQuantidadeOutros() {
		return quantidadeOutros;
	}

	public void setQuantidadeOutros(Integer quantidadeOutros) {
		this.quantidadeOutros = quantidadeOutros;
	}
}
