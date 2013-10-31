package br.com.infowaypi.ecare.relatorio;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.resumos.ResumoProcedimentosSR;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;


public class RelatorioProcedimentoPorPeriodo {
	
	
	public ResumoProcedimentosSR gerarRelatorio(TabelaCBHPM tabelaCBHPM,Date dataInicial, Date dataFinal, Prestador prestador ){
		
		Criteria criteriaProcedimentos = HibernateUtil.currentSession().createCriteria(Procedimento.class);
		Criteria criteriaGuias = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
		
		
		String[] situacoes = {SituacaoEnum.CANCELADO.descricao(),SituacaoEnum.NAO_AUTORIZADO.descricao()};
		criteriaProcedimentos.add(Expression.not(Expression.in("situacao.descricao", situacoes)));
		
		if (tabelaCBHPM != null)
			criteriaProcedimentos = criteriaProcedimentos.add(Expression.eq("procedimentoDaTabelaCBHPM",tabelaCBHPM));
		
		if (dataInicial != null)
			criteriaGuias = criteriaGuias.add(Expression.ge("dataMarcacao",dataInicial));
		
		if (dataFinal != null)
			criteriaGuias = criteriaGuias.add(Expression.le("dataMarcacao",dataFinal));
		
		if (prestador != null)
			criteriaGuias = criteriaGuias.add(Expression.eq("prestador", prestador));
		
		List guias = criteriaGuias
			.add(Expression.not(Expression.in("situacao.descricao", situacoes)))
			.list();
		
		List<Procedimento> procedimentos = criteriaProcedimentos
		.add(Expression.in("guia", guias))
		.list();
		
		return new ResumoProcedimentosSR(procedimentos);
	}

}
