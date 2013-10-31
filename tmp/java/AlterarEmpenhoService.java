package br.com.infowaypi.ecare.services.suporte;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoFaturamentos;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class AlterarEmpenhoService {

public ResumoFaturamentos buscarFaturamento(String numeroEmpenho, String competencia, Prestador prestador) throws Exception {
		boolean parametrosNulos = numeroEmpenho == null && competencia == null && prestador == null;
		Assert.isFalse(parametrosNulos, "Informe pelo menos um parâmetro!");
		
		SearchAgent sa = new SearchAgent();
		
		if (numeroEmpenho != null) {
			sa.addParameter(new Equals("numeroEmpenho", numeroEmpenho));
		} else {
			sa.addParameter(new Equals("status", 'P'));
			if (competencia != null) {
				Date comp = Utils.gerarCompetencia(competencia);
				sa.addParameter(new Equals("competencia", comp));
			}
			if (prestador != null) {
				sa.addParameter(new Equals("prestador", prestador));
			}
		}
		
		List<AbstractFaturamento> faturamentos = sa.list(AbstractFaturamento.class);
		
		ResumoFaturamentos resumo = new ResumoFaturamentos(faturamentos, null, ResumoFaturamentos.RESUMO_CATEGORIA, null, false);
		
		Assert.isNotEmpty(resumo.getFaturamentos(), "Faturamento não encontrado.");
		return resumo;
	}
	
	public AbstractFaturamento selecionarFaturamento(AbstractFaturamento faturamento) {
		faturamento.getGuiasFaturaveis().size();
		return faturamento;
	}
	
	public AbstractFaturamento alterarEmpenho(AbstractFaturamento faturamento) throws Exception {
		ImplDAO.save(faturamento);
		return faturamento;
	}
	
}
