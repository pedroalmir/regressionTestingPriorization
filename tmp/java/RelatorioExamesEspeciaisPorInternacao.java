package br.com.infowaypi.ecare.services;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioExamesEspeciaisPorInternacao {
	
	public static void main(String[] args) throws ValidateException {
		Date competencia = Utils.gerarCompetencia("01/2008");
		
		Calendar dataInicial = new GregorianCalendar();
		dataInicial.setTime(competencia);
		
		Calendar dataFinal = new GregorianCalendar();
		dataFinal.setTime(competencia);
		dataFinal.set(Calendar.DAY_OF_MONTH, dataFinal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		System.out.println("DT. Inicial: " + dataInicial.getTime());
		System.out.println("DT. Final: " + dataFinal.getTime());
		System.out.println();
		
		Criteria criteria = HibernateUtil.currentSession().createCriteria(GuiaInternacao.class);
		criteria.add(Expression.between("dataAtendimento", dataInicial.getTime(), dataFinal.getTime()));
		criteria.add(Expression.eq("tipoDeGuia", "IEL"));
		criteria.add(Expression.or(Expression.eq("situacao.descricao", SituacaoEnum.FECHADO.descricao()), Expression.eq("situacao.descricao", SituacaoEnum.FATURADA.descricao())));
		criteria.add(Expression.isNotEmpty("procedimentos"));
		
		List<GuiaInternacao> internacoes = criteria.list();
		
//		for (GuiaInternacao internacao : internacoes) {
//			for (ProcedimentoInterface procedimento : internacao.getProcedimentos()) {
//				if (procedimento.getProcedimentoDaTabelaCBHPM()){
//					
//				}
//			}
//		}
	}

}
