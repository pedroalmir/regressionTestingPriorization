package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;

@SuppressWarnings("rawtypes")
public class TetoConsumoEExamesValidator extends  AbstractGuiaValidator<GuiaSimples> {
	
	TetoExamesPrestadorValidator 	exameValidator 		= new TetoExamesPrestadorValidator();
	TetoConsultasPrestadorValidator consultasValidator 	= new TetoConsultasPrestadorValidator();
	
	@Override
	public boolean templateValidator(GuiaSimples guia) throws Exception {
		
		if (guia.isExame()){
			exameValidator.execute(guia);
		}
		
		if (guia.isConsulta()){
			consultasValidator.execute(guia);
		}
		
		return false;
	}
}
