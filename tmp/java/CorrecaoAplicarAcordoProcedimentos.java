package br.com.infowaypi.ecare.correcaomanual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.utils.Utils;

public class CorrecaoAplicarAcordoProcedimentos {

	public static void main(String[] args) throws Exception {
//		recalcularGuias();
		
//		dezfaserAlteracaoProcedientos();
	}

	private static void desfazerAlteracaoProcedientos() throws FileNotFoundException, Exception {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("idProcedimento", getProcedimentosCorrigir()));
		List<Procedimento> procedimentos = sa.list(Procedimento.class);
		
		for(Procedimento p : procedimentos){
			System.out.println(p.getIdProcedimento() + " - " + p.getProcedimentoDaTabelaCBHPM().getDescricaoFormatado() + " - " + p.getValorAtualDoProcedimentoFormatado());
			p.setValorAtualDoProcedimento(new BigDecimal(80.00));
			ImplDAO.save(p);
		}
		
		tx.commit();
	}

	private static void recalcularGuias() throws FileNotFoundException, Exception {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("autorizacao", getGuiasCorrigir()));
		sa.addParameter(new GreaterEquals("dataAtendimento", CompetenciaUtils.getInicioCompetencia(Utils.createData("01/09/2012"))));
		List<GuiaSimples> guias = sa.list(GuiaSimples.class);
		
		System.out.println("Somatório das guias antes da intervenção: " + getSomatorioGuia(guias));
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		for(GuiaSimples guia : guias){
			System.out.println(guia.getAutorizacao());
			guia.recalcularValores();
			guia.updateValorCoparticipacao();
			ImplDAO.save(guia);
		}
		
		System.out.println("Somatório das guias depois da intervenção: " + getSomatorioGuia(guias));
		
		tx.commit();
	}
	
	private static BigDecimal getSomatorioGuia(List<GuiaSimples> guias){
		BigDecimal total = BigDecimal.ZERO;
		for(GuiaSimples guia : guias){
			total = total.add(guia.getValorTotal());
		}
		return total;
	}
	
	private static List<String> getGuiasCorrigir() throws FileNotFoundException {
		List<String> autorizacoes = new ArrayList<String>();
		File file = new File("C://Users//wislanildo//Desktop//Ajuste procedimento por fora do acordo//guiaRecalcular.txt");
		Scanner sc = new Scanner(new FileReader(file));
		
		while(sc.hasNext()) {
			autorizacoes.add(sc.nextLine());
		}
		
		return autorizacoes;
	}
	
	private static List<Long> getProcedimentosCorrigir() throws FileNotFoundException {
		List<Long> autorizacoes = new ArrayList<Long>();
		File file = new File("C://Users//wislanildo//Desktop//Ajuste procedimento por fora do acordo//retornarValorProcedimento.txt");
		Scanner sc = new Scanner(new FileReader(file));
		
		while(sc.hasNext()) {
			autorizacoes.add(new Long(sc.nextLine()));
		}
		
		return autorizacoes;
	}
}
