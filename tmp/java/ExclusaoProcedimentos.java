package br.com.infowaypi.ecare.services;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.segurados.SeguradoInterface;
import br.com.infowaypi.ecarebc.service.ExclusaoProcedimento;
/**
 * 
 * @author Idelvane
 *
 */
public class ExclusaoProcedimentos extends ExclusaoProcedimento<SeguradoInterface>{
	@Override
	public GuiaCompleta buscarGuia(String autorizacao, Prestador prestador) throws Exception{
		return super.buscarGuia(autorizacao, prestador);
	}
}
