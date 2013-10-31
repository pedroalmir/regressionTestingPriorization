/**
 * 
 */
package br.com.infowaypi.ecare.services;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.service.autorizacoes.AuditarGuiaExamesEspeciaisService;

/**
 * @author Marcus bOolean
 *
 */
public class AuditarGuiasExamesEspeciais extends AuditarGuiaExamesEspeciaisService{
	
	public ResumoGuias<GuiaSimples> buscarGuias(String autorizacao, String dataInicial, String dataFinal,Prestador prestador) throws Exception {
		return super.buscarGuias(autorizacao, dataInicial, dataFinal, prestador);
	}
	
	public void salvarGuia(GuiaSimples guia) throws Exception {
		super.salvarGuia(guia);
	}
}
