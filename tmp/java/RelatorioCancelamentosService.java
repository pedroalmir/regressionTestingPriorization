/**
 * 
 */
package br.com.infowaypi.ecare.services;

import java.text.ParseException;
import java.util.Date;

import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
public class RelatorioCancelamentosService extends RelatorioAdesoesService {
	
public ResumoSegurados gerarRelatorio(String dataInicial, String dataFinal) throws ValidateException, ParseException{
		
		Assert.isNotEmpty(dataInicial, "Data inicial deve ser informada.");
		Assert.isNotEmpty(dataFinal, "Data final deve ser informada.");
		
		this.validaData(dataInicial, dataFinal);

		Date dataInicialInformada = Utils.parse(dataInicial);
		Date dataFinalInformada = Utils.parse(dataFinal);
		
		ResumoSegurados resumo = new ResumoSegurados();
		resumo.setDataInicial(dataInicialInformada);
		resumo.setDataFinal(dataFinalInformada);
		resumo.computarResultado();
		
		return resumo;
	}

}
