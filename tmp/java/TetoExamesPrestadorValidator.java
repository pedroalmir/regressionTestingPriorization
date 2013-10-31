package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

@SuppressWarnings("rawtypes")
public class TetoExamesPrestadorValidator extends AbstractGuiaValidator<GuiaSimples>{
	@Override
	protected boolean templateValidator(GuiaSimples guia)throws ValidateException{
		Prestador prestador = guia.getPrestador();
		
		
		if (prestador != null && guia.isExame()){
			boolean verificaTeto 			= prestador.isVerificarTetos();
			boolean estourouLimite 			= prestador.isTetoExamesEstourado(guia.getValorTotal());
			boolean solicitadoComPrestador 	= guia.isSolicitadoComPrestador(); 
			
			if (verificaTeto && estourouLimite && !solicitadoComPrestador){
				throw new ValidateException(MensagemErroEnum.TETO_PRESTADOR_MENSAL_EXAMES_ESTOUROU.getMessage());
			}
		}		
		
		return true;
	}
}
