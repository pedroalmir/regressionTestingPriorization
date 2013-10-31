package br.com.infowaypi.ecare.services;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecarebc.atendimentos.ResumoConsignacoes;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioExtratoSegurado {
	public ResumoConsignacoes buscarConsignacoes(String cpfTitular, String numeroDoCartao) throws Exception{
		Criteria criteria = HibernateUtil.currentSession().createCriteria(TitularFinanceiroSR.class);
		
		Boolean isNenhumCampoInformado = Utils.isStringVazia(numeroDoCartao) && Utils.isStringVazia(cpfTitular);
		
		if(isNenhumCampoInformado)
			throw new ValidateException("Prezado(a) usuário(a), pelo menos um parâmetro de pesquisa deve ser informado.");
		
		if(!Utils.isStringVazia(numeroDoCartao))
			criteria.add(Expression.eq("numeroDoCartao", numeroDoCartao));
		else if(!Utils.isStringVazia(cpfTitular))
			criteria.add(Expression.eq("pessoaFisica.cpf", cpfTitular));
		
		
		TitularFinanceiroSR titular = (TitularFinanceiroSR) criteria.uniqueResult();;
		
		Boolean isTitularNulo = titular == null;
		
		if(isTitularNulo)
			throw new ValidateException("Nehum beneficiário foi encontrado.");
		
		ResumoConsignacoes resumoConsignacoes = new ResumoConsignacoes(titular, true);	
		
		return resumoConsignacoes;
	}
	
}
