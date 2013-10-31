package br.com.infowaypi.ecare.services;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.service.autorizacoes.AutorizarProcedimentosOdontoEspeciaisService;

/**
 * Service para autoriza��o de procedimentos odntol�gicos especiais pela central de servi�os
 * @author Danilo Nogueira Portela
 *
 */
public class AutorizarProcedimentosOdontoEspeciais extends
		AutorizarProcedimentosOdontoEspeciaisService {
	
	public  ResumoGuias buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		return super.buscarGuias(autorizacao, dataInicial, dataFinal, prestador);
	}

}
