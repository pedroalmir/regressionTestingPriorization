package br.com.infowaypi.ecare.correcaomanual;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AlteracaoFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioValorFaturamentoPorSomatorioGuias {

	private List<Date> competencias;
	private StringBuilder stringBuilder;
	private BigDecimal somatorioGuias;
	private BigDecimal somatorioHonorarios;
	private boolean possuiHonorario;
	
	private String normal = "Faturamento Normal";
	
	private CorrecaoProcedimentosCriadosDepoisDaGuiaFaturada correcaoProcedimentos = new CorrecaoProcedimentosCriadosDepoisDaGuiaFaturada();
	
	public RelatorioValorFaturamentoPorSomatorioGuias() {
		competencias = new ArrayList<Date>();
		stringBuilder = new StringBuilder();
		
		montarColecaoDeCompetencias();
	}

	private void montarColecaoDeCompetencias() {
//		competencias.add(Utils.createData("01/01/2011"));
//		competencias.add(Utils.createData("01/02/2011"));
//		competencias.add(Utils.createData("01/03/2011"));
//		competencias.add(Utils.createData("01/04/2011"));
//		competencias.add(Utils.createData("01/05/2011"));
//		competencias.add(Utils.createData("01/06/2011"));
//		competencias.add(Utils.createData("01/07/2011"));
//		competencias.add(Utils.createData("01/08/2011"));
//		competencias.add(Utils.createData("01/09/2011"));
//		competencias.add(Utils.createData("01/10/2011"));
		competencias.add(Utils.createData("01/11/2011"));
//		competencias.add(Utils.createData("01/12/2011"));
	}

	private List<AbstractFaturamento> getFaturamentos(Date competencia) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("competencia", competencia));
		
		//Apenas para investigação São Marcos
		Prestador prestador = (Prestador) sa.findById(374002L, Prestador.class);
		sa.addParameter(new Equals("prestador", prestador));
		//----------------------------------
		
		List<AbstractFaturamento> faturamentos = sa.list(AbstractFaturamento.class);
		Utils.sort(faturamentos, "prestador.pessoaJuridica.fantasia");
		return faturamentos;
	}
	
	private void totalizarGuias(AbstractFaturamento faturamento) throws Exception{
		somatorioGuias = BigDecimal.ZERO;
		somatorioHonorarios = BigDecimal.ZERO;
		possuiHonorario = false;
		
		Set<GuiaFaturavel> guias = faturamento.getGuiasFaturaveis();
		for(GuiaFaturavel guia : guias){
			somatorioGuias = somatorioGuias.add(guia.getValorPagoPrestador());
		}
		
		Set<AlteracaoFaturamento> alteracoesFaturamento = faturamento.getAlteracoesFaturamentoAtivos();
		for(AlteracaoFaturamento af : alteracoesFaturamento ){
			somatorioGuias = somatorioGuias.add(af.getSaldo());
		}

		Set<ItemGuiaFaturamento> itensGuiaFaturamento = faturamento.getItensGuiaFaturamento();
		for(ItemGuiaFaturamento ig : itensGuiaFaturamento){
			somatorioGuias = somatorioGuias.add(ig.getValor());
//			somatorioGuias = somatorioGuias.add(ig.getValorPago());
			
			possuiHonorario = true;
			somatorioHonorarios = somatorioHonorarios.add(ig.getValor());
//			somatorioHonorarios = somatorioHonorarios.add(ig.getValorTotal());
		}
		
	}
	
	private void logaFaturamentoDiscrepante(AbstractFaturamento faturamento, BigDecimal valorBruto, BigDecimal somatorioDeGuias) {
		if(faturamento.getTipoFaturamento().equals(normal)){
			stringBuilder.append("NORMAL;");
		}else {
			stringBuilder.append("PASSIVO;");
		}
		stringBuilder.append(valorBruto + ";");
		stringBuilder.append(somatorioDeGuias + ";");
	}
	
//	private void recalcularGuia(GuiaFaturavel guia) {
//		GuiaSimples guiaRecalculo = ImplDAO.findById(guia.getIdGuia(), GuiaSimples.class); 
//		guiaRecalculo.recalcularValores();
//	}
//	
//	private void recalcularGuias(AbstractFaturamento faturamento) {
//		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new Equals("faturamento", faturamento));
//		List<GuiaSimples> guias = sa.list(GuiaSimples.class);
//		
//		for(GuiaSimples g : guias){
//			g.recalcularValores();
//		}
//	}
	
//	private BigDecimal getTotalGuias(AbstractFaturamento faturamento){
//		BigDecimal somatorioGuias = BigDecimal.ZERO;
//		
//		Set<GuiaFaturavel> guias = faturamento.getGuiasFaturaveis();
//		for(GuiaFaturavel guia : guias){
//			somatorioGuias = somatorioGuias.add(guia.getValorPagoPrestador());
////			somatorioGuias = somatorioGuias.add(guia.getValorTotal());
//		}
//		
//		Set<AlteracaoFaturamento> alteracoesFaturamento = faturamento.getAlteracoesFaturamentoAtivos();
//		for(AlteracaoFaturamento af : alteracoesFaturamento ){
//			somatorioGuias = somatorioGuias.add(af.getSaldo());
//		}
//
//		Set<ItemGuiaFaturamento> itensGuiaFaturamento = faturamento.getItensGuiaFaturamento();
//		for(ItemGuiaFaturamento ig : itensGuiaFaturamento){
//			somatorioGuias = somatorioGuias.add(ig.getValor());
////			somatorioGuias = somatorioGuias.add(ig.getValorPago());
//		}
//		return somatorioGuias;
//	}
	public void analisarDiscrepancia() throws Exception {
		stringBuilder.append("Competencia;IdFaturamento;Prestador;Discrepancia;Tipo de Faturamento;Valor Bruto; Somatorio das Guias;Possui honorario;Somatorio das Honorarios \n");
		for(Date competencia : competencias){
			int countDiscrepante = 0;
			List<AbstractFaturamento> faturamentos = getFaturamentos(competencia);
			for(AbstractFaturamento faturamento : faturamentos){
				
				//Corrigindo e recalculando guias antes de analisar
				Transaction tx = HibernateUtil.currentSession().beginTransaction();
				correcaoProcedimentos.corrigirFaturamentos(Arrays.asList(faturamento.getIdFluxoFinanceiro()));
				tx.commit();
				//-------------------------------------------------
				
				BigDecimal valorBruto = faturamento.getValorBruto();
				totalizarGuias(faturamento);

				if(MoneyCalculation.compare(valorBruto, somatorioGuias) != 0){
					countDiscrepante++;
					stringBuilder.append(Utils.format(competencia) + ";");
					stringBuilder.append(faturamento.getIdFluxoFinanceiro() + ";");
					stringBuilder.append(faturamento.getPrestador().getPessoaJuridica().getFantasia() + ";");
					
					if(MoneyCalculation.compare(valorBruto, somatorioGuias) > 0){
						stringBuilder.append("MAIS;");
						logaFaturamentoDiscrepante(faturamento, valorBruto, somatorioGuias);
					}else {
						stringBuilder.append("MENOS;");
						logaFaturamentoDiscrepante(faturamento, valorBruto, somatorioGuias);
					}
					stringBuilder.append(possuiHonorario == true? "Sim;" : "Nao;");
					stringBuilder.append(somatorioHonorarios + "\n");
				}
			}
		}
	}

	public StringBuilder getStringBuilder() {
		return stringBuilder;
	}
	
	public static void main(String[] args) throws Exception {
		RelatorioValorFaturamentoPorSomatorioGuias relatorioValorFaturamentoPorSomatorioGuias = new RelatorioValorFaturamentoPorSomatorioGuias();
		relatorioValorFaturamentoPorSomatorioGuias.analisarDiscrepancia();

		File arquivo = new File("C://Users//wislanildo//Documents//FaturamentoDiscrepante//SaoMarcos//RelatorioFaturamentoDiscrepante.csv");
		FileWriter fw = new FileWriter(arquivo);
		fw.write(relatorioValorFaturamentoPorSomatorioGuias.getStringBuilder().toString());
		fw.flush();
		fw.close();
		
		System.out.println("Pronto!!");
	}
}
