package br.com.infowaypi.ecare.correcaomanual;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;

/**
 * @author SR Team - Marcos Roberto 11.07.2012
 * Responsável por listar todos os procedimentos odontológicos por especialidade.
 */
public class ProcedimentosOdontoPorEspecialidade {

	private static final String PATH = "../reformulacao_auditoria/arquivos/";
	
	private String hql;
	private Session sessao;
	private Query consulta;
	
	private static Map<String, List<TabelaCBHPM>> mapa = new HashMap<String, List<TabelaCBHPM>>();
	private static WritableWorkbook planilha;
	private static WritableSheet sheet;
	private static int linha; 

	@Before
	public void before() {
		sessao = HibernateUtil.currentSession();
	}
	
	@Test
	public void test() throws Exception {
		planilha = Workbook.createWorkbook(new File(PATH + "Listagem de Procedimentos Odontologicos por Especialidade.xls"));
		
		buscarEspecialidades();
		preencherMapa(consulta.list());
		listarResultado(mapa);
		
		if(planilha.getSheets().length > 0) {			
			planilha.write();       
			planilha.close();
		}
	}

	private void buscarEspecialidades() {
		hql = "";
		hql = "Select e from Especialidade e ";		
		hql = hql + "where e.ativa=true and e.classe=2 ";
		hql = hql + "order by e.descricao";
		
		consulta = sessao.createQuery(hql);

		System.out.print(consulta.list().size()+" registros buscados");
	}

	private void preencherMapa(List<Especialidade> lista) {
		System.out.print("Preenchendo mapa ... ");
		
		for (Especialidade especialidade : lista) {
			for (TabelaCBHPM procedimento : especialidade.getProcedimentosDaTabelaCBHPM()) {
				if(procedimento.getAtivo()) {
					if (!mapa.containsKey(especialidade.getDescricao())) {				
						mapa.put(especialidade.getDescricao(), new ArrayList<TabelaCBHPM>());
					}
					mapa.get(especialidade.getDescricao()).add(procedimento);
				}
			}
		}
	}
	
	private void listarResultado(Map<String, List<TabelaCBHPM>> mapa) {
		int i = 0;
		for (String especialidade : mapa.keySet()) {
			try {
				sheet = planilha.createSheet(especialidade, i);
				linha = 0;
				sheet.addCell(new Label(0, linha, "PROCEDIMENTOS"));
				linha++;
				
				sheet.addCell(new Label(0, linha, "CÓDIGO"));
				sheet.addCell(new Label(1, linha, "DESCRIÇÃO"));
				linha++;
				
				List<TabelaCBHPM> lista = mapa.get(especialidade);
				for (TabelaCBHPM procedimento : lista) {				
					sheet.addCell(new Label(0, linha, procedimento.getCodigo()));
					sheet.addCell(new Label(1, linha, procedimento.getDescricao()));
					linha++;
				}
				i++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@After
	public void after() {}
}