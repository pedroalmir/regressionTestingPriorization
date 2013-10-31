package br.com.infowaypi.ecarebc.service.autorizacoes;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoUrgencia;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.molecular.ImplDAO;

/**
 * Service para autoriza��o de interna��es de urgencia do plano de sa�de
 * @author root
 */
public class AutorizarInternacaoUrgenciaService extends AutorizarInternacoes {
	
	public AutorizarInternacaoUrgenciaService(){
		super();
	}
	
	public ResumoGuias buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		return buscarGuias(autorizacao, dataInicial,dataFinal,prestador, GuiaInternacaoUrgencia.class);
	}

}
