package br.com.infowaypi.ecare.correcaomanual;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;

public class ConformizacaoGuiasComAcordoProcedimentosIncorretosSegundaParte {

	
	private static List<String> getAutorizacoes() throws FileNotFoundException{ 
		Scanner sc = new Scanner(new File("/home/wislanildo/workspaces/SaudeRecife/DeployValendo/trunk/arquivos/ConformizacaoGuiasComAcordoProcedimentosIncorretosSegundaParte.txt"));
		List<String> autorizacoes = new ArrayList<String>();
		while(sc.hasNext()){
			autorizacoes.add(sc.nextLine());
		}
		return autorizacoes;
	}
	
	private static List<GuiaConsultaUrgencia> getGuiasAptasACorrecao() throws FileNotFoundException{ 
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("autorizacao", getAutorizacoes()));
		return sa.list(GuiaConsultaUrgencia.class);
	}
	
	private static void corrigirGuias() throws Exception {
		int countProcedimentosCorrigidos = 0;
		
		List<GuiaConsultaUrgencia> guiasAptasACorrecao = getGuiasAptasACorrecao();
		System.out.println("Qtd de guias aptas a correção: " + guiasAptasACorrecao.size() + "\n");
		
		for(Iterator<GuiaConsultaUrgencia> itGuia = guiasAptasACorrecao.iterator(); itGuia.hasNext();){
			GuiaConsultaUrgencia guia = itGuia.next();
			
			if(guia.getValorTotalApresentado() != null){
				System.out.println("Guia " + guia.getAutorizacao() + " teve o valo total apresentado corrigido.");
				BigDecimal valorCorreto = guia.getValorTotalApresentado().subtract(new BigDecimal("20.00"));
//				guia.setValorTotalApresentado(valorCorreto); 
			}
			
			countProcedimentosCorrigidos++;
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		corrigirGuias();
		tx.commit();
	}
}
