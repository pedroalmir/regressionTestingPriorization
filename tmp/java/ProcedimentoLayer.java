package br.com.infowaypi.ecarebc.procedimentos;


import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecarebc.atendimentos.ItemGlosavelLayer;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;

/**
 * Classe que representa uma Layer da classe Procedimento
 * @author Silvano
 */
public class ProcedimentoLayer extends ImplColecaoSituacoesComponent implements ItemGlosavelLayer{

	private static final long serialVersionUID = 1L;

	private Long idProcedimentoLayer;
	private TabelaCBHPM procedimentoDaTabelaCBHPM;
	private boolean bilateralProcedimentoLayer;
	private Integer quantidadeProcedimentoLayer;
	private ProcedimentoInterface procedimento;
	private MotivoGlosa motivoGlosa;
	private boolean glosar;

	private String justificativaGlosa;
	
	public ProcedimentoLayer(ProcedimentoInterface p) {
		this.procedimento = p;
		this.procedimentoDaTabelaCBHPM = p.getProcedimentoDaTabelaCBHPM();
		this.bilateralProcedimentoLayer = p.getBilateral();
		this.quantidadeProcedimentoLayer = p.getQuantidade();
		this.glosar = p.isGlosar();
		this.justificativaGlosa = p.getJustificativaGlosa();
	}

	public boolean getBilateralProcedimentoLayer() {
		return bilateralProcedimentoLayer;
	}
	
	public void setBilateralProcedimentoLayer(boolean bilateralProcedimentoLayer) {
		this.bilateralProcedimentoLayer = bilateralProcedimentoLayer;
	}
	
	public TabelaCBHPM getProcedimentoDaTabelaCBHPM() {
		return procedimentoDaTabelaCBHPM;
	}
	
	public void setProcedimentoDaTabelaCBHPM(TabelaCBHPM procedimentoDaTabelaCBHPM) {
		this.procedimentoDaTabelaCBHPM = procedimentoDaTabelaCBHPM;
	}

	public Integer getQuantidadeProcedimentoLayer() {
		return quantidadeProcedimentoLayer;
	}
	public void setQuantidadeProcedimentoLayer(Integer quantidadeProcedimentoLayer) {
		this.quantidadeProcedimentoLayer = quantidadeProcedimentoLayer;
	}

	public Long getIdProcedimentoLayer() {
		return idProcedimentoLayer;
	}

	public void setIdProcedimentoLayer(Long idProcedimentoLayer) {
		this.idProcedimentoLayer = idProcedimentoLayer;
	}

	public ProcedimentoInterface getProcedimento() {
		return procedimento;
	}

	public void setProcedimentoInterface(ProcedimentoInterface procedimento) {
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
}