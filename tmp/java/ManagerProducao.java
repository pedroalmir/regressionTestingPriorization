package br.com.infowaypi.ecare.relatorio.producao;


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.type.BooleanType;
import org.hibernate.type.Type;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;

/**
 * Centraliza as consultas envolvendo GuiaProducao (VO de GuiaSimples para geração de relatórios de Produção)
 *  
 * @author Leonardo Sampaio
 *
 */
public class ManagerProducao {
	
	/**
	 * Busca todos as guias do intervalo e os agrupa por prestador no {@link ResumoRelatorioProducao}
	 * @param dataInicio
	 * @param dataFim
	 * @return 
	 */
	public Set<GuiaProducao> getGuias(Date dataInicioAdmissao, Date dataFimAdmissao) {
	  	    
	    Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaSimples.class);
	    
	    String cirurgiaAmbulatorialSQL = "(not exists (select iditemdiaria from ItemDiaria where ItemDiaria.idguia = this_.idGuia) and "+
	    "exists (select idprocedimento  from PROCEDIMENTOS_Procedimento left join procedimentos_tabelacbhpm as tabela on " + 
	    	"PROCEDIMENTOS_Procedimento.idprocedimentodatabelacbhpm = tabela.idtabelacbhpm " +
	    "where PROCEDIMENTOS_Procedimento.idguia = this_.idGuia and tabela.codigo like '3%'))";
	    
	    ProjectionList projList = Projections.projectionList();
	    projList.add(Projections.property("idGuia"));
	    projList.add(Projections.property("prestador"));
	    projList.add(Projections.property("valorTotalApresentado"));
	    projList.add(Projections.property("valorPagoPrestador"));
	    projList.add(Projections.property("tipoDeGuia"));
	    projList.add(Projections.property("tipoTratamento"));
	    projList.add(Projections.alias(Projections.sqlProjection(cirurgiaAmbulatorialSQL+" as cirurgiaAmbulatorial", new String[]{"cirurgiaAmbulatorial"}, new Type[]{new BooleanType()}),"cirurgiaAmbulatorial"));
	    
	    criteria.setProjection(projList).
	    setResultTransformer(new AliasToBeanConstructorResultTransformer(GuiaProducao.class.getConstructors()[0]));

	    String[] situacoesDaGuia = {
                        		    SituacaoEnum.RECEBIDO.descricao(),
                        		    SituacaoEnum.FATURADA.descricao(),
                        		    SituacaoEnum.PAGO.descricao()
                        	 	};

	    if(dataInicioAdmissao != null && dataFimAdmissao != null ){ //FIXME verificar datas
		criteria.add(Restrictions.ge("dataMarcacao", dataInicioAdmissao));
		criteria.add(Restrictions.le("dataAtendimento", dataFimAdmissao));
	    }

	    criteria.createAlias("colecaoSituacoes", "cs")
	    .createAlias("cs.situacoes", "st")
	    .add(Expression.eq("st.descricao", SituacaoEnum.RECEBIDO.descricao()))
	    .add(Expression.ne("situacao.descricao", SituacaoEnum.CANCELADO.descricao()))
	    .add(Expression.in("situacao.descricao", situacoesDaGuia));
	    criteria.addOrder(Order.asc("st.dataSituacao"));

	    @SuppressWarnings("unchecked")
	    List<GuiaProducao> list = criteria.list();

	    return new HashSet<GuiaProducao>(list);
	}

	public Map<Prestador, Collection<GuiaProducao>> getGuiasAgrupadasPorPrestador(Date dataInicioAdmissao, Date dataFimAdmissao) {
		final Map<Prestador, Collection<GuiaProducao>> guias = new HashMap<Prestador, Collection<GuiaProducao>>();
		Set<GuiaProducao> set = this.getGuias(dataInicioAdmissao, dataFimAdmissao);
		for (GuiaProducao guia : set) {
			put(guia, guias);
		}
		return guias;
	}

	private void put(GuiaProducao guia, Map<Prestador,Collection<GuiaProducao>> map){
		Collection<GuiaProducao> set = map.get(guia.getPrestador());
		if(set == null){
			set = new HashSet<GuiaProducao>();
			map.put(guia.getPrestador(), set);
		}
		set.add(guia);
	}
	
}
