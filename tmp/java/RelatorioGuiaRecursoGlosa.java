package br.com.infowaypi.ecare.services.recurso.relatorio;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.services.recurso.ManagerBuscaGRG;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.painelDeControle.PainelDeControle;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioGuiaRecursoGlosa {
	
	public ResumoRecursoGlosa gerarRelatorio(String competencia, Prestador prestador) throws Exception {
		
		Assert.isNotEmpty(competencia, "A competência deve ser informada.");
		
		SearchAgent sa = new SearchAgent();
		
		ResumoRecursoGlosa resumo;
		
		sa.clearAllParameters();
		
		Date comp = Utils.gerarCompetencia(competencia);
		sa.addParameter(new Equals("prestador", prestador));
		sa.addParameter(new In("situacao.descricao", SituacaoEnum.PAGO.descricao(), SituacaoEnum.GLOSADO.descricao()));
		sa.addParameter(new GreaterEquals("dataTerminoAtendimento", CompetenciaUtils.getInicioCompetencia(comp)));
		sa.addParameter(new LowerEquals("dataTerminoAtendimento", Utils.incrementaDias(CompetenciaUtils.getFimCompetencia(comp), 1)));
		sa.addParameter(new GreaterEquals("dataTerminoAtendimento", Utils.parse(ManagerBuscaGRG.DATA_IMPLANTACAO_RECURSO_GLOSA)));
		
		List<GuiaCompleta<ProcedimentoInterface>> guias = sa.list(GuiaCompleta.class);
		
		for (GuiaCompleta<ProcedimentoInterface> guia : guias) {
			Date hoje = new Date();
			Integer tempoLimiteRecursoGlosa = PainelDeControle.getPainel().getTempoLimiteRecursoGlosa();
			Date dataSituacao = guia.getSituacao().getDataSituacao();
			Date dataComLimite = Utils.incrementaDias(dataSituacao, tempoLimiteRecursoGlosa);
			if (hoje.after(dataComLimite)) {
				guias.remove(guia);
			}
		}
		
		if (guias == null || guias.size() < 1) {
			throw new ValidateException("Nenhuma guia encontrada!");
		}
		
		resumo = new ResumoRecursoGlosa(guias);
		
		if(resumo.getResumos() == null || resumo.getResumos().size() < 1){
			throw new ValidateException("Nenhuma guia encontrada!");
		}
		
		return resumo;
	}
	
}
