package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;


/**
 * Classe respons�vel por verificar se uma guia ser� fechada parcialmente.
 * 
 * @author Eduardo
 *
 */
public class FechamentoParcialValidator implements FechamentoGuiaCompletaValidator {

	@SuppressWarnings("unchecked")
	public void execute(GuiaCompleta guia, Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException {
		parcial = parcial == null? false : parcial;
		if (!guia.isInternacao() && parcial){
			throw new ValidateException("N�o � permitido fechar parcialmente uma guia de "+guia.getTipo());
		}
	}

}
