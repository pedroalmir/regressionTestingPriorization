package br.com.infowaypi.ecare.relatorio;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecare.enums.SituacaoEnum;
import br.com.infowaypi.ecare.legislacaosr.LegislacaoSR;
import br.com.infowaypi.ecare.resumos.ResumoGeral;
import br.com.infowaypi.molecular.SearchAgent;
/**
 * Classe que gera uma lista de arquivos PDF da Legislação do Saúde Recife
 * 
 * @author Jefferson
 *
 */
public class LegislacaoSRDownload {

	public ResumoGeral<LegislacaoSR> buscarLegislacao() {
		SearchAgent sa = new SearchAgent();
		Criteria ca =  sa.createCriteriaFor(LegislacaoSR.class);
		ca.add(Restrictions.eq("situacao", SituacaoEnum.ATIVO.getDescricao()));
		ca.addOrder(Order.desc("datapublicacao"));
		List<LegislacaoSR> list = ca.list();
		return new ResumoGeral<LegislacaoSR>(list);
	}
}
