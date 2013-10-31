package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de Prestador em guias de exame
 * @author Danilo Nogueira Portela
 */
public class ExamePrestadorValidator<E extends GuiaExame> extends AbstractGuiaValidator<E> {
 
	public boolean templateValidator(E guia) throws ValidateException {
		Prestador prestador = guia.getPrestador();
		String tipoExame = guia.isExameOdonto() ? "TRATAMENTOS ODONTOLÓGICOS" : "EXAMES";
		
		if(guia.isExameOdonto() && prestador != null && !prestador.isFazOdontologico() )
			throw new ValidateException(MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE
					.getMessage(tipoExame));
		
		if(prestador != null) {
			if(!guia.isExameOdonto() && !prestador.isFazExame())
				throw new ValidateException(MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE
						.getMessage(tipoExame));
		}	
		return true;
	}

}
