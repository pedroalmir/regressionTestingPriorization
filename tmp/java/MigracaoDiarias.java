package br.com.infoway.ecare.services.tarefasCorrecao.migracaoNovaTabela;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecarebc.atendimentos.Diaria;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoInterface;
import br.com.infowaypi.ecarebc.procedimentos.TipoAcomodacao;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

public class MigracaoDiarias {

	private static final File FILE = new File(ArquivoInterface.REPOSITORIO_MIGRACAO_TPSR+"tiposAcomodacoes.xls");
	private static final Map<String, Diaria> DIARIAS = new HashMap<String, Diaria>();
	private static final Map<String, TipoAcomodacao> TIPO = new HashMap<String, TipoAcomodacao>();

	public static void main(String[] args) throws Exception {

		Session session = HibernateUtil.currentSession();
		Transaction transaction = session.beginTransaction();
		
		createMapDiarias();
		createMapAcomodacoes();
		
		Workbook arquivo = Workbook.getWorkbook(FILE);
		Sheet planilha = arquivo.getSheet(0);
		int linha = 2;
		
		
		System.out.println("------------------------------------------------");
		for (String string : DIARIAS.keySet()) {
			System.out.println(string);
		}
		System.out.println("------------------------------------------------");
		
		while(true) {
			try {
				String codigoDescricao = planilha.getCell(3, linha).getContents().trim();
				if (Utils.isStringVazia(codigoDescricao)) {
					break;
				}
				System.out.print(codigoDescricao + " - ");
				Diaria diaria = DIARIAS.get(codigoDescricao);
				String apto = planilha.getCell(13, linha).getContents();
				if (!Utils.isStringVazia(apto)) {
					diaria.setTipoAcomodacao(TIPO.get("ENFERMARIA"));
					System.out.print("ENFERMARIA");
				}
				
				String uti = planilha.getCell(14, linha).getContents();
				if (!Utils.isStringVazia(uti)) {
					diaria.setTipoAcomodacao(TIPO.get("UTI"));
					System.out.print("UTI");
				}
				
				String berc = planilha.getCell(15, linha).getContents();
				if (!Utils.isStringVazia(berc)) {
					diaria.setTipoAcomodacao(TIPO.get("BERÇARIO"));
					System.out.print("BERÇARIO");
				}
				
				String day = planilha.getCell(16, linha).getContents();
				if (!Utils.isStringVazia(day)) {
					diaria.setTipoAcomodacao(TIPO.get("DAYCLINIC"));
					System.out.print("DAYCLINIC");
				}
				linha++;
				
				ImplDAO.save(diaria);
				System.out.println();
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
		}
		transaction.commit();
		session.close();
	}

	@SuppressWarnings("unchecked")
	private static void createMapAcomodacoes() {
		List<TipoAcomodacao> tipos = HibernateUtil.currentSession().createCriteria(TipoAcomodacao.class).list();
		for (TipoAcomodacao tipoAcomodacao : tipos) {
			TIPO.put(tipoAcomodacao.getDescricao(), tipoAcomodacao);
		}
		
		for (TipoAcomodacao tipoAcomodacao : TIPO.values()) {
			System.out.println(tipoAcomodacao);
		}
	}

	@SuppressWarnings("unchecked")
	private static void createMapDiarias() {
		List<Diaria> diarias = HibernateUtil.currentSession().createCriteria(Diaria.class)
										    .add(Expression.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()))
										    .list();

		for (Diaria diaria : diarias) {
			DIARIAS.put(diaria.getCodigoDescricao().trim(), diaria);
		}
	}
}
