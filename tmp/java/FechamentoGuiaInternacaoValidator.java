package br.com.infowaypi.ecarebc.atendimentos.validators.fechamento;

import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaInternacao;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public interface FechamentoGuiaInternacaoValidator {

	public void execute(GuiaInternacao guia, Boolean parcial, Date dataFinal, UsuarioInterface usuario) throws ValidateException;
	
}
