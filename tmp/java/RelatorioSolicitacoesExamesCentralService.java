package br.com.infowaypi.ecare.services;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Marcus bOolean
 *
 */
public class RelatorioSolicitacoesExamesCentralService {
	
	public static final Integer TIPO_RELATORIO_EXAMES = 1;
	public static final Integer TIPO_RELATORIO_INTERNACOES = 2;
	
	public ResumoSolicitacoesCentral processar(Date dataInicial, Date dataFinal,Integer tipoRelatorio, UsuarioInterface usuario) {
		String[] situacoes = {SituacaoEnum.SOLICITADO.descricao(), SituacaoEnum.SOLICITADO_INTERNACAO.descricao()};
		System.out.println("");
		Criteria criteria = HibernateUtil.currentSession().createCriteria(getKlass(tipoRelatorio));
		
		if (dataInicial != null)
			criteria.add(Expression.ge("dataMarcacao", dataInicial));
		if (dataFinal != null)
			criteria.add(Expression.le("dataMarcacao", dataFinal));
		
		criteria.createAlias("colecaoSituacoes", "col");
		criteria.createAlias("col.situacoes", "sit");
		criteria.add(Expression.in("sit.descricao", situacoes));
		
		if (usuario != null)
			criteria.add(Expression.eq("sit.usuario", usuario));
		
		List<GuiaSimples> guias = criteria.list();
		
		Assert.isNotEmpty(guias, "Não foram encontrados procedimentos/exames ou Internações solicitados com os parametros informados");
		
		ResumoSolicitacoesCentral resumo = new ResumoSolicitacoesCentral(guias, dataInicial, dataFinal, usuario, tipoRelatorio);
	
		return resumo;
	}
	
	public Class getKlass(Integer tipoRelatorio) {
		if(tipoRelatorio.equals(TIPO_RELATORIO_EXAMES)) {
			return GuiaExame.class;
		}else {
			return GuiaInternacao.class;
		}
	}

}
