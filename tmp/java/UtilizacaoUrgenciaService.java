/**
 * 
 */
package br.com.infowaypi.ecare.relatorio;


import java.util.Date;

import br.com.infowaypi.ecare.resumos.ResumoUtilizacaoUrgencia;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Marcus Vinicius
 *
 */
public class UtilizacaoUrgenciaService {
	
	public ResumoUtilizacaoUrgencia gerarRelatorio(Date dataInicial, Date dataFinal, String quantidadeGuias, Prestador prestador) {
		
		Assert.isNotNull(dataInicial, "O campo Data Inicial deve ser informado.");
		Assert.isNotNull(dataFinal, "O campo Data Final deve ser informado.");
		
		Integer qtGuias = new Integer(0);
		try {
			qtGuias = Integer.parseInt(quantidadeGuias);
		} catch (Exception e) {
			// Mantém o valor padrão Zero.
		}
		
		return new ResumoUtilizacaoUrgencia(dataInicial, dataFinal, prestador, qtGuias);
	}

}
