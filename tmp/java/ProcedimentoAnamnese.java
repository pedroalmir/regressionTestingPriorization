package br.com.infowaypi.ecarebc.procedimentos;

import java.util.Date;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.QuadroClinico;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;


public class ProcedimentoAnamnese {

	private GuiaSimples guia;
	private TabelaCBHPM procedimentoDaTabelaCBHPM;
	private String autorizadoString;
	private String motivoRegulacao;
	private Date dataRealizacao;
	private GuiaCompleta<ProcedimentoInterface> guiaC;
	
	public ProcedimentoAnamnese(Procedimento p) {
		this.guia = p.getGuia();
		this.procedimentoDaTabelaCBHPM = p.getProcedimentoDaTabelaCBHPM();
		this.autorizadoString = getAutorizadoString(p);
		this.motivoRegulacao = getMotivoRegulacao(p);
		this.dataRealizacao = p.getSituacao(SituacaoEnum.REALIZADO.descricao()) != null ? p.getSituacao(SituacaoEnum.REALIZADO.descricao()).getDataSituacao() : null;

		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", guia.getAutorizacao()));
		guiaC = sa.uniqueResult(GuiaCompleta.class);
	}

	private String getAutorizadoString(Procedimento p) {
		if (p.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null) {
			return "Sim";
		} else if (p.getSituacao(SituacaoEnum.NAO_AUTORIZADO.descricao()) != null) {
			return "Não";
		}
		return null;
	}
	
	private String getMotivoRegulacao(Procedimento p) {
		if (this.getAutorizadoString(p).equals("Sim")) {
			return p.getSituacao(SituacaoEnum.AUTORIZADO.descricao()).getMotivo();
		} else if (getAutorizadoString(p).equals("Não")) {
			return p.getSituacao(SituacaoEnum.NAO_AUTORIZADO.descricao()).getMotivo();
		}
		return null;
	}

	public String getCids() {
		String retorno = "";
		try {
			Set<CID> cids = guiaC.getCids();
			for (CID cid : cids) {
				if (cids.size() == 1) {
					retorno = cid.getDescricaoDaDoenca();
				} else {
					retorno = retorno + "<br /> - " + cid.getDescricaoDaDoenca();
				}
			}
		} catch (Exception e ) {
			e.printStackTrace();
		}
		return retorno;
	}
	
	public String getQuadrosClinicos() {
		String retorno = "";
		try {
			List<QuadroClinico> quadros = guiaC.getQuadrosClinicos();
			for (QuadroClinico quadro : quadros) {
				if(quadros.size() == 1) {
					retorno = quadro.getJustificativa();
				} else {
					retorno = retorno + "<br /> - " + quadro.getJustificativa();
				}
			}
		} catch (Exception e) {
			
		}
		return retorno;
	}
	
	public GuiaSimples getGuia() {
		return guia;
	}

	public void setGuia(GuiaSimples guia) {
		this.guia = guia;
	}

	public TabelaCBHPM getProcedimentoDaTabelaCBHPM() {
		return procedimentoDaTabelaCBHPM;
	}

	public void setProcedimentoDaTabelaCBHPM(TabelaCBHPM procedimentoDaTabelaCBHPM) {
		this.procedimentoDaTabelaCBHPM = procedimentoDaTabelaCBHPM;
	}

	public String getAutorizadoString() {
		return autorizadoString;
	}

	public void setAutorizadoString(String autorizadoString) {
		this.autorizadoString = autorizadoString;
	}

	public String getMotivoRegulacao() {
		return motivoRegulacao;
	}

	public void setMotivoRegulacao(String motivoRegulacao) {
		this.motivoRegulacao = motivoRegulacao;
	}

	public Date getDataRealizacao() {
		return dataRealizacao;
	}

	public void setDataRealizacao(Date dataRealizacao) {
		this.dataRealizacao = dataRealizacao;
	}

}
