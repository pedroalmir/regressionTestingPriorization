package br.com.infowaypi.ecare.correcaomanual;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import br.com.infowaypi.ecare.financeiro.consignacao.ConsignacaoSegurado;
import br.com.infowaypi.ecare.financeiro.consignacao.tuning.ConsignacaoSeguradoTuning;
import br.com.infowaypi.ecare.segurados.tuning.TitularConsignacaoTuning;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

public class AjustaSituacaoConsignacaoPensionista {
	static int count = 0;
	static Map<String, Set<String>> consignacoesPensionistas = new HashMap<String, Set<String>>();
	static Set<String> titulares = new HashSet<String>();

	public static void main(String[] args) throws Exception {
//		Transaction tx = HibernateUtil.currentSession().beginTransaction();
		ajustarConsignacoes(new HashSet<TitularConsignacaoTuning>(buscarTitulares().list()));
//		tx.commit();
		
		System.out.println("Qtd consignações corrigidas: " + count);
		System.out.println("Pensionistas");
		for(String nomeECartao : consignacoesPensionistas.keySet()){
			System.out.println(nomeECartao);
			Set<String> consignacaoesAjustadas = consignacoesPensionistas.get(nomeECartao);
			for(String consignacao : consignacaoesAjustadas){
				System.out.println("      "  + consignacao);
			}
		}
		
		System.out.println();
		
		System.out.println("Titular");
		for(String titular : titulares){
			System.out.println(titular);
		}
	}

	private static Criteria buscarTitulares() {
		SearchAgent sa = new SearchAgent();
		Criteria criteria = sa.createCriteriaFor(TitularConsignacaoTuning.class);
		criteria.createAlias("consignacoes", "consig");
		criteria.add(Restrictions.eq("consig.statusConsignacao", "A"));
		criteria.add(Restrictions.eq("tipoSegurado", "P"));
		criteria.add(Restrictions.eq("situacao.descricao", SituacaoEnum.ATIVO.descricao()));
		return criteria;
	}

	private static void ajustarConsignacoes(Set<TitularConsignacaoTuning> pensionistas) throws Exception {
		for(TitularConsignacaoTuning pensionista : pensionistas){
			Set<ConsignacaoSeguradoTuning> consignacoes =  pensionista.getConsignacoes();
			for(Consignacao consignacao : consignacoes){
				String cpf = pensionista.getPessoaFisica().getCpf();
				Date competencia = consignacao.getCompetencia();
				if(consignacao.getStatusConsignacao() == Consignacao.STATUS_ABERTO 
						&& isPossuiConsignacaoMesmaCompetenciaParaTitularInativo(cpf, competencia)){
					
					consignacao.setStatusConsignacao(Consignacao.STATUS_PAGO);
					ImplDAO.save(consignacao);
					
					String numeroDoCartao = pensionista.getNumeroDoCartao();
					String nome = pensionista.getPessoaFisica().getNome();
					
					String nomeEnumeroDoCartao = numeroDoCartao + " - " + nome;
					alimentarMapSeguradoConsiguinacoes(consignacao, nomeEnumeroDoCartao);
					
					count++;
				}
			}
		}
	}

	private static void alimentarMapSeguradoConsiguinacoes(Consignacao consignacao, String numeroDoCartao) {
		if(!consignacoesPensionistas.containsKey(numeroDoCartao)){
			Set<String> competencias = new HashSet<String>();
			competencias.add(consignacao.getCompetenciaFormatada());
			consignacoesPensionistas.put(numeroDoCartao, competencias);
		}else {
			consignacoesPensionistas.get(numeroDoCartao).add(consignacao.getCompetenciaFormatada());
		}
	}
	
	private static boolean isPossuiConsignacaoMesmaCompetenciaParaTitularInativo(String cpf, Date competencia){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("statusConsignacao", "P"));
		sa.addParameter(new Equals("competencia", competencia));
		sa.addParameter(new Equals("titular.tipoSegurado", "T"));
		sa.addParameter(new Equals("titular.situacao.descricao", SituacaoEnum.CANCELADO.descricao()));
		sa.addParameter(new Equals("titular.pessoaFisica.cpf", cpf));
		
		ConsignacaoSegurado consignacao = sa.uniqueResult(ConsignacaoSegurado.class); 
		
		if(consignacao != null){
			titulares.add(consignacao.getTitular().getNumeroDoCartao() + " - " + consignacao.getTitular().getPessoaFisica().getNome());
			return true;
		}
		
		return false;
	}
}