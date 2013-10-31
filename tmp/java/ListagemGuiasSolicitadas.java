package br.com.infowaypi.ecarebc.atendimentos;

import java.util.Calendar;
import java.util.Date;

import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.OR;
import br.com.infowaypi.molecular.parameter.OrderBy;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class ListagemGuiasSolicitadas {

	public ResumoGuiasSolicitadas buscarGuias(String dataInicial, String dataFinal, UsuarioInterface usuario) {
		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new OR(new Equals("situacao.descricao", SituacaoEnum.SOLICITADO.descricao()), 
				new Equals("situacao.descricao", SituacaoEnum.SOLICITADO_INTERNACAO.descricao())));

		if (dataInicial != null && dataFinal == null) {
			sa.addParameter(new Between("situacao.dataSituacao", Utils.createData(dataInicial), new Date()));
		} else if (dataInicial != null && dataFinal != null) {
			sa.addParameter(new Between("situacao.dataSituacao", Utils.createData(dataInicial), Utils.createData(dataFinal)));
		} else if (dataInicial == null && dataFinal == null) {
			sa.addParameter(new Between("situacao.dataSituacao", Utils.incrementaDias(Calendar.getInstance(), -60), new Date()));
		}
		
		sa.addParameter(new OrderBy("situacao.dataSituacao"));
		
		return new ResumoGuiasSolicitadas(sa.list(GuiaCompleta.class));
		
	}

}
