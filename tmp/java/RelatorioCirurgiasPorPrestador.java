import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;

/**
 * 
 */

/**
 * @author Marcus boolean
 *
 */
public class RelatorioCirurgiasPorPrestador {
	
	private static List<GuiaInternacao> guias = new ArrayList<GuiaInternacao>();
	private static Map<Date, Set<GuiaInternacao>> guiasPorMes = new HashMap<Date, Set<GuiaInternacao>>();
	private static Map<Date, List<ProcedimentoCirurgico>> procedimentosPorMes = new HashMap<Date, List<ProcedimentoCirurgico>>();
	private static Map<Date, Map<ProcedimentoCirurgico, Integer>> relatorio = new HashMap<Date, Map<ProcedimentoCirurgico,Integer>>();
	private static String[] situacoes = {"Cancelado(a)","Não Autorizado(a)","Glosado(a)","Inapto(a)","Solicitado(a) Internação"};
	private static Prestador SAO_MARCOS = ImplDAO.findById(374002L, Prestador.class);
	
	
	public static void main(String[] args) {
		guias = buscarGuias();
//		Utils.sort(guias, "dataMarcacao");
		agruparGuias();
		guias.clear();
		agruparProcedimentos();
		gerarRelatorio();
		
	}


	private static void agruparProcedimentos() {
		System.out.println("Agrupando procedimentos");
		for (Date competencia : guiasPorMes.keySet()) {
			List<ProcedimentoCirurgico> procedimentosNaCompetencia = new ArrayList<ProcedimentoCirurgico>();
			for (GuiaInternacao guia : guiasPorMes.get(competencia)) {
				for (ProcedimentoCirurgico procedimento : guia.getProcedimentosCirurgicos()) {	
					if(isProcedimentoValido(procedimento)) {
						procedimentosNaCompetencia.add(procedimento);
					}
				}
			}
			procedimentosPorMes.put(competencia, procedimentosNaCompetencia);
		}
		
//		for (Date comp : procedimentosPorMes.keySet() ) {
//			for (ProcedimentoCirurgico procedimento : procedimentosPorMes.get(comp)) {
//				System.out.println(procedimento.getProcedimentoDaTabelaCBHPM().getDescricao() +" - "+ procedimento.getQuantidade());
//	
//			}
//		}
	}


	private static void gerarRelatorio() {
		
		System.out.println("Gerando relatorio...");
		Map<ProcedimentoCirurgico, Integer> procedimentoPorQuantidade = new HashMap<ProcedimentoCirurgico, Integer>();
		for (Date competencia : procedimentosPorMes.keySet()) {
			for (ProcedimentoCirurgico procedimento : procedimentosPorMes.get(competencia)) {
				if(!procedimentoPorQuantidade.containsKey(procedimento)) {
					procedimentoPorQuantidade.put(procedimento, procedimento.getQuantidade());
				}else {
					Integer quantidade = procedimentoPorQuantidade.get(procedimento);
					quantidade = quantidade + procedimento.getQuantidade();
					procedimentoPorQuantidade.put(procedimento, quantidade);
				}
			}
//			relatorio.put(competencia, procedimentoPorQuantidade);
		}
		
		System.out.println("PROCEDIMENTO;QUANTIDADE;VALOR UNITARIO");
		for (Procedimento procedimento : procedimentoPorQuantidade.keySet()) {
			System.out.print(procedimento.getProcedimentoDaTabelaCBHPM().getDescricao());
			System.out.print(";");
			System.out.print(procedimentoPorQuantidade.get(procedimento));
			System.out.print(";");
			System.out.println(procedimento.getValorTotal().setScale(2,BigDecimal.ROUND_HALF_UP));
		}
		
//		
//		for (Date competencia : relatorio.keySet()) {
//			System.out.print(";");
//			System.out.println(Utils.format(competencia));
//			System.out.println("PROCEDIMENTO;QUANTIDADE;VALOR UNITARIO");
//			Map<ProcedimentoCirurgico, Integer> map = relatorio.get(competencia);
//			for (Procedimento procedimento : map.keySet()) {
//				System.out.print(procedimento.getProcedimentoDaTabelaCBHPM().getDescricao());
//				System.out.print(";");
//				System.out.print(map.get(procedimento));
//				System.out.print(";");
//				System.out.println(procedimento.getValorTotal().setScale(2,BigDecimal.ROUND_HALF_UP));
//			}
//			System.out.println();
//		}
		
	}


	private static void agruparGuias() {
		System.out.println("Agrupando guias...");
		for (GuiaInternacao guia : guias) {
			Date competencia = CompetenciaUtils.getCompetencia(guia.getDataMarcacao());
			if(!guiasPorMes.containsKey(competencia)) {
				Set<GuiaInternacao> guiasDaCompetencia = new HashSet<GuiaInternacao>();
				guiasDaCompetencia.add(guia);
				guiasPorMes.put(competencia, guiasDaCompetencia);
			}else {
				guiasPorMes.get(competencia).add(guia);
			}
		}
	}


	private static List<GuiaInternacao> buscarGuias() {
		System.out.println("Buscando guias...");
		Criteria c = HibernateUtil.currentSession().createCriteria(GuiaInternacao.class);
		c.add(Expression.not(Expression.in("situacao.descricao", situacoes)));
		c.add(Expression.eq("prestador", SAO_MARCOS));	
		return c.list();
	}
	
	private static boolean  isProcedimentoValido(ProcedimentoCirurgico procedimento) {
		boolean isProcedimentoCancelado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao());
		boolean isProcedimentoNaoAutorizado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao());
		boolean isProcedimentoSolicitado = procedimento.getSituacao().getDescricao().equals(SituacaoEnum.SOLICITADO.descricao());
		boolean isProcedimentoValido = (!isProcedimentoCancelado && !isProcedimentoNaoAutorizado && !isProcedimentoSolicitado);
		
		return isProcedimentoValido;
	}
	
	
	public static void main2(String[] args) {
		Criteria c1 = HibernateUtil.currentSession().createCriteria(ProcedimentoCirurgico.class);
		c1.createAlias("procedimentoDaTabelaCBHPM", "t");
		c1.add(Expression.eq("t.descricao", "VISITA HOSPITALAR"));
		List<ProcedimentoCirurgico> procedimentos = c1.list();
		
		System.out.println(procedimentos.size());
		
		Set<ProcedimentoCirurgico> procedimentosDoMal = new HashSet<ProcedimentoCirurgico>();
		for (ProcedimentoCirurgico procedimento : procedimentos) {
			System.out.println(procedimentosDoMal.add(procedimento));
		}
	
	}
	
}
