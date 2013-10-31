package br.com.infowaypi.ecare.relatorio.portalBeneficiario.extratoCoparticipacao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.financeiro.Cobranca;
import br.com.infowaypi.ecare.financeiro.consignacao.tuning.ConsignacaoTuning;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.Service;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.FluxoFinanceiroInterface;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.utils.CompetenciaUtils;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * @author Emanuel
 */
public class RelatorioDemonstrativoCoparticipacao extends Service {


	private List<Cobranca> buscarBoletos(Date competencia, Segurado segurado) {
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("titular", segurado));
		sa.addParameter(new Equals("situacao.descricao", SituacaoEnum.PAGO.descricao()));
		sa.addParameter(new Equals("competencia", competencia));

		List<Cobranca> listaCobranca = sa.list(Cobranca.class);
		return listaCobranca;
	}
	
	public List<Consignacao> buscarConsiginacoes(Date competencia, Segurado segurado) throws Exception{
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("competencia", competencia));
		sa.addParameter(new Equals("statusConsignacao", Consignacao.STATUS_PAGO));
		
		if(segurado != null && (segurado.isSeguradoTitular() || segurado.isSeguradoDependenteSuplementar() || segurado.isSeguradoPensionista())){
			sa.addParameter(new Equals("titular.idSegurado", segurado.getIdSegurado()));
		}
		else{
			throw new Exception("Não possivel gerar essa informação para um beneficiario Dependente.");
		}
		
		return sa.list(ConsignacaoTuning.class);
	}
	
	/**
	 * Método usado no Report DemonstrativoCoparticipacoes.jhm.xml
	 * @param segurado
	 * @param competencia
	 * @return
	 * @throws Exception
	 */
	public ResumoDemonstrativoCoparticipacao buscarCoparticipacao(Segurado segurado, Date competencia) throws Exception {

		competencia = CompetenciaUtils.getCompetencia(competencia);
		
		List<Cobranca> boletos = buscarBoletos(competencia, segurado);
		List<Consignacao> consignacoes = buscarConsiginacoes(competencia, segurado);
		
		List<FluxoFinanceiroInterface> fluxos = new ArrayList<FluxoFinanceiroInterface>();
		fluxos.addAll(boletos);
		fluxos.addAll(consignacoes);
		
		if(fluxos.isEmpty()){
			throw new ValidateException("O segurado não possui nenhuma cobrança na competência informada.");
		}
		
		return new ResumoDemonstrativoCoparticipacao(fluxos, competencia);
	}

}
