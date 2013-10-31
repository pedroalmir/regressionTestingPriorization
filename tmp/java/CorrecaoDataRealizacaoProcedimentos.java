package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;

import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoCirurgico;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.user.Usuario;

/**
 * Classe de correção dos procedimentos quanto à data de realização.
 * @author Luciano Rocha
 * @since 03/02/2013
 */
public class CorrecaoDataRealizacaoProcedimentos {

	public static void main(String[] args) {
		
		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		
		Usuario usuario = ImplDAO.findById(1L, Usuario.class);

		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new In("autorizacao", "1880286", "1913706"));

		List<GuiaCompleta> guias = sa.list(GuiaCompleta.class);
		for (GuiaCompleta guia : guias) {
			for (Object diaria : guia.getItensDiaria(SituacaoEnum.AUTORIZADO.descricao())) {
				((ItemDiaria) diaria).setDataInicial(guia.getSituacao(SituacaoEnum.ABERTO.descricao()).getDataSituacao());
				System.out.println(((ItemDiaria) diaria).getDataInicialFormatada());
				System.out.println(((ItemDiaria) diaria).getDataFinalFormatada());
			}
			
		
		sa.addParameter(new In("idProcedimento", 38393443L, 38393459L, 38393446L));
		
		List<ProcedimentoCirurgico> procedimentos = sa.list(ProcedimentoCirurgico.class);
		
		for (ProcedimentoCirurgico proc : procedimentos) {
			proc.mudarSituacao(usuario, SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao(), "COBRANCA DE PROCEDIMENTO CONFORME AUDITORIA EXTERNA", new Date());
			proc.setDataRealizacao(proc.getGuia().getDataAtendimento());
			try {
				ImplDAO.save(guia);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		tx.commit();
		
//		sa.addParameter(new IsNull("dataRealizacao"));
//		sa.addParameter(new GreaterEquals("situacao.dataSituacao", Utils.parse("01/11/2012")));
//		
//		List<ProcedimentoCirurgico> procedimentos = sa.list(ProcedimentoCirurgico.class);
//		
//		System.out.println("Qnt proc: " + procedimentos.size());
//		for (Procedimento proc : procedimentos) {
//			System.out.println("Procedimento: " + proc.getIdProcedimento());
//			System.out.println("Data atual: " + proc.getDataRealizacao());
//			if (proc.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null) {
//				proc.setDataRealizacao(proc.getSituacao(SituacaoEnum.AUTORIZADO.descricao()).getDataSituacao());
//			}
//			System.out.println("Data depois: " + proc.getDataRealizacao());
//			try {
//				ImplDAO.save(proc);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
//		sa.addParameter(new IsNull("dataRealizacao"));
//		sa.addParameter(new GreaterEquals("situacao.dataSituacao", Utils.parse("01/11/2012")));
//		
//		List<ProcedimentoOutros> procedimentos = sa.list(ProcedimentoOutros.class);
//		
//		System.out.println("Qnt proc: " + procedimentos.size());
//		for (Procedimento proc : procedimentos) {
//			System.out.println("Procedimento: " + proc.getIdProcedimento());
//			System.out.println("Data atual: " + proc.getDataRealizacao());
//			if (proc.getSituacao(SituacaoEnum.AUTORIZADO.descricao()) != null) {
//				proc.setDataRealizacao(proc.getSituacao(SituacaoEnum.AUTORIZADO.descricao()).getDataSituacao());
//			}
//			if (proc.getSituacao(SituacaoEnum.SOLICITADO.descricao()) != null) {
//				proc.setDataRealizacao(proc.getSituacao(SituacaoEnum.SOLICITADO.descricao()).getDataSituacao());
//			}
//			System.out.println("Data depois: " + proc.getDataRealizacao());
//			try {
//				ImplDAO.save(proc);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		}
	}
}
