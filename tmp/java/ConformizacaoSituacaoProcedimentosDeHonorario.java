package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe de correção da situação dos procedimentos que têm Honorário na situação Gerado(a).
 * @author Luciano Rocha
 * @since 28/01/2013
 */
public class ConformizacaoSituacaoProcedimentosDeHonorario {

	public static void main(String[] args) {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();

		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.GERADO.descricao()));
		sa.addParameter(new GreaterEquals("situacao.dataSituacao", Utils.parse("01/11/2012")));
		
		List<HonorarioExterno> honorarios = sa.list(HonorarioExterno.class);
		
		System.out.println("Qnt Honorários: " + honorarios.size());
		
		int countProcedimentosCorrigidos = 0;
		for(HonorarioExterno honorario : honorarios){
			ProcedimentoInterface procedimento = honorario.getProcedimento();
			if (procedimento != null && procedimento.isSituacaoAtual(SituacaoEnum.AGUARDANDO_COBRANCA.descricao())) {
				countProcedimentosCorrigidos++;
				Usuario usuario = ImplDAO.findById(1L, Usuario.class);
				procedimento.mudarSituacao(usuario, SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao(), "Conformização de situação de procedimentos de honorários.", new Date());
			}
		}
		
		System.out.println("Quantidade de procedimento alterados: " + countProcedimentosCorrigidos);
		
//		tx.commit();
	}
}