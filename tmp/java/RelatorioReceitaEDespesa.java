package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoSegurado;
import br.com.infowaypi.ecare.financeiro.faturamento.FaturamentoSR;
import br.com.infowaypi.ecare.procedimentos.ProcedimentoCirurgicoSR;
import br.com.infowaypi.ecare.resumos.ResumoReceitaEDespesa;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.enums.TipoSeguradoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.Faturamento;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;


public class RelatorioReceitaEDespesa {
	
	public static String TODOS = TipoSeguradoEnum.TODOS.descricao();
	public static String TITULAR = TipoSeguradoEnum.TITULAR.descricao();
	public static String PENSIONISTA = TipoSeguradoEnum.PENSIONISTA.descricao();
	public static String DEPENDENTE = TipoSeguradoEnum.DEPENDENTE.descricao();
	public static String DEPENDENTE_SUPLEMENTAR = TipoSeguradoEnum.DEPENDENTE_SUPLEMENTAR.descricao();
	
	public ResumoReceitaEDespesa gerarRelatorio(String tipoBeneficiario ,String competencia) throws ValidateException{
		
		Date dataCompetencia = Utils.getCompetencia(competencia);
		List<Faturamento> faturamentos = getFaturamentos(dataCompetencia, Faturamento.class);
		Assert.isNotEmpty(faturamentos, "Não foi possível encontrar nenhum faturamento para a competência informada.");
		List<FaturamentoSR> faturamentosAnestesistas = getFaturamentos(dataCompetencia, FaturamentoSR.class);
		List<GuiaSimples> guias = getGuias(faturamentos);
		
		ResumoReceitaEDespesa resumo = new ResumoReceitaEDespesa(guias, tipoBeneficiario);

		BigDecimal totalFinanciamento = (BigDecimal) HibernateUtil.currentSession().createCriteria(ConsignacaoSegurado.class)
		.setProjection(Projections.sum("valorFinanciamento"))
		.add(Restrictions.eq("competencia", dataCompetencia))
		.add(Restrictions.eq("statusConsignacao", 'P'))
		.uniqueResult();
		
		totalFinanciamento = (totalFinanciamento != null ? totalFinanciamento : BigDecimal.ZERO);
		
		BigDecimal totalCoParticipacao = (BigDecimal) HibernateUtil.currentSession().createCriteria(ConsignacaoSegurado.class)
		.setProjection(Projections.sum("valorCoparticipacao"))
		.add(Restrictions.eq("competencia", dataCompetencia))
		.add(Restrictions.eq("statusConsignacao", 'P'))
		.uniqueResult();
		
		totalCoParticipacao = (totalCoParticipacao != null ? totalCoParticipacao : BigDecimal.ZERO);
		
		BigDecimal totalBoletos = (BigDecimal) HibernateUtil.currentSession().createCriteria(Cobranca.class)
		.setProjection(Projections.sum("valorPago"))
		.add(Restrictions.eq("situacao.descricao", SituacaoEnum.PAGO.descricao()))
		.add(Restrictions.eq("competencia", dataCompetencia))
		.uniqueResult();
		
		totalBoletos = (totalBoletos != null ? totalBoletos : BigDecimal.ZERO);

		resumo.computarFaturamentoAnestesista(faturamentosAnestesistas, tipoBeneficiario);
		
		if(!faturamentosAnestesistas.isEmpty()){
			List<ProcedimentoCirurgicoSR> procedimentosAnestesicos = HibernateUtil.currentSession().createCriteria(ProcedimentoCirurgicoSR.class)
			.add(Restrictions.in("faturamento", faturamentosAnestesistas))
			.setFetchMode("profissionalResponsavel", FetchMode.SELECT)
			.setFetchMode("profissionalAuxiliar1", FetchMode.SELECT)
			.setFetchMode("profissionalAuxiliar2", FetchMode.SELECT)
			.setFetchMode("profissionalAuxiliar3", FetchMode.SELECT)
			.setFetchMode("procedimentoDaTabelaCBHPM", FetchMode.SELECT)
			.setFetchMode("guia", FetchMode.SELECT)
			.setFetchMode("faturamento", FetchMode.SELECT)
			.setFetchMode("prestadorAnestesista", FetchMode.SELECT)
			.list();
//			resumo.computarProcedimentos(procedimentosAnestesicos, tipoBeneficiario);
		}
		BigDecimal receitaTotal = totalFinanciamento.add(totalCoParticipacao).add(totalBoletos);
		resumo.setReceitaTotal(receitaTotal);
		return resumo;
	}

	private List getFaturamentos(Date dataCompetencia, Class tipoFaturamento) {
		List faturamentos = HibernateUtil.currentSession().createCriteria(tipoFaturamento)
		.add(Restrictions.ne("status", 'C'))
		.add(Restrictions.eq("competencia", dataCompetencia))
		.setFetchMode("prestador", FetchMode.SELECT)
		.setFetchMode("profissional", FetchMode.SELECT)
		.setFetchMode("retencoes", FetchMode.SELECT)
		.setFetchMode("guias", FetchMode.SELECT)
		.setFetchMode("informacaoFinanceira", FetchMode.SELECT)
		.setFetchMode("honorariosMedicos", FetchMode.SELECT)
		.setFetchMode("alteracoesFaturamento", FetchMode.SELECT)
		.list();
		return faturamentos;
	}

	private List<GuiaSimples> getGuias(List faturamentos) {
		List<GuiaSimples> guias = HibernateUtil.currentSession().createCriteria(GuiaSimples.class)
		.add(Restrictions.in("faturamento",faturamentos))
		.setFetchMode("segurado", FetchMode.SELECT)
		.setFetchMode("profissional", FetchMode.SELECT)
		.setFetchMode("faturamento", FetchMode.SELECT)
		.setFetchMode("solicitante", FetchMode.SELECT)
		.setFetchMode("prestador", FetchMode.SELECT)
		.setFetchMode("especialidade", FetchMode.SELECT)
		.setFetchMode("guiaOrigem", FetchMode.SELECT)
		.setFetchMode("fluxoFinanceiro", FetchMode.SELECT)
		.setFetchMode("procedimentos", FetchMode.SELECT)
		.setFetchMode("guiasFilhas", FetchMode.SELECT)
		.setFetchMode("colecaoSituacoes", FetchMode.SELECT)
		.setFetchMode("cids", FetchMode.SELECT)
		.setFetchMode("quadrosClinicos", FetchMode.SELECT)
		.setFetchMode("itensDiaria", FetchMode.SELECT)
		.setFetchMode("itensTaxa", FetchMode.SELECT)
		.setFetchMode("itensGasoterapia", FetchMode.SELECT)
		.setFetchMode("itensPacote", FetchMode.SELECT)
		.setFetchMode("observacoes", FetchMode.SELECT)
		.setFetchMode("valoresMatMed", FetchMode.SELECT)
		.setFetchMode("situacao", FetchMode.SELECT)
		.list();
		return guias;
	}

	@Test
	public void test() throws ValidateException{
		long inicio;
		inicio = System.currentTimeMillis();
		
		ResumoReceitaEDespesa resumo = new RelatorioReceitaEDespesa().gerarRelatorio(TipoSeguradoEnum.TODOS.descricao(), "09/2007");
	}
	
//	public static void main(String[] args) throws ValidateException {
//		Titular titular = ImplDAO.findById(11205l, Titular.class);
//		Consignacao consignacao = titular.getConsignacao(Utils.parse("01/07/2007"));
////		for (Consignacao c : titular.getConsignacoes()) {
////			System.out.println("COMPETENCIA: "+c.getCompetencia());
////		}
//		
//		BigDecimal aliquota = BigDecimal.ZERO;
//		BigDecimal valorFixo = BigDecimal.ZERO;
//		BigDecimal valorAliquota = BigDecimal.ZERO;
//		BigDecimal salarioBase;
//		
//		aliquota = aliquota.add(titular.getValorFinanciamento(new BigDecimal(100))[0]);
//		
//		BigDecimal[] aliquotaValor = new BigDecimal[2];
//		for (DependenteInterface dependente : titular.getDependentes()) {
//			aliquotaValor = dependente.getValorFinanciamentoDependente(new BigDecimal(100));
//			if(aliquotaValor[0].equals(BigDecimal.ZERO))
//				valorFixo = valorFixo.add(aliquotaValor[1]);
//			else
//				aliquota = aliquota.add(aliquotaValor[0]);
//		}
//		
//		aliquota = aliquota.setScale(2, BigDecimal.ROUND_HALF_UP);
//		
//		valorAliquota = consignacao.getValorFinanciamento().subtract(valorFixo);
//		
////		System.out.println("ALIQUOTA: "+aliquota);
////		System.out.println("VALOR ALIQUOTA: "+valorAliquota);
//		System.out.println("VALOR FINANCIAMENTO: "+consignacao.getValorFinanciamento());
////		System.out.println("VALOR FIXO: "+valorFixo);
//		
//		BigDecimal result = valorAliquota.multiply(new BigDecimal(100)).setScale(2);
//		System.out.println("RESULT: "+result);
////		valorSalario = result.divide(aliquota);
//		salarioBase = MoneyCalculation.divide(result, aliquota.floatValue());
//		
//		System.out.println("SALARIO: "+salarioBase);
//		
//		System.out.println("VALOR TITULAR: "+titular.getValorFinanciamento(salarioBase)[1]);
//		for (DependenteInterface dependente : titular.getDependentes()) {
//			System.out.println("VALOR DEPENDENTE: "+dependente.getValorFinanciamentoDependente(salarioBase)[1]);
//		}
//		
//	}
	
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		format.setLenient(false);
		System.out.println(format.parse("01/05/0008"));
	}
	

}
