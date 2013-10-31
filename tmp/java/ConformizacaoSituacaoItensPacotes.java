package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.IsNull;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe de corre��o da situa��o dos itens pacotes que n�o estavam com a situa��o inicial Solicitado(a).
 * O erro foi causado porque a classe ItemPacote n�o mudava a situa��o do item para Solicitado(a) na sua
 * cria��o.
 * @author Luciano Rocha
 * @since 03/02/2013
 */
public class ConformizacaoSituacaoItensPacotes {

	public static void main(String[] args) {
		Transaction tx = HibernateUtil.currentSession().beginTransaction();

		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new IsNull("situacao"));
		
		List<ItemPacote> pacotes = sa.list(ItemPacote.class);
		
		System.out.println("Qnt pacotes encontrados: " + pacotes.size());
		
		int countPacotesCorrigidos = 0;
		
		System.out.println(" ########## PACOTES ALTERADOS ################");
		for (ItemPacote pacote : pacotes) {
			if (pacote.getGuia() != null) {
				System.out.println(pacote.getGuia().getAutorizacao());
			}
			countPacotesCorrigidos++;
			pacote.mudarSituacao(ImplDAO.findById(1L, Usuario.class), SituacaoEnum.SOLICITADO.descricao(), MotivoEnum.INCLUSAO_ITEM.getMessage(), new Date());
		}
		
		System.out.println("Quantidade de pacotes alterados: " + countPacotesCorrigidos);
		
		tx.commit();
	}
}