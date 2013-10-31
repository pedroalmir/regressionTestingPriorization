package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.Greater;
import br.com.infowaypi.msr.utils.Utils;

public class GuiaZerada {
	public static void main(String[] args) throws Exception {
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.FECHADO.descricao()));
		sa.addParameter(new Greater("situacao.dataSituacao", Utils.createData("01/01/2012")));
		sa.addParameter(new Equals("valorTotal", BigDecimal.ZERO.setScale(2)));
		List<GuiaCompleta<ProcedimentoInterface>> guias = sa.list(GuiaCompleta.class);
		
		int count = 0;
		 
		for(GuiaCompleta<ProcedimentoInterface> guia : guias){
			System.out.print(guia.getAutorizacao() + " " + guia.getSituacao().getDescricao() + " Valor antes:" + guia.getValorTotalFormatado());
			
			guia.recalcularValores();
			guia.updateValorCoparticipacao();
			
			System.out.println(" => Valor após recalculo: " + guia.getValorTotalFormatado());
			
			if(guia.getValorTotal() != BigDecimal.ZERO.setScale(2)){
				ImplDAO.save(guia);
				count++;
			}
			
		}
		
		System.out.println("Qtd guias encontradas: " + guias.size());
		System.out.println("Qtd guias corrigidas: " + count);
		
		tx.commit();
		
	}
}
