package br.com.infowaypi.ecare.services;

import java.util.List;

import br.com.infowaypi.ecare.resumos.ResumoGuia;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;


public class RelatorioGuiasNecessitadasDeAuditoriaService extends Service{

	@SuppressWarnings("unchecked")
	public <G extends GuiaSimples> ResumoGuia buscarGuias(String dataInicial, String dataFinal, Prestador prestador) throws Exception{
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao",SituacaoEnum.FECHADO.descricao()));
		List<GuiaSimples> guias = super.buscarGuias(sa,"",null,dataInicial,dataFinal,prestador,false,false,GuiaSimples.class, null);
		ResumoGuia resumo = new ResumoGuia(guias,ResumoGuia.SITUACAO_TODAS,false); 		
		return resumo;
	}
	
	public ResumoGuia finalizar(ResumoGuia resumo) {
		return resumo;
	}
}
