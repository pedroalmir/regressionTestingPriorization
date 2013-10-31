package br.com.infowaypi.ecarebc.atendimentos;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;

public class ResumoGuiasSolicitadas {

	private List<GuiaCompleta> gex;
	private List<GuiaCompleta> iel;
	private List<GuiaCompleta> iur;
	
	public ResumoGuiasSolicitadas(List<GuiaCompleta<ProcedimentoInterface>> list) {
		
		gex = new ArrayList<GuiaCompleta>();
		iel = new ArrayList<GuiaCompleta>();
		iur = new ArrayList<GuiaCompleta>();
		
		for (GuiaCompleta<ProcedimentoInterface> guia : list) {
			if (guia.getTipoDeGuia().equals("IUR")) {
				iur.add(guia);
			} else if (guia.getTipoDeGuia().equals("IEL")) {
				iel.add(guia);
			} else if (guia.getTipoDeGuia().equals("GEX")) {
				gex.add(guia);
			}
		}
	}

	public List<GuiaCompleta> getGex() {
		return gex;
	}

	public void setGex(List<GuiaCompleta> gex) {
		this.gex = gex;
	}

	public List<GuiaCompleta> getIel() {
		return iel;
	}

	public void setIel(List<GuiaCompleta> iel) {
		this.iel = iel;
	}

	public List<GuiaCompleta> getIur() {
		return iur;
	}

	public void setIur(List<GuiaCompleta> iur) {
		this.iur = iur;
	}

}
