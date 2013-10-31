package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.enums.TipoLiberacaoForaLimiteEnum;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.utils.Utils;


/**
 * 
 * @author Marcus bOolean
 *
 */
public class RelatorioGuiasExameForaDoLimiteService {
	
	@SuppressWarnings("unchecked")
	public ResumoGuiasForaDoLimite buscarGuias(String dataInicial, String dataFinal, Integer tipoData) {
		Date dataInicio = Utils.parse(dataInicial);
		Date dataFim = Utils.parse(dataFinal);
		Calendar dataDoInicio = Calendar.getInstance();
		Calendar dataDoFim = Calendar.getInstance();
		dataDoInicio.setTime(dataInicio);
		dataDoInicio.set(Calendar.HOUR_OF_DAY, 0);
		dataDoInicio.set(Calendar.MINUTE, 0);
		dataDoInicio.set(Calendar.SECOND, 0);
		dataDoFim.setTime(dataFim);
		dataDoFim.set(Calendar.HOUR_OF_DAY, 23);
		dataDoFim.set(Calendar.MINUTE, 59);
		dataDoFim.set(Calendar.SECOND, 59);
		
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaExame.class);
		if(tipoData.equals(GuiaSimples.DATA_DE_ATENDIMENTO)) {
			criteria.add(Expression.ge("dataAtendimento", dataDoInicio.getTime()));
			criteria.add(Expression.le("dataAtendimento", dataDoFim.getTime()));
		}else {
			criteria.add(Expression.ge("dataMarcacao", dataDoInicio.getTime()));
			criteria.add(Expression.le("dataMarcacao", dataDoFim.getTime()));
		}
		List<Integer> tiposLiberacao = new ArrayList<Integer>();
		tiposLiberacao.add(TipoLiberacaoForaLimiteEnum.LIBERACAO_AUDITOR.codigo());
		tiposLiberacao.add(TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo());
		criteria.add(Expression.in("liberadaForaDoLimite", tiposLiberacao));
		criteria.add(Expression.ne("situacao.descricao", SituacaoEnum.REALIZADO.descricao()));
		
		ResumoGuiasForaDoLimite resumo = new ResumoGuiasForaDoLimite(criteria.list());
		
		return resumo;
	}

}
