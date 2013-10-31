package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioProducaoResiduoPassivoTodasCompetencias {

	public static void main(String[] args) {
		Calendar competencia = Calendar.getInstance(); 
		competencia.setTime(Utils.createData("01/07/2007"));
		
		Date dataFim = Utils.createData("01/10/2012");
		System.out.println("competencia; somatorio");
		while(competencia.getTime().compareTo(dataFim) <= 0){
			imprimirPassivo(competencia.getTime());
			competencia.add(Calendar.MONTH, 1);
		}
		
	}

	private static void imprimirPassivo(Date competencia) {
		BigDecimal passivo = getValorFaturamentoPassivo(competencia);
		System.out.println(Utils.format(competencia) + ";" + Utils.format(passivo != null? passivo:BigDecimal.ZERO));
	}
	
	private static void imprimirProduzidas(Date competencia) {
		BigDecimal somatorioGuiaProduzidas = getGuiasProduzidasNaCompetencia(competencia);
		System.out.println(Utils.format(competencia) + ";" + Utils.format(somatorioGuiaProduzidas));
	}
	
	private static void imprimirPago(Date competencia) {
		BigDecimal somatorioGuiaPagas= getGuiasPagasNaCompetencia(competencia);
		System.out.println(Utils.format(competencia) + ";" + Utils.format(somatorioGuiaPagas));
	}
	
	private static void imprimirResiduo(Date competencia) {
		BigDecimal somatorioGuiaProduzidas = getGuiasProduzidasNaCompetencia(competencia);
		BigDecimal somatorioGuiaPagas= getGuiasPagasNaCompetencia(competencia);
		System.out.println(Utils.format(competencia) + ";" + Utils.format(somatorioGuiaProduzidas.subtract(somatorioGuiaPagas)));
	}
	
	private static BigDecimal getValorFaturamentoPassivo(Date competencia){
		String hql = "select sum(f.valorBruto) from FaturamentoPassivo f where f.competencia = :competencia";
		
		Query hqlQuery = HibernateUtil.currentSession().createQuery(hql);
		hqlQuery.setDate("competencia", CompetenciaUtils.getCompetencia(competencia));
		return (BigDecimal) hqlQuery.list().get(0);
	}
	
	private static BigDecimal getGuiasPagasNaCompetencia(Date competencia) {
		String hql = "select sum(g.valorTotal) from GuiaFaturavel g where g.dataTerminoAtendimento >= :inicioCompetencia and " +
				" g.dataTerminoAtendimento <= :finalCompetencia and g.situacao.descricao in ('Pago(a)', 'Faturado(a)')";
		
		Query hqlQuery = HibernateUtil.currentSession().createQuery(hql);
		hqlQuery.setDate("inicioCompetencia", CompetenciaUtils.getInicioCompetencia(competencia));
		hqlQuery.setDate("finalCompetencia", CompetenciaUtils.getFimCompetencia(competencia));
		return (BigDecimal) hqlQuery.list().get(0);
	}
	
	private static BigDecimal getGuiasProduzidasNaCompetencia(Date competencia) {
		String hql = "select sum(g.valorTotal) from GuiaFaturavel g " +
				"where g.dataTerminoAtendimento >= :inicioCompetencia and g.dataTerminoAtendimento <= :finalCompetencia " +
				"and g.situacao.descricao not in ('Não Autorizado(a)', 'Inapto(a)', 'Solicitado(a) Internação', 'Cancelado(a)', 'Solicitado(a)', 'Agendado(a)','Glosado(a)')";
		
		Query hqlQuery = HibernateUtil.currentSession().createQuery(hql);
		hqlQuery.setDate("inicioCompetencia", CompetenciaUtils.getInicioCompetencia(competencia));
		hqlQuery.setDate("finalCompetencia", CompetenciaUtils.getFimCompetencia(competencia));
		return (BigDecimal) hqlQuery.list().get(0);
	}
	
}
