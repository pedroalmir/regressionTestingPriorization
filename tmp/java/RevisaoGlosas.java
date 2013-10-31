package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
/**
 * 
 * @author Idelvane Santana
 * Usada no flow de revisão de glosas
 *
 */
public class RevisaoGlosas extends Service{
	
	@SuppressWarnings("unchecked")
	public ResumoGlosas gerarRelatorio(String competencia, Prestador prestador) throws ValidateException{
		Assert.isNotEmpty(competencia,"A competência deve ser informada.");
		Assert.isNotNull(prestador,"O prestador deve ser informado.");
		Date competenciaEscolhida = getCompetencia(competencia);
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("competencia", competenciaEscolhida));
		sa.addParameter(new Equals("prestador", prestador));
		sa.addParameter(new Equals("status", "A"));
		
		List<Faturamento> faturas = sa.list(Faturamento.class);
		Assert.isNotEmpty(faturas, "Nenhuma glosa foi encontrada.");
		
		ResumoGlosas resumo = new ResumoGlosas(faturas.get(0), competenciaEscolhida, prestador, true);
		
		return resumo;
	}

	

}
