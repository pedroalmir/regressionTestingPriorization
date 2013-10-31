package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public interface FechamentoGuiaCompletaValidator {

	/**
	 * Executa o validator.
	 * 
	 * @param guia
	 * @param parcial
	 * @param dataFinal
	 * @param usuario
	 * @throws ValidateException
	 */
	@SuppressWarnings("unchecked")
	public void execute(GuiaCompleta guia, Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException;
}
