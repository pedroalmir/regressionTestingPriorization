/**
 * 
 */
package br.com.infowaypi.ecarebc.service;

import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.associados.ResumoPrestadores;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.address.Municipio;

/**
 * @author Marcus bOolean
 * @changes Emanuel
 */
public class RelatorioPrestadoresService {
	
	public ResumoPrestadores buscarPrestadores(String prestador, Especialidade especialidade, Profissional profissional, TabelaCBHPM procedimento, 
			String bairro, Municipio municipio, Integer tipoResultado, boolean eletivo, boolean urgencia, boolean odontologico) throws Exception {

		ResumoPrestadores resumo = new ResumoPrestadores(especialidade, prestador, profissional, procedimento, bairro, municipio, tipoResultado, eletivo, urgencia, odontologico);
		
		return resumo;
	}
}
