package br.com.infowaypi.ecare.procedimentos;

import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgicoInterface;


@SuppressWarnings("serial")
public class ProcedimentoCirurgicoParaAuditoriaDeHonorarios extends ProcedimentoCirurgicoSR {

	private ProcedimentoCirurgicoInterface procedimentoOrigem;

	public ProcedimentoCirurgicoInterface getProcedimentoOrigem() {
		return procedimentoOrigem;
	}

	public void setProcedimentoOrigem(
			ProcedimentoCirurgicoInterface procedimentoOrigem) {
		this.procedimentoOrigem = procedimentoOrigem;
	}
	
}
