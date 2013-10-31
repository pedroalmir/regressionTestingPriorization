package br.com.infowaypi.ecarebc.service.autorizacoes;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacaoEletiva;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;


/**
 * Service para autoriza��o de interna��es eletivas do plano de sa�de
 * @author root
 */
public class AutorizarInternacaoEletivaService extends AutorizarInternacoes {
	
	public AutorizarInternacaoEletivaService(){
		super();
	}
	
	public ResumoGuias buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		return buscarGuias(autorizacao, dataInicial,dataFinal,prestador, GuiaInternacaoEletiva.class);
	}
	
}
