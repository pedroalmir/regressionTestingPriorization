package br.com.infoway.ecare.services.tarefasCorrecao.migracaoNovaTabela;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;

public class MigracaoUCO {
	private static Map<String, BigDecimal> codesValue; 
	
	public static void main(String[] args) throws Exception {
		
		Session session = HibernateUtil.currentSession();
		Transaction transaction = session.beginTransaction();
		
		List<TabelaCBHPM> cbhpms = updateUCO();
		
		for (TabelaCBHPM tabelaCBHPM : cbhpms) {
			ImplDAO.save(tabelaCBHPM);
		}
		session.flush();
		transaction.commit();
		
		System.out.println("salvou!!!");
	}
	
	private static List<TabelaCBHPM> updateUCO()throws Exception{
		Workbook pasta = Workbook.getWorkbook(new File(ArquivoInterface.REPOSITORIO_MIGRACAO_TPSR+"migracaoUco.xls"));
		Sheet planilha = pasta.getSheet(0);
		
		codesValue = new HashMap<String, BigDecimal>();
		
		String code;
		BigDecimal value = new BigDecimal("0.000");
		System.out.println("linhas da planilha: "+planilha.getRows());
		for (int i = 1; i < planilha.getRows(); i++) {
			code = planilha.getCell(0,i).getContents();
			code = code.replace("-", "").replace(".", "");
			if (code.isEmpty()) {
				System.out.println("Codigo Vazio na planilha");
			}
			
			String contents = planilha.getCell(2,i).getContents();
			contents = contents.replace(",", ".");
			value = new BigDecimal("0.000");
			if (!contents.isEmpty()){
				value = new BigDecimal(contents).divide(new BigDecimal("11.500"),3, RoundingMode.HALF_DOWN);
			}
			BigDecimal put = codesValue.put(code, value);
			if(put != null){
				System.out.println("codigo duplicado na planilha: "+code);
			}
		}
		List<TabelaCBHPM> cbhpms = getCBHPMByCode(codesValue.keySet());
		
		for (TabelaCBHPM tabelaCBHPM : cbhpms) {
			BigDecimal uco = codesValue.get(tabelaCBHPM.getCodigo());
			if (uco == null){
				tabelaCBHPM.setUco(new BigDecimal("0.000"));
			} else {
				tabelaCBHPM.setUco(uco);
			}
		}
		
		return cbhpms;
	}
	
//	descomentar as linhas do método para saber quantos procedimentos da planilha não existem no banco
	private static List<TabelaCBHPM> getCBHPMByCode(Set<String> codes){
		SearchAgent sa = new SearchAgent();
//		sa.addParameter(new In("codigo", codes));
		List<TabelaCBHPM> result = sa.list(TabelaCBHPM.class);
		
//		int diferenca = codes.size()-result.size();
//		System.out.println("xls: "+codes.size());
//		System.out.println("banco: "+result.size());
//		System.out.println("xls - buscado no banco :"+diferenca);
		
		Set<String> codesBuscados = new HashSet<String>();
		for (TabelaCBHPM tabelaCBHPM : result) {
			codesBuscados.add(tabelaCBHPM.getCodigo());
		}
		
		Set<String> planilhaMenosBanco = new HashSet<String>(codes);
		planilhaMenosBanco.removeAll(codesBuscados);
		for (String string : planilhaMenosBanco) {
			System.out.println("Não encontrado: "+string+ "   Uco definido: " + codesValue.get(string));
		}
		
		return result;
	}

//	4 procedimentos duplicados
//	uma linha vazia
//	um código errado 307130053
//	24 procedimentos na planilha ki não existem no banco, deste 11 teriam valor uco <> 0;
	
}
