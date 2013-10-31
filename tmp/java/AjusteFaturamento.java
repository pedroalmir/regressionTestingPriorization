package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;

import br.com.infowaypi.ecare.financeiro.faturamento.CancelarFaturamento;
import br.com.infowaypi.ecarebc.associados.Prestador.TipoIncremento;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.ManagerGuia;
import br.com.infowaypi.ecarebc.constantes.Constantes;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.FaturamentoPassivo;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.ecarebc.financeiro.faturamento.ItemGuiaFaturamento;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class AjusteFaturamento {
	
	private static String competenciaAjuste = null;
	private static UsuarioInterface usuario = null;
	private static String motivo = "Ajuste operacional, guias de consulta cobradas externamente.";
	
	static{
		usuario = ImplDAO.findById(1L, Usuario.class);
	}
	
	private static void addProcedimento(GuiaCompleta<ProcedimentoInterface> guia, ProcedimentoInterface proc) throws Exception {

		Procedimento procedimento = new Procedimento();
		procedimento.setProcedimentoDaTabelaCBHPM(proc.getProcedimentoDaTabelaCBHPM());
		procedimento.calcularCampos();
		procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), "Ajuste operacional.", new Date());
		procedimento.setGuia(guia);
		procedimento.aplicaValorAcordo();
		procedimento.calculaCoParticipacao();
		
		guia.getProcedimentos().add(procedimento);
		
		// Atualizando valores
		guia.setValorTotal( guia.getValorTotal().add(procedimento.getValorTotal()));
		
		//Atualizando consumos
		guia.getPrestador().atualizarConsumoFinanceiro(guia, procedimento.getQuantidade(), TipoIncremento.POSITIVO);
		guia.getSegurado().atualizarLimites(guia, TipoIncremento.POSITIVO, procedimento.getQuantidade());
			
	}
	
	private static void removeProcedimento(GuiaCompleta<ProcedimentoInterface> guia, ProcedimentoInterface procedimento) throws Exception {
		for (ProcedimentoInterface proced : guia.getProcedimentos()) {
			if (proced.equals(procedimento) && !proced.isSituacaoAtual(SituacaoEnum.CANCELADO.descricao())) {

				// Atualizando valores
				guia.setValorTotal( guia.getValorTotal().subtract(proced.getValorTotal()));
				
				//Atualizando consumos
				guia.getPrestador().atualizarConsumoFinanceiro(guia, proced.getQuantidade(), TipoIncremento.NEGATIVO);
				guia.getSegurado().atualizarLimites(guia, TipoIncremento.NEGATIVO, proced.getQuantidade());
				
				proced.mudarSituacao(usuario, SituacaoEnum.CANCELADO.descricao(), motivo, new Date());
			}
		}
		guia.updateValorCoparticipacao();
	}
	
	private static void adicionarGuiasAoFaturamento(SearchAgent sa, List<GuiaCompleta> guias, BigDecimal somatorioGuias, Long idPrestador) throws ValidateException {
		
		sa.clearAllParameters();
		Date competencia = Utils.getCompetencia(competenciaAjuste);
		sa.addParameter(new Equals("competencia", competencia));
		sa.addParameter(new Equals("status", Constantes.FATURAMENTO_ABERTO));
		sa.addParameter(new Equals("prestador.idPrestador", idPrestador));
		
		FaturamentoPassivo faturamento = sa.uniqueResult(FaturamentoPassivo .class);
		
		//Bugado, desvinculando guias do faturamento
//		faturamento.getGuiasFaturaveis().size();
//		faturamento.getGuiasFaturaveis().addAll(guias);
		
		for(GuiaCompleta guia : guias){
			guia.setFaturamento(faturamento);
		}
		
		String fantasia = faturamento.getPrestador().getPessoaJuridica().getFantasia();
		
		System.out.println("Valor faturamento " + fantasia + " antes da intervenção: " + faturamento.getValorBruto());
		faturamento.setValorBruto(faturamento.getValorBruto().add(somatorioGuias));
		System.out.println("Valor faturamento " + fantasia + " depois da intervenção: " + faturamento.getValorBruto());
	}

	private static void removerItensGuiaFaturamento(SearchAgent sa, Long idPrestador) throws Exception {
		
		sa.clearAllParameters();
		sa.addParameter(new Equals("competencia", Utils.getCompetencia(competenciaAjuste)));
		sa.addParameter(new Equals("status", Constantes.FATURAMENTO_ABERTO));
		sa.addParameter(new Equals("prestador.idPrestador", idPrestador));
		
		FaturamentoPassivo faturamento = sa.uniqueResult(FaturamentoPassivo.class);
		
		BigDecimal valorAjuste = BigDecimal.ZERO;
		
		List<ItemGuiaFaturamento> itensGuiaFaturamento = new ArrayList<ItemGuiaFaturamento>(faturamento.getItensGuiaFaturamento());
		for(ItemGuiaFaturamento ig:  itensGuiaFaturamento){
			ProcedimentoInterface procedimento = ig.getProcedimento();
			if(ManagerGuia.isProcedimentoDeConsulta(procedimento)){
				valorAjuste = valorAjuste.add(procedimento.getValorTotal());
				System.out.println("Removendo IGF para o procedimento - " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao() 
						+ " na guia - " + procedimento.getGuia().getAutorizacao() + " - " + ig.getIdItemGuiaFaturamento());
				procedimento.getGuia().getItensGuiaFaturamento().remove(ig);
				faturamento.getItensGuiaFaturamento().remove(ig);
				ImplDAO.delete(ig);
			}
		}
		
		final String fantasia = faturamento.getPrestador().getPessoaJuridica().getFantasia();
		System.out.println("Valor removido do faturamento do "+ fantasia + ": " + valorAjuste);
		
		System.out.println("Valor faturamento " + fantasia + " antes da intervenção: " + faturamento.getValorBruto());
		faturamento.setValorBruto(faturamento.getValorBruto().subtract(valorAjuste));
		System.out.println("Valor faturamento " + fantasia + " depois da intervenção: " + faturamento.getValorBruto());
		
		if(MoneyCalculation.compare(faturamento.getValorBruto(), BigDecimal.ZERO) == 0){
			System.out.println("O faturamento do prestador " + fantasia + " será cancelado por ter sido zerado após o ajuste.");
//			CancelarFaturamento.cancelaFaturamento(faturamento, usuario);
		}
		
	}

	private static Object[] corrigirCBHPMProcedimentos(SearchAgent sa, List<String> autorizacoes) throws Exception {
		
		//Primeira posição coleção de guias, segunda posição somatória das guias da interveção
		Object[] retorno = new Object[2];
		
		List<GuiaCompleta> guias = null;

		sa.clearAllParameters();
		sa.addParameter(new In("autorizacao", autorizacoes));
		guias = sa.list(GuiaCompleta.class);
		
		retorno[0] = guias;
		
		System.out.println("Antes da intervenção");
		quebrarLinha();
		logarGuias(guias);
		
		BigDecimal valorAjuste = BigDecimal.ZERO;
		
		for(GuiaCompleta guia : guias){
			Set<ProcedimentoInterface> procedimentosOutrosNaoCancelados = guia.getProcedimentosOutrosNaoCancelados();
			for(ProcedimentoInterface procedimento : procedimentosOutrosNaoCancelados){
				if(ManagerGuia.isProcedimentoDeConsulta(procedimento)){
					removeProcedimento(guia, procedimento);
					addProcedimento(guia, procedimento);
					
					//Valor a ser decrementado
					valorAjuste = valorAjuste.add(procedimento.getValorTotal());
					
					//Atualizando a guia
					guia.recalcularValores();
					guia.updateValorCoparticipacao();
				}
			}
		}
		
		quebrarLinha();
		System.out.println("Depois da intervenção");
		quebrarLinha();
		logarGuias(guias);
		
		retorno[1] = valorAjuste;
		
		return retorno;
	}

	private static void quebrarLinha() {
		System.out.println();
	}

	private static void logarGuias(List<GuiaCompleta> guias) {
		for(GuiaCompleta guia : guias){
			quebrarLinha();
			System.out.println("## Autorização: " + guia.getAutorizacao() + " ##");
			System.out.println("Procedimentos Simples:");
			imprimeProcedimentosSimplesConferencia(guia);
			System.out.println("------------------------------------------------------------------------------------");
			System.out.println("Procedimentos Outros:");
			imprimeProcedimentosOutrosARemover(guia);
			System.out.println("____________________________________________________________________________________");
			quebrarLinha();
		}
	}

	private static void imprimeProcedimentosOutrosARemover(GuiaCompleta guia) {
		Set<ProcedimentoInterface> procedimentosOutrosNaoCancelados = guia.getProcedimentosOutrosNaoCancelados();
		for(ProcedimentoInterface procedimento : procedimentosOutrosNaoCancelados){
			if(ManagerGuia.isProcedimentoDeConsulta(procedimento)){
				System.out.println("  --> " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao());
			}
		}
	}
	
	private static void imprimeProcedimentosSimplesConferencia(GuiaCompleta guia) {
		Set<ProcedimentoInterface> procedimentosSimplesNaoCancelados = guia.getProcedimentosSimples();
		for(ProcedimentoInterface procedimento : procedimentosSimplesNaoCancelados){
			if(ManagerGuia.isProcedimentoDeConsulta(procedimento)){
				System.out.println("  --> " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao());
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		competenciaAjuste = "11/2012";
		
//		long SAO_MARCOS = 374002L;
//		long SEMPRE = 789151L;
		
		long PRONTOLINDA = 29815917L;
		long CICLO = 5550481L;		
		
		SearchAgent sa = new SearchAgent();
		List<String> guiasParaCorrigir = Arrays.asList("1822075", "1826045");
		
		//Ajustando os procedimentos CBHPM das guias
		Object[] retorno = corrigirCBHPMProcedimentos(sa, guiasParaCorrigir);
		
		List<GuiaCompleta> guias = (List<GuiaCompleta>) retorno[0];
		BigDecimal somatorioGuias = (BigDecimal) retorno[1];
		
		System.out.println("Valor total do ajuste: " + somatorioGuias);
		
		removerItensGuiaFaturamento(sa, CICLO);
		adicionarGuiasAoFaturamento(sa, guias, somatorioGuias, PRONTOLINDA);
		
		forcarRecalculoDeGuias(guiasParaCorrigir);
		
		tx.commit();
	}

	private static void forcarRecalculoDeGuias(List<String> autorizacoes) {
		SearchAgent sa = new SearchAgent();
		
		List<GuiaCompleta> guias = null;
		sa.clearAllParameters();
		sa.addParameter(new In("autorizacao", autorizacoes));
		guias = sa.list(GuiaCompleta.class);
		
		for(GuiaCompleta g : guias){
			System.out.println(g.getAutorizacao() + " - " + g.getValorTotal() );
			g.recalcularValores();
			g.updateValorCoparticipacao();
		}
	}

}