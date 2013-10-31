package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Guia que faz a validação se uma guia completa está apta a ser fechada.
 * 
 * @author Eduardo
 *
 */
public class FechamentoDefaultStrategy {

	private static List<FechamentoGuiaCompletaValidator> validators = new ArrayList<FechamentoGuiaCompletaValidator>();
	
	static {
//		validators.add(new FechamentoProcedimentosSolicitadosValidator());
		validators.add(new FechamentoDataLimiteValidator());
		validators.add(new FechamentoParcialValidator());
		validators.add(new FechamentoOutrosValoresValidator());
	}

	public static void validaFechamento(GuiaCompleta guia, Boolean parcial,
			Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		
		for (FechamentoGuiaCompletaValidator validator : validators) {
			validator.execute(guia, parcial, dataFinal, usuario);
		}
	}
	
}
