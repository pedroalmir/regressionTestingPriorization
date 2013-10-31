package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.honorario.HonorarioExterno;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoAnestesico;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.LowerEquals;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe de correção da situação dos procedimentos de acompanhamento anestésico que têm  situação Honorário Gerado(a), mas era pra ser Aguardando Cobrança.
 * @author Luciano Rocha
 * @since 21/02/2013
 */
public class ConformizacaoSituacaoProcedimentosDeHonorarioAnestesistas {

	public static void main(String[] args) {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();

		SearchAgent sa = new SearchAgent();
		
//		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao()));
		sa.addParameter(new Equals("situacao.dataSituacao", Utils.parse("30/01/2013")));
		
		List<Procedimento> procedimentos = sa.list(Procedimento.class);
		
		System.out.println("Qnt Honorários: " + procedimentos.size());
		
		int countProcedimentosCorrigidos = 0;
		for(Procedimento procedimento : procedimentos){
			if (procedimento != null && procedimento.isSituacaoAtual(SituacaoEnum.PROCEDIMENTO_COM_HONORARIO_GERADO.descricao())) {
				countProcedimentosCorrigidos++;
				Usuario usuario = ImplDAO.findById(1L, Usuario.class);
				procedimento.mudarSituacao(usuario, SituacaoEnum.AGUARDANDO_COBRANCA.descricao(), "Conformização de situação de procedimentos de anestesistas.", new Date());
			}
		}
		
		System.out.println("Quantidade de procedimento alterados: " + countProcedimentosCorrigidos);
		
//		tx.commit();
	}
}