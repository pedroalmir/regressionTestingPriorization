package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Disjunction;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/* if[INTEGRACAO]
import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import br.com.infowaypi.molecular.HibernateUtil;
end[INTEGRACAO] */

/**
 * 
 * Classe respons�vel por gerar um resumo de guias que representem o consumo do Segurado.
 * Mapeamento do relat�rio: RelatorioGuiasPorSegurado.jhm.xml
 * 
 * @author Diogo Vin�cius
 *
 */
public class RelatorioGuiasPorSegurado extends Service {
	
	/**
	 * M�todo que ser� executado ao receber os par�metros enviados pelo <b> report </b>
	 * @param cpf
	 * @param numeroDoCartao
	 * @param dataInicial
	 * @param dataFinal
	 * @param exibirGuias
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ResumoGuiasPorSegurado buscarGuias(String cpf, String numeroDoCartao, String dataInicial, String dataFinal, Boolean exibirGuias) throws Exception {
	
		if (Utils.isStringVazia(cpf) && Utils.isStringVazia(numeroDoCartao)) {
			throw new ValidateException("Informe o CPF ou o N�meo do Cart�o");
		} else if (!(Utils.isStringVazia(cpf) ^ Utils.isStringVazia(numeroDoCartao))) {
			throw new ValidateException("Informe apenas ou o CPF ou o N�meo do Cart�o");
		}
		
		List<AbstractSegurado> segurados = new ArrayList<AbstractSegurado>();
		SearchAgent sa = new SearchAgent();
		
		/* if[INTEGRACAO]
		Criteria criteria = HibernateUtil.currentSession().createCriteria(AbstractSegurado.class);
		segurados = criteria.createAlias("pessoaFisica", "p")
			.add(Expression.disjunction()
				.add(Expression.eq("p.cpf", cpf))
				.add(Expression.eq("numeroDoCartao", numeroDoCartao))
			).list();
		else[INTEGRACAO] */
		sa.addParameter(new Disjunction(new Equals("pessoaFisica.cpf", cpf), new Equals("numeroDoCartao", numeroDoCartao)));
		segurados = sa.list(AbstractSegurado.class);
		/* end[INTEGRACAO] */
		
		if (segurados.isEmpty()) {
			throw new ValidateException("Nenhum Benefici�rio encontrado!");
		}
		
		SituacaoEnum[] situacoesGuia = {SituacaoEnum.ABERTO,SituacaoEnum.FATURADA, 
										SituacaoEnum.CONFIRMADO, SituacaoEnum.FECHADO, 
										SituacaoEnum.PRORROGADO,SituacaoEnum.SOLICITADO_PRORROGACAO, 
										SituacaoEnum.AUDITADO, SituacaoEnum.PAGO};
		
		
		List<GuiaSimples> guias = new ArrayList<GuiaSimples>();
		for (AbstractSegurado segurado : segurados) {
			sa = getSearchAgent();
			sa = getSearchSituacoes(situacoesGuia);
			guias.addAll(this.buscarGuias(sa, "", segurado, dataInicial, dataFinal, null, true, true, GuiaSimples.class, GuiaSimples.DATA_DE_ATENDIMENTO));		
		}
		
		ResumoGuiasPorSegurado resumo = new ResumoGuiasPorSegurado(guias);
		return resumo;
		
	}

}
