package br.com.infowaypi.ecare.correcaomanual;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.situations.SituacaoInterface;

public class CorrecaoGuiasSemDataDeTerminioDeAtendimento_AlterarSituacao {

	public static void main(String[] args) throws Exception {
		String[] autorizacoes = new String[]{"983912","995473","112597","135136","1249797","900135","858161","972323B"};

		SearchAgent sa = new SearchAgent();
		sa.addParameter(new In("autorizacao", Arrays.asList(autorizacoes)));
		
		List<GuiaSimples> guiasCorrigir = sa.list(GuiaSimples.class);
		
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		for(GuiaSimples guia : guiasCorrigir){
			Set<SituacaoInterface> situacoes = guia.getSituacoes();
			boolean encontrou = false;
			for(SituacaoInterface situacao : situacoes){
				if(!encontrou &&(situacao.getUsuario().getLogin().equals("ti") || situacao.getUsuario().getLogin().equals("aurinosuporte"))){
					encontrou = true;
					System.out.println(guia.getAutorizacao() + " - " + situacao.getUsuario().getLogin() + " - " + guia.getDataTerminoAtendimento() + " - " + guia.getDataRecebimento());
//					guia.setDataTerminoAtendimento(situacao.getDataSituacao());
//					ImplDAO.save(guia);
				}
			}
		}
		
//		tx.commit();
	}
	
}
