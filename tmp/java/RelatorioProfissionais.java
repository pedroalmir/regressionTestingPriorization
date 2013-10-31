/**
 * 
 */
package br.com.infowaypi.ecare.services;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.ResumoProfissionais;
import br.com.infowaypi.ecarebc.service.RelatorioProfissionaisService;
import br.com.infowaypi.msr.address.Municipio;

/**
 * @author Marcus Boolean
 *
 */
public class RelatorioProfissionais extends RelatorioProfissionaisService {
	
	public ResumoProfissionais buscarProfissionais(Prestador prestador, Especialidade especialidade, String profissionalCitado, String bairro, Municipio municipio) throws Exception {
		return super.buscarProfissionais(prestador, especialidade, profissionalCitado, municipio, bairro);
	}
}
