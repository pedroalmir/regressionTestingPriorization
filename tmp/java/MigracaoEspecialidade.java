package br.com.infoway.ecare.services.tarefasCorrecao.migracaoNovaTabela;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;


import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

public class MigracaoEspecialidade {
	
	
	private static final String TODAS_AS_ESPCIALIDADES = "TODAS AS ESPCIALIDADES";
	private static final String NÃO_PARAMETRIZA = "não parametriza";
	private static Map<String, Set<Especialidade>> mapEquivalencia = new HashMap<String, Set<Especialidade>>();
	private static Map<String, String> mapProcedimentoEspecialidade = new HashMap<String, String>();
	private static String PATH_ARQUIVO_EQUIVALENCIA = ArquivoInterface.REPOSITORIO_MIGRACAO_TPSR+"Equivalencia.CSV";
	private static String PATH_ARQUIVO_PROCEDIMENTO_ESPECIALIDADE = ArquivoInterface.REPOSITORIO_MIGRACAO_TPSR+"procedimento_especialidade.csv";
	private static List<Especialidade> todasAsEspcecialidades = new ArrayList<Especialidade>();
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		Transaction tm= HibernateUtil.currentSession().beginTransaction();
		
		todasAsEspcecialidades = HibernateUtil.currentSession().createCriteria(Especialidade.class).list();
		System.out.println("Especialidades: " + todasAsEspcecialidades.size());
		
		Scanner sc = new Scanner(new FileReader(PATH_ARQUIVO_EQUIVALENCIA));
		
		String linha = "";
		while(sc.hasNext()) {
			linha = sc.nextLine();
			StringTokenizer token = new StringTokenizer(linha,";");
			String key = token.nextToken();
			if(!mapEquivalencia.keySet().contains(key)) {
				mapEquivalencia.put(key, getEspecialidades(token));
			}else {
				for (Especialidade especialidade : getEspecialidades(token)) {
					mapEquivalencia.get(key).add(especialidade);
				}
			}
		}
		
		for (String chave : mapEquivalencia.keySet()) {
			System.out.print(chave + ";");
			for (Especialidade especialidade : mapEquivalencia.get(chave)) {
				if(especialidade != null)
					System.out.print(especialidade.getDescricao() + ";");
			}
			System.out.println();
		}
		
		sc = new Scanner(new FileReader(PATH_ARQUIVO_PROCEDIMENTO_ESPECIALIDADE));
		
		SearchAgent sa = new SearchAgent();
		TabelaCBHPM procedimento = null;
		String codigoProcedimento = "";
		String especialidade = "";
		while(sc.hasNext()) {
			linha = sc.nextLine();
			StringTokenizer token = new StringTokenizer(linha,";");
			codigoProcedimento = token.nextToken();
			especialidade = token.nextToken();
			mapProcedimentoEspecialidade.put(codigoProcedimento, especialidade);
		}	
		
		List<TabelaCBHPM> procedimentos = HibernateUtil.currentSession().createCriteria(TabelaCBHPM.class)
			.add(Expression.in("codigo", mapProcedimentoEspecialidade.keySet()))
		.list();
		
		for (TabelaCBHPM tabelaCBHPM : procedimentos) {
			if(mapEquivalencia.get(mapProcedimentoEspecialidade.get(tabelaCBHPM.getCodigo())) != null && mapEquivalencia.get(mapProcedimentoEspecialidade.get(tabelaCBHPM.getCodigo())).size() > 0) {
				tabelaCBHPM.setEspecialidades(
						mapEquivalencia.get(
								mapProcedimentoEspecialidade.get(
										tabelaCBHPM.getCodigo())));
				ImplDAO.save(tabelaCBHPM);
				
			}
		}
		
		tm.commit();
	}
	
	private static Set<Especialidade> getEspecialidades(StringTokenizer token) {
		Set<Especialidade> especialidades = new HashSet<Especialidade>();
		SearchAgent sa = new SearchAgent();
		String especialidadeToken = "";
		StringBuffer buffer = new StringBuffer();
		while(token.hasMoreTokens()) {
			especialidadeToken = token.nextToken();
			buffer.append(especialidadeToken);
			if(!especialidadeToken.equals(NÃO_PARAMETRIZA)) {
				if(especialidadeToken.equals(TODAS_AS_ESPCIALIDADES)) {
					especialidades.addAll(todasAsEspcecialidades);
					continue;
				}
				sa.addParameter(new Equals("descricao", especialidadeToken));
				Especialidade especialidade = sa.uniqueResult(Especialidade.class);
				especialidades.add(especialidade);
				sa.clearAllParameters();
			}
		}
		return especialidades;
	}

}
