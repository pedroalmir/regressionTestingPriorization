package br.com.infowaypi.ecarebc.portalTiss.report;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.portalTiss.ArquivoTiss;
import br.com.infowaypi.ecarebc.portalTiss.ArquivoTissEnum;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.Equals;

/**
 * Report que disponibiliza os arquivos TISS para o prestador.
 * O filtro é realizado n
 * @author Emanuel
 *
 */
public class ReportBaixarArquivosTiss {

	/**
	 * Step 1: quando for outro usuario
	 * @param dataInicial
	 * @param dataFinal
	 * @param prestador
	 */
	public ResumoBaixarArquivoTiss buscarArquivos(Date dataInicial, Date dataFinal, Prestador prestador){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Between("dataCriacao", dataInicial, dataFinal));
		sa.addParameter(new Equals("prestador", prestador));
		sa.addParameter(new Equals("tipoTransacao", ArquivoTissEnum.ENVIO_LOTE_GUIAS.value()));
		
		List<ArquivoTiss> arquivos = sa.list(ArquivoTiss.class);
		
		return new ResumoBaixarArquivoTiss(dataInicial, dataFinal, arquivos, prestador);
	}

}
