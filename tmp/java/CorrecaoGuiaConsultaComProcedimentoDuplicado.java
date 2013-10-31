package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Greater;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class CorrecaoGuiaConsultaComProcedimentoDuplicado {

	private static List<GuiaConsulta> getGuiasAptasACorrecao(){ 
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new GreaterEquals("dataMarcacao", Utils.createData("21/10/2011")));
		sa.addParameter(new Greater("quantidadeProcedimentos", 1));
		return sa.list(GuiaConsulta.class);
	}

	private static void corrigirGuias() {
		List<GuiaConsulta> guiasAptasACorrecao = getGuiasAptasACorrecao();
		System.out.println("Qtd de guias aptas a correção: " + guiasAptasACorrecao.size());
		for(GuiaConsulta<Procedimento> g : guiasAptasACorrecao){
			System.out.println("Guia: " + g.getAutorizacao());
			for(Iterator<Procedimento> it = g.getProcedimentosAtivos().iterator(); it.hasNext();){
				Procedimento procedimento = it.next();
				System.out.println("Procedimento: " + procedimento.getProcedimentoDaTabelaCBHPM().getCodigo());
				if(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo().equals("10101043")){
					procedimento.mudarSituacao((UsuarioInterface)new SearchAgent().findById(1L, Usuario.class), SituacaoEnum.CANCELADO.descricao(), "Remoção de procedimento indevido na guia.", new Date());
					System.out.println("...procedimento cancelado.");
				}
			}
			
			g.updateValorCoparticipacao();
			g.recalcularValores();
			
			System.out.println("------------------------------------------------------------");
		}
	}
	
	public static void main(String[] args) {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		corrigirGuias();
		
//		tx.commit();
	}
}
