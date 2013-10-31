/**
 * 
 */
package br.com.infowaypi.ecarebc.service;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.ResumoProfissionais;
import br.com.infowaypi.msr.address.Municipio;

/**
 * @author Marcus Boolean
 *
 */
public class RelatorioProfissionaisService {
	
	public ResumoProfissionais buscarProfissionais(Prestador prestador, Especialidade especialidade, String profissionalCitado, Municipio municipio, String bairro) throws Exception {
		ResumoProfissionais resumo = new ResumoProfissionais(especialidade,prestador, profissionalCitado, bairro, municipio);
		return resumo;
	}
}
