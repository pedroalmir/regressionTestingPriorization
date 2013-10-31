package br.com.infowaypi.ecare.relatorio;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecare.dicadesaude.DicaDeSaude;
import br.com.infowaypi.ecare.enums.SituacaoEnum;
import br.com.infowaypi.ecare.resumos.ResumoGeral;
import br.com.infowaypi.molecular.SearchAgent;
/**
 * Classe que gera uma lista de arquivos PDF da Legislação do Saúde Recife
 * 
 * @author Jefferson
 *
 */
public class DicaDeSaudeDownload {

	public ResumoGeral<DicaDeSaude> buscarDica() {
		SearchAgent sa = new SearchAgent();
		Criteria ca =  sa.createCriteriaFor(DicaDeSaude.class);
		ca.add(Restrictions.eq("situacao", SituacaoEnum.ATIVO.getDescricao()));
		ca.addOrder(Order.desc("dataCriacao"));
		List<DicaDeSaude> list = ca.list();
		return new ResumoGeral<DicaDeSaude>(list);
	}
	
}
