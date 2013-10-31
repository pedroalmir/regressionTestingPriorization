package br.com.infoway.ecare.services.tarefasCorrecao.migracaoNovaTabela;

import java.io.File;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import br.com.infowaypi.ecarebc.procedimentos.CapituloCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.ClassificacaoCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.GrupoCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.SubgrupoCBHPM;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Like;

public class MigracaoClassificacaoCBHPM {
	
	public static void main(String[] args) throws Exception {
		Session sessao = HibernateUtil.currentSession();
		Transaction transacao = sessao.beginTransaction();
		
		Workbook arquivo = Workbook.getWorkbook(new File("C:/Users/Emanuel/WorkColmeia/SaudeRecife_Desenvolvimento/arquivos/novaTPSR/classificacaocbhpm.xls"));
		Sheet planilha = arquivo.getSheet(0);
		
		try {
			CapituloCBHPM capituloCache = null;
			GrupoCBHPM grupoCache = null;
			for (int i = 0; i < planilha.getRows(); i++) {
				Cell[] linha = planilha.getRow(i);
				String codigo = linha[1].getContents().trim();
				String descricao = linha[0].getContents().trim();
				if (codigo.isEmpty()) break;
				
				int tamanho = codigo.length();
				if (tamanho == 1) {
					capituloCache = salvarCapitulo(codigo, descricao);
				} else if (tamanho == 3) {
					grupoCache = salvarGrupo(capituloCache, codigo, descricao);
				} else if (tamanho == 5) {
					SubgrupoCBHPM subgrupo = criarSubgrupo(grupoCache, codigo, descricao);
					salvarProcedimentos(codigo, subgrupo);
				}
			}
			transacao.commit();
		} catch (Exception e) {
			e.printStackTrace();
			transacao.rollback();
		} finally {
			sessao.close();
		}
	}

	private static CapituloCBHPM salvarCapitulo(String codigo, String descricao)
			throws Exception {
		CapituloCBHPM capitulo = getClassificacao(CapituloCBHPM.class, codigo, descricao);
		capitulo.validate();
		ImplDAO.save(capitulo);
		return capitulo;
	}

	private static GrupoCBHPM salvarGrupo(CapituloCBHPM capituloCache,
			String codigo, String descricao) throws Exception {
		GrupoCBHPM grupo = getClassificacao(GrupoCBHPM.class, codigo, descricao);
		grupo.setCapituloCBHPM(capituloCache);
		grupo.validate();
		ImplDAO.save(grupo);
		return grupo;
	}

	@SuppressWarnings("unchecked")
	private static List<TabelaCBHPM> salvarProcedimentos(
			String codigo, SubgrupoCBHPM subgrupo) throws Exception {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Like("codigo", codigo + "%"));
		List<TabelaCBHPM> procedimentos = sa.list(TabelaCBHPM.class);
		for (TabelaCBHPM tabelaCBHPM : procedimentos) {
			tabelaCBHPM.setSubgrupoCBHPM(subgrupo);
			ImplDAO.save(tabelaCBHPM);
		}
		return procedimentos;
	}

	private static SubgrupoCBHPM criarSubgrupo(GrupoCBHPM grupoCache,
			String codigo, String descricao) throws Exception {
		SubgrupoCBHPM subgrupo = getClassificacao(SubgrupoCBHPM.class, codigo, descricao);
		subgrupo.setGrupoCBHPM(grupoCache);
		subgrupo.validate();
		ImplDAO.save(subgrupo);
		return subgrupo;
	}
	
	private static <C extends ClassificacaoCBHPM> C getClassificacao(Class<C> klass, String codigo, String descricao) throws Exception {
		C result = klass.newInstance();
		result.setCodigo(codigo);
		result.setDescricao(descricao);
		return result;
	}
	
}
