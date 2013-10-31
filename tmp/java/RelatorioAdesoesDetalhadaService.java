package br.com.infowaypi.ecare.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecare.enums.TipoArquivoEnum;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcos Roberto - 10.10.2011
 */
@SuppressWarnings("unchecked")
public class RelatorioAdesoesDetalhadaService {

	public ResumoSegurados gerarRelatorio(String dataInicial, String dataFinal, boolean detalhar) throws Exception{
		
		Assert.isNotEmpty(dataInicial, "Data inicial deve ser informada.");
		Assert.isNotEmpty(dataFinal, "Data final deve ser informada.");
		
		this.validaData(dataInicial, dataFinal);

		Date dataInicialInformada = Utils.parse(dataInicial);
		Date dataFinalInformada = Utils.parse(dataFinal);
		
		Calendar dataInicio = Calendar.getInstance();
		dataInicio.setTime(dataInicialInformada);
		dataInicio.set(Calendar.HOUR_OF_DAY, 0);
		dataInicio.set(Calendar.MINUTE, 0);
		dataInicio.set(Calendar.SECOND, 0);
		
		Calendar dataFim = Calendar.getInstance();
		dataFim.setTime(dataFinalInformada);
		dataFim.set(Calendar.HOUR_OF_DAY, 23);
		dataFim.set(Calendar.MINUTE, 59);
		dataFim.set(Calendar.SECOND, 59);
		
		SearchAgent sa = new SearchAgent();
		sa.clearAllParameters();
		sa.addParameter(new Between("dataAdesao",dataInicio.getTime(), dataFim.getTime()));
		sa.addParameter(new In("situacao.descricao",SituacaoEnum.ATIVO.descricao(),SituacaoEnum.SUSPENSO.descricao()));
		List<Segurado> listaSegurados = sa.list(Segurado.class);
		
		ResumoSegurados resumo = new ResumoSegurados();
		
		resumo.setSegurados(listaSegurados);
		
		if (detalhar) {
			resumo.setDetalharSegurados(detalhar);
			resumo.criarNomeArquivo();
			gerarArquivo(resumo);
		}
		
		return resumo;
	}

	protected void gerarArquivo(ResumoSegurados resumo) throws Exception {
		ArquivoBase arquivoXLS = new ArquivoBase();
		arquivoXLS.setArquivo(resumo.getFile());
		arquivoXLS.setDataCriacao(new Date());
		arquivoXLS.setZipado(false);
		arquivoXLS.setTipoArquivo(TipoArquivoEnum.XLS.getDescricao());
		arquivoXLS.setTituloArquivo("Relação de Adesões no Período");
		
		resumo.setArquivoXLS(arquivoXLS);
	}
	
	public void validaData(String dataInicial, String dataFinal) throws ValidateException{
		Date dtInicial = Utils.parse(dataInicial);
		Date dtFinal = Utils.parse(dataFinal);
		Date hoje = new Date();
		
		if(dtFinal.before(dtInicial))
			throw new ValidateException("Caro usuario(a), a data inicial informada é maior que a data final informada. Por favor, informe as datas corretamente. A data inicial deve ser menor que a data final.");
		if(dtInicial.after(hoje) || dtFinal.after(hoje))
			throw new ValidateException("Caro usuario(a), o período informado é superior a data atual. Por favor, informe as datas corretamente. A data inicial e data final devem ser menores ou iguais a data atual.");
	}
}