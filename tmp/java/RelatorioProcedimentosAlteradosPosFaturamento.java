package br.com.infowaypi.ecare.correcaomanual;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AbstractFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.AlteracaoFaturamento;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.NotEquals;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioProcedimentosAlteradosPosFaturamento {

	private List<Date> competencias;
	
	public RelatorioProcedimentosAlteradosPosFaturamento() {
		competencias = new ArrayList<Date>();
		montarColecaoDeCompetencias();
	}

	private void montarColecaoDeCompetencias() {
//		competencias.add(Utils.createData("01/01/2011"));
		competencias.add(Utils.createData("01/02/2011"));
		competencias.add(Utils.createData("01/03/2011"));
//		competencias.add(Utils.createData("01/04/2011"));
//		competencias.add(Utils.createData("01/05/2011"));
//		competencias.add(Utils.createData("01/06/2011"));
//		competencias.add(Utils.createData("01/07/2011"));
//		competencias.add(Utils.createData("01/08/2011"));
//		competencias.add(Utils.createData("01/09/2011"));
//		competencias.add(Utils.createData("01/10/2011"));
//		competencias.add(Utils.createData("01/11/2011"));
//		competencias.add(Utils.createData("01/12/2011"));
	}

	private List<AbstractFaturamento> getFaturamentos(Date competencia) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("competencia", competencia));
		sa.addParameter(new NotEquals("status", Constantes.FATURAMENTO_CANCELADO));
		
		//Apenas para investigação São Marcos
		Prestador prestador = (Prestador) sa.findById(374002L, Prestador.class);
		sa.addParameter(new Equals("prestador", prestador));
		//----------------------------------
		
		List<AbstractFaturamento> faturamentos = sa.list(AbstractFaturamento.class);
		Utils.sort(faturamentos, "prestador.pessoaJuridica.fantasia");
		return faturamentos;
	}
	
	private BigDecimal totalizarGuias(AbstractFaturamento faturamento){
		BigDecimal somatorioGuias = BigDecimal.ZERO;
		
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
		}
		return somatorioGuias;
	}
	
	private void recalcularGuia(GuiaFaturavel guia) {
		GuiaSimples guiaRecalculo = ImplDAO.findById(guia.getIdGuia(), GuiaSimples.class); 
		guiaRecalculo.recalcularValores();
	}
	
	public void filtrarFaturamentos() {
		for(Date competencia : competencias){
			List<AbstractFaturamento> faturamentos = getFaturamentos(competencia);
			for(AbstractFaturamento faturamento : faturamentos){
				BigDecimal valorBruto = faturamento.getValorBruto();
				if(MoneyCalculation.compare(valorBruto, totalizarGuias(faturamento)) != 0){
					System.out.println("Competência: " + Utils.format(competencia) + " Tipo: " + faturamento.getTipoFaturamento() + " Prestador: " + faturamento.getPrestador().getPessoaJuridica().getFantasia() + " Id fatturamento: " + faturamento.getIdFluxoFinanceiro());
					buscarProcedimentoAlteradoPosFaturamento(faturamento);
				}
			}
		}
	}
	
	private void buscarProcedimentoAlteradoPosFaturamento(AbstractFaturamento faturamento){
		Date dataFaturamento = null;
		Set<GuiaFaturavel> guias = faturamento.getGuiasFaturaveis();
		for(GuiaFaturavel guia : guias){
			dataFaturamento = guia.getSituacao(SituacaoEnum.FATURADA.descricao()).getDataSituacao();
			Set<Procedimento> procedimentos = ((GuiaSimples<Procedimento>)guia).getProcedimentos();
			for(ProcedimentoInterface procedimento : procedimentos){
				if(Utils.compareData(procedimento.getSituacao().getDataSituacao(), dataFaturamento) > 0){
					System.out.println(guia.getAutorizacao() + " --> procedimento: " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigo());
				}
			}
		}
	}

	public List<Date> getCompetencias() {
		return competencias;
	}

	private static void recalcularGuias(RelatorioProcedimentosAlteradosPosFaturamento relatorioProcedimentosAlterados) {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		SearchAgent sa = new SearchAgent();
		List<AbstractFaturamento> faturamentos = relatorioProcedimentosAlterados.getFaturamentos(Utils.createData("01/05/2011"));
		for(AbstractFaturamento faturamento : faturamentos){
			System.out.println("Recalculando guias do faturamento:" + faturamento.getPrestador().getPessoaJuridica().getRazaoSocial());
			
			sa.clearAllParameters();
			sa.addParameter(new Equals("faturamento", faturamento));
			List<GuiaSimples> guias = sa.list(GuiaSimples.class);
			
			for(GuiaSimples g : guias){
				g.recalcularValores();
			}
		}
		tx.commit();
	}
	
	public static void main(String[] args) throws IOException {
		RelatorioProcedimentosAlteradosPosFaturamento relatorioProcedimentosAlterados = new RelatorioProcedimentosAlteradosPosFaturamento();
		relatorioProcedimentosAlterados.filtrarFaturamentos();
		System.out.println("Pronto!!");
		
//		recalcularGuias(relatorioProcedimentosAlterados);
	}
}
