package br.com.infoway.ecare.services.tarefasCorrecao.migracaoNovaTabela;

import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

public class MigracaoProcedimentosIncompativeis {
	
	private static Map<TabelaCBHPM, Set<TabelaCBHPM>> mapProcedimentos = new HashMap<TabelaCBHPM, Set<TabelaCBHPM>>();
	
	public static void main(String[] args) throws Exception {
		Transaction tm = HibernateUtil.currentSession().beginTransaction();
		
		Scanner sc = new Scanner(new FileReader(ArquivoInterface.REPOSITORIO_MIGRACAO_TPSR+"procedimentosIncompativeis.csv"));
		String linha = "";
		String codigoProcedimento = "";
		String codigoProcedimentoIncompativel = "";
		
		SearchAgent sa = new SearchAgent();
		
		while(sc.hasNext()) {
			linha = sc.nextLine();
			StringTokenizer token = new StringTokenizer(linha,";");
			codigoProcedimento = token.nextToken().replace(" ", "");
			codigoProcedimentoIncompativel = token.nextToken().replace(" ", "");
			
			sa.addParameter(new Equals("codigo", codigoProcedimento));
			TabelaCBHPM tabela = sa.uniqueResult(TabelaCBHPM.class);
			
			sa.clearAllParameters();
			sa.addParameter(new Equals("codigo", codigoProcedimentoIncompativel));
			TabelaCBHPM tabelaIncompativel = sa.uniqueResult(TabelaCBHPM.class);
			
			if(!mapProcedimentos.keySet().contains(tabela)) {
				Set<TabelaCBHPM> incompativeis = new HashSet<TabelaCBHPM>();
				incompativeis.add(tabelaIncompativel);
				mapProcedimentos.put(tabela, incompativeis);
			}else {
				mapProcedimentos.get(tabela).add(tabelaIncompativel);
			}
			
			sa.clearAllParameters();
		}
		
		boolean temIncompativel;
		for (TabelaCBHPM key : mapProcedimentos.keySet()) {
			temIncompativel = false;
			System.out.println("PROCEDIMENTO: " + key.getCodigoEDescricao());
			key.setProcedimentosIncompativeis(mapProcedimentos.get(key));
			ImplDAO.save(key);
			for (TabelaCBHPM inc : mapProcedimentos.get(key)) {
				temIncompativel = true;
				System.out.println("INCOMPATIVEL: " + inc.getCodigoEDescricao());
			}
			if(!temIncompativel) {
				throw new RuntimeException ("Erro: Um procedimento sem procedimento incompativel: "+ key.getCodigo());
			}
			System.out.println("----------");
		}
		
		HibernateUtil.currentSession().flush();
		
		System.out.println("MIGRACAO FEITA!");
		System.out.println("Recuperando objetos migrados...");
		
		List<TabelaCBHPM> procedimentos = HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class)
			.add(Expression.isNotEmpty("procedimentosIncompativeis"))
			.list();
		
		System.out.println("Objetos migrados: " + procedimentos.size());
		
		System.out.println("Caminho de volta reconstruido para: ");
		for (TabelaCBHPM procedimento : procedimentos) {
			for (TabelaCBHPM inc : procedimento.getProcedimentosIncompativeis()) {
				if(!inc.getProcedimentosIncompativeis().contains(procedimento)) {
					inc.getProcedimentosIncompativeis().add(procedimento);
					System.out.println(inc.getCodigoEDescricao());
					System.out.println(procedimento.getCodigoEDescricao());
					System.out.println();
				}
			}
			System.out.println("----");
		}
		
		tm.commit();
		System.out.println("Fim!");
	}
}
