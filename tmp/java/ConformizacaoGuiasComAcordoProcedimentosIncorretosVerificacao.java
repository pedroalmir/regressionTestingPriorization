package br.com.infowaypi.ecare.correcaomanual;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaUrgencia;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

public class ConformizacaoGuiasComAcordoProcedimentosIncorretosVerificacao {

	
	private static List<GuiaConsultaUrgencia> getGuiasAptasACorrecao(){ 
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new GreaterEquals("dataMarcacao", Utils.createData("21/10/2011")));
		sa.addParameter(new Equals("prestador.pessoaJuridica.fantasia", "SAO SALVADOR - MEIOS DE PROMOCAO"));
		return sa.list(GuiaConsultaUrgencia.class);
	}
	
	private static void corrigirGuias() {
		int countProcedimentosAptosACorrecao = 0;
		
		List<GuiaConsultaUrgencia> guiasAptasACorrecao = getGuiasAptasACorrecao();
		System.out.println("Qtd de guias aptas a verificação: " + guiasAptasACorrecao.size() + "\n");
		
		for(Iterator<GuiaConsultaUrgencia> itGuia = guiasAptasACorrecao.iterator(); itGuia.hasNext();){
			GuiaConsultaUrgencia guia = itGuia.next();
			
			
			for(Iterator<ProcedimentoInterface> itProcedimento = guia.getProcedimentosAtivos().iterator(); itProcedimento.hasNext();){
				ProcedimentoInterface procedimento = itProcedimento.next();
				
				
				if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals("10101040") 
						|| procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals("10101043")){
					
					if(MoneyCalculation.compare(procedimento.getValorAtualDoProcedimento(), new BigDecimal(80.00)) == 0){
						countProcedimentosAptosACorrecao++;
						
						//Aplicando o valor do acordo
						procedimento.aplicaValorAcordo();
						
						System.out.println("Guia: " + guia.getAutorizacao() + " Procedimento: " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigo() + " Especialidade da guia: " + guia.getEspecialidade().getDescricao());
					}
					
				}
			}
			
			guia.updateValorCoparticipacao();
			guia.recalcularValores();
		}
		
		System.out.println("Qtd de procedimentos aptos a correção: " + countProcedimentosAptosACorrecao);
	}
	
	public static void main(String[] args) {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		corrigirGuias();
//		tx.commit();
	}
}
