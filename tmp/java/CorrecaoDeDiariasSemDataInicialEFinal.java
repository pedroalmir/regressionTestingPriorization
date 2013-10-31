package br.com.infowaypi.ecare.correcaomanual;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe de correção das diárias que não estavam com as datas inicial e/ou final.
 * @author Luciano Rocha
 * @since 28/05/2013
 */
public class CorrecaoDeDiariasSemDataInicialEFinal {

	public static void main(String[] args) {
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new In("autorizacao", "2497", "2561"));
		
		List<GuiaCompleta> guias = sa.list(GuiaCompleta.class);
		List<GuiaCompleta> guiasComUmaDiaria = new ArrayList<GuiaCompleta>();
		List<GuiaCompleta> guiasComMaisDiarias = new ArrayList<GuiaCompleta>();
		
		for (GuiaCompleta guia : guias) {
			
			if (guia.getItensDiaria().size() == 1) {
				guiasComUmaDiaria.add(guia);
			}
			else {
				guiasComMaisDiarias.add(guia);
			}
			
		}

		System.out.println("Guias com uma diária corrida: " + guiasComUmaDiaria.size());
		for (GuiaCompleta guia : guiasComUmaDiaria) {
			System.out.println(guia.getAutorizacao());
			corrigirGuiasComUmaDiaria(guia);
			try {
				ImplDAO.save(guia);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Guias com mais de uma diária corridas: " + guiasComMaisDiarias.size());
		for (GuiaCompleta guia2 : guiasComMaisDiarias) {
			System.out.println(guia2.getAutorizacao());
			corrigirGuiasComMaisDeUmaDiaria(guia2);
			try {
				ImplDAO.save(guia2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		tx.commit();
	}

	private static void corrigirGuiasComUmaDiaria(GuiaCompleta guia) {
		Set<ItemDiaria> itensDiaria = guia.getItensDiaria();
		for (ItemDiaria item : itensDiaria) {
			item.recalcularCampos();
			geraDataPrimeiraDiaria(guia, item);
			System.out.println("Valor atual da diária: " + item.getValor());
		}
	}
	
	private static void corrigirGuiasComMaisDeUmaDiaria(GuiaCompleta guia){
		Set<ItemDiaria> itensSet = guia.getItensDiaria();
		
		List<ItemDiaria> itens = new ArrayList<ItemDiaria>();
		for (ItemDiaria itemDiaria : itensSet) {
			itemDiaria.recalcularCampos();
			itens.add(itemDiaria);
			System.out.println("Valor atual da diária: " + itemDiaria.getValor());
		}
		Utils.sort(itens, false, "situacao.dataSituacao");
		
		ItemDiaria diaria = itens.get(0);
		
		geraDataPrimeiraDiaria(guia, diaria);
		
		geraDataDemaisDiarias(itens, diaria);
		
		for (ItemDiaria d : itens){
			System.out.println("Data inicial da diária: " + d.getDataInicialFormatada());
			System.out.println("Data final da diária: " + d.getDataFinalFormatada());
		}
	}

	private static void geraDataDemaisDiarias(List<ItemDiaria> itens, ItemDiaria diaria) {
		int tam = itens.size();
		for (int i = 1; i < tam; i++) {
			if (i > 0) {
				diaria = itens.get(i);
			}
			diaria.setDataInicial(itens.get(i - 1).getDataFinal());
		}
	}

	private static void geraDataPrimeiraDiaria(GuiaCompleta guia, ItemDiaria primeiraDiaria) {
		if (guia.getDataAtendimento() != null) {
			primeiraDiaria.setDataInicial(guia.getDataAtendimento());
		}
		else if (primeiraDiaria.getSituacao(SituacaoEnum.SOLICITADO.descricao()) != null){
			primeiraDiaria.setDataInicial(primeiraDiaria.getSituacao(SituacaoEnum.SOLICITADO.descricao()).getDataSituacao());
		}
	}
}
