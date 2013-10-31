package br.com.infowaypi.ecare.relatorio;

import br.com.infowaypi.ecare.resumos.ResumoProrrogacoes;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Assert;

public class RelatorioProrrogacoes {

	public ResumoProrrogacoes buscarProrrogacoes(String autorizacao) {
		GuiaInternacao guia = buscarGuia(autorizacao);
		return new ResumoProrrogacoes(guia);
	}

	private GuiaInternacao buscarGuia(String autorizacao) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("autorizacao", autorizacao));
		GuiaInternacao guia = sa.uniqueResult(GuiaInternacao.class);
		
		Assert.isNotNull(guia, "Não existe nenhuma guia com essa autorização.");
		return guia;
	}
	
}
