package br.com.infowaypi.ecare.services;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecare.resumos.ResumoGuia;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.NotIn;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioSeguradosInternadosService extends Service {
	
	@SuppressWarnings("unchecked")
	public <G extends GuiaSimples> ResumoGuia buscarGuias(String dataInicial, String dataFinal, Prestador prestador) throws Exception{
		if(prestador == null && Utils.isStringVazia(dataInicial) && Utils.isStringVazia(dataFinal))
			throw new Exception(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		SearchAgent sa = new SearchAgent();
		
		List<String> listaDeSituacoes = new ArrayList<String>();
		listaDeSituacoes.add(SituacaoEnum.FATURADA.descricao());
		listaDeSituacoes.add(SituacaoEnum.AUDITADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.FECHADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.CANCELADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.NAO_AUTORIZADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.AUTORIZADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.SOLICITADO_INTERNACAO.descricao());
		listaDeSituacoes.add(SituacaoEnum.ENVIADO.descricao());
		listaDeSituacoes.add(SituacaoEnum.RECEBIDO.descricao());
		listaDeSituacoes.add(SituacaoEnum.DEVOLVIDO.descricao());
		listaDeSituacoes.add(SituacaoEnum.PAGO.descricao());
		listaDeSituacoes.add(SituacaoEnum.INAPTO.descricao());
		listaDeSituacoes.add(SituacaoEnum.ALTA_REGISTRADA.descricao());
		
		
		sa.addParameter(new NotIn("situacao.descricao",listaDeSituacoes));
		
		List<GuiaSimples> guias = super.buscarGuias(sa,"",null,dataInicial,dataFinal,prestador,false,false,GuiaSimples.class, GuiaSimples.DATA_DE_MARCACAO);
		ResumoGuia resumo = new ResumoGuia(guias,ResumoGuia.SITUACAO_TODAS,false); 		
		return resumo;
	}
	
	public ResumoGuia finalizar(ResumoGuia resumo) {
		return resumo;
	}

}
