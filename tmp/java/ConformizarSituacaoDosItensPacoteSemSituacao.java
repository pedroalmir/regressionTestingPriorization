package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemGuia;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemPacote;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.IsNull;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class ConformizarSituacaoDosItensPacoteSemSituacao {

	public static void main(String[] args) {
		Transaction tx = HibernateUtil.currentSession().beginTransaction(); 

		SearchAgent sa = new SearchAgent();
		
		List<ItemGuia> itensPacote = getItemPacoteComSituacaoNula(sa, ItemPacote.class); 
		System.out.println(itensPacote.size());
		System.out.println();
		
		Set<String> situacoes = new HashSet<String>();
		Set<String> itensPacoteCorrigidosEAutorizacaoGuia = new HashSet<String>();
		
		int contabilizadas = 0;
		int naoContabilizadas = 0;
		
		for(ItemGuia item : itensPacote){
			GuiaCompleta guia = ((ItemPacote) item).getGuia();
			if(guia != null){
				contabilizadas++;
				situacoes.add(guia.getSituacao().getDescricao());
				itensPacoteCorrigidosEAutorizacaoGuia.add(item.getIdItemGuia() + " - " + guia.getAutorizacao());
				item.mudarSituacao(
						(UsuarioInterface)ImplDAO.findById(1L, Usuario.class), 
						SituacaoEnum.SOLICITADO.descricao(), 
						"Conformização dos itens pacote sem situação.", 
						new Date());
			}else {
				naoContabilizadas++;
			}
		}
		
		System.out.println("Contabilizados: " + contabilizadas);
		System.out.println("Não contabilizados (Item pacotes se referencia a guia): " + naoContabilizadas);
		System.out.println();
		
		for(String st : situacoes){
			System.out.println(st);
		}
		
		System.out.println();
		System.out.println("Guias corrigidas:");
		System.out.println("Id item pacote | Autorização guia");
		
		for(String st : itensPacoteCorrigidosEAutorizacaoGuia){
			System.out.println(st);
		}
		
		tx.commit();
	}

	private static List<ItemGuia> getItemPacoteComSituacaoNula(SearchAgent sa, Class clazz) {
		sa.clearAllParameters();
		sa.addParameter(new IsNull("situacao"));
		return sa.list(clazz);
	}
	
}
