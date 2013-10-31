/**
 * 
 */
package br.com.infowaypi.ecare.services;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.resumos.ResumoGuiasEProcedimentos;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.molecular.HibernateUtil;

/**
 * @author Marcus bOolean
 * Service para geração de relatório de Guias confirmadas e procedimentos
 * fechados por um prestador anestesista.
 */
public class RelatorioGuiasProcedimentosPrestadorAnestesista {
		
	public ResumoGuiasEProcedimentos buscarDados(Prestador prestador) {
		Criteria criteria1 = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
		criteria1.add(Expression.eq("prestador", prestador));
		criteria1.add(Expression.eq("situacao.descricao", SituacaoEnum.CONFIRMADO.descricao()));
		List<GuiaSimples<Procedimento>> guiasConfirmadas = criteria1.list();
		
		Criteria criteria2 = HibernateUtil.currentSession().createCriteria(Procedimento.class);
		criteria2.add(Expression.eq("situacao.descricao", SituacaoEnum.FECHADO.descricao()));
		criteria2.add(Expression.eq("prestadorAnestesista", prestador));
		List<Procedimento> procedimentosFechados = criteria2.list();
		
		ResumoGuiasEProcedimentos resumo = new ResumoGuiasEProcedimentos(guiasConfirmadas, procedimentosFechados); {
		
		return resumo;
		}
		
	}
}
