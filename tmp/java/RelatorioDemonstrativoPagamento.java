package br.com.infowaypi.ecare.relatorio.portalBeneficiario.demostrativoIR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.consignacao.tuning.ConsignacaoTuning;
import br.com.infowaypi.ecare.manager.SeguradoManager;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.segurados.TitularFinanceiroSR;
import br.com.infowaypi.ecare.services.BuscarSegurados;
import br.com.infowaypi.ecare.services.Service;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.molecular.HibernateUtil;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Emanuel
 * @change Wislanildo
 */
public class RelatorioDemonstrativoPagamento extends Service {

	public ResumoDemonstrativoPagamento buscarFluxoFinanceiro(String numeroCartao, int ano) throws Exception {
		return buscarFluxoFinanceiro(null, numeroCartao, ano);
	}

	public ResumoDemonstrativoPagamento buscarFluxoFinanceiro(Segurado segurado, int ano) throws Exception {
		return buscarFluxoFinanceiro(segurado, segurado.getNumeroDoCartao(), ano);
	}

	public ResumoDemonstrativoPagamento buscarFluxoFinanceiro(Segurado segurado, String numeroCartao, Integer ano) throws Exception {

		Date dataInicial = Utils.createData("01/01/" + ano);
		Date dataFinal = Utils.createData("31/12/" + ano);
		
		List<Cobranca> boletos = buscarBoletos(dataInicial, dataFinal, segurado, numeroCartao);
		List<Consignacao> consignacoes = buscarConsiginacoes(dataInicial, dataFinal, segurado, numeroCartao);
		
		List<FluxoFinanceiroInterface> fluxos = new ArrayList<FluxoFinanceiroInterface>();
		fluxos.addAll(boletos);
		fluxos.addAll(consignacoes);
		
		Collections.sort(fluxos, new ComparatorFluxoFinanceiro());
		
		return new ResumoDemonstrativoPagamento(segurado, ano, fluxos);

	}
	
	class ComparatorFluxoFinanceiro implements Comparator<FluxoFinanceiroInterface>{

		@Override
		public int compare(FluxoFinanceiroInterface o1, FluxoFinanceiroInterface o2) {
			return o1.getCompetencia().compareTo(o2.getCompetencia());
		}
		
	}

	private List<Cobranca> buscarBoletos(Date dataInicial, Date dataFinal,Segurado segurado, String numeroCartao) {
		Criteria criteria = HibernateUtil.currentSession().createCriteria(Cobranca.class);
		
		if(segurado != null){
			criteria.add(Expression.eq("titular", segurado));
		}
		else if (numeroCartao != null) {
			List<Segurado> titulares = BuscarSegurados.buscar("", numeroCartao, TitularFinanceiroSR.class).getSegurados();
			if (!titulares.isEmpty()) {
				criteria.add(Expression.in("titular", titulares));
			}
		}

		criteria.add(Expression.eq("situacao.descricao", SituacaoEnum.PAGO.descricao()));
		
		criteria.add(Expression.between("dataPagamento", dataInicial, dataFinal));
		
		List<Cobranca> listaCobranca = criteria.list();
		
		return listaCobranca;
	}
	
	public List<Consignacao> buscarConsiginacoes(Date dataInicial, Date dataFinal, Segurado segurado, String numeroCartao){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Between("dataDoCredito", dataInicial, dataFinal));
		sa.addParameter(new Equals("statusConsignacao", Consignacao.STATUS_PAGO));
		
		if(segurado != null && (segurado.isSeguradoTitular() || segurado.isSeguradoDependenteSuplementar())){
			sa.addParameter(new Equals("titular.idSegurado", segurado.getIdSegurado()));
		}
		else{
			sa.addParameter(new Equals("titular", SeguradoManager.buscarPorCartao(numeroCartao, TitularFinanceiroSR.class, true)));
		}
		
		return sa.list(ConsignacaoTuning.class);
	}
}
