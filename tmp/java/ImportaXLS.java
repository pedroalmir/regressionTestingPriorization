package br.com.infowaypi.ecare.correcaomanual;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.infowaypi.ecare.cadastros.GrupoMotivoGlosa;
import br.com.infowaypi.ecare.cadastros.ItemAplicavel;
import br.com.infowaypi.ecare.cadastros.MotivoGlosa;
import br.com.infowaypi.ecare.scheduller.CreateDataBase;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.utils.Utils;

public class ImportaXLS {
    
	@SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        
		CreateDataBase.main(null);
        Session sessao = HibernateUtil.currentSession();
        Workbook arquivo = Workbook.getWorkbook(new File("D:\\Infoway\\Sprint 54 - SR\\motivos.xls"));
        Sheet planilha = arquivo.getSheet(0);
        
        Transaction transacao = sessao.beginTransaction();
        Map<String, ItemAplicavel> mapItemAplicavel = new HashMap<String, ItemAplicavel>();

        //Inicia a persistencia dos itens aplicáveis.
        Set<ItemAplicavel> itens = new HashSet<ItemAplicavel>();

        for (int i = 1; i < planilha.getRows(); i++) {
        	Cell[] linha = planilha.getRow(i);
        	ItemAplicavel item = new ItemAplicavel();
        	String descricaoItemAplicavel = linha[0].getContents().trim();
        	if (descricaoItemAplicavel == "") {
        		item.setDescricao("Todos(as)");
        	} else {
        		item.setDescricao(descricaoItemAplicavel);
        	}
        	itens.add(item);
        }
 
        for (ItemAplicavel item : itens) {
        	mapItemAplicavel.put(item.getDescricao(), item);
        }
        
        //Inicia a persistencia dos motivos.
        Map<String, Set<MotivoGlosa>> map = new HashMap<String, Set<MotivoGlosa>>();
        
        try {
            for (int i = 1; i < planilha.getRows(); i++) {
                Cell[] linha = planilha.getRow(i);
                String descricaoItemAplicavel = linha[0].getContents().trim();
                descricaoItemAplicavel = descricaoItemAplicavel == "" ? "Todos(as)" : descricaoItemAplicavel;
                ItemAplicavel item = mapItemAplicavel.get(descricaoItemAplicavel);
                String observacao = linha[1].getContents().trim();
                String grupo = linha[2].getContents().trim();
                String codigo = linha[3].getContents().trim();
                String descricao = linha[4].getContents().trim();
                descricao = Utils.removeAccents(descricao).toUpperCase();
                if (descricao.isEmpty()) continue;
                if (map.containsKey(grupo)) {
                    if (map.get(grupo) == null) {
                        map.put(grupo, new HashSet<MotivoGlosa>());
                        map.get(grupo).add(gerarMotivoGlosa(codigo, observacao, descricao, item));
                    } else {
                        map.get(grupo).add(gerarMotivoGlosa(codigo, observacao, descricao, item));
                    }
                } else {
                    map.put(grupo, new HashSet<MotivoGlosa>());
                    map.get(grupo).add(gerarMotivoGlosa(codigo, observacao, descricao, item));
                }
            }
            
            for (Entry<String, Set<MotivoGlosa>> motivosAgrupadosPorGrupo : map.entrySet()) {
                salvarGrupo(motivosAgrupadosPorGrupo.getKey(), motivosAgrupadosPorGrupo.getValue());
            }
            transacao.commit();
        } catch (Exception e) {
            e.printStackTrace();
            transacao.rollback();
        } finally {
            sessao.close();
        }
    }
    private static void salvarGrupo(String descricao, Set<MotivoGlosa> motivos) throws Exception {
        GrupoMotivoGlosa grupo = new GrupoMotivoGlosa();
        grupo.setDescricao(descricao);        
        ImplDAO.save(grupo);
        
        for (MotivoGlosa motivo : motivos) {
            motivo.setGrupo(grupo);
            ImplDAO.save(motivo);
            grupo.setMotivoGlosa(motivo);
            ImplDAO.save(grupo);
        }
    }
    private static MotivoGlosa gerarMotivoGlosa(String codigo, String observacao, String descricao, ItemAplicavel item) throws Exception {
        MotivoGlosa motivo = new MotivoGlosa();
        motivo.setDescricao(descricao);
        motivo.setObservacao(observacao);
        motivo.setCodigoMensagem(Integer.parseInt(codigo));
        motivo.setAtivo(true);
        motivo.getItensGuiaAplicaveis().add(item);
        return motivo;
    }
}