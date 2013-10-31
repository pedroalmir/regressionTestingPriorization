package br.com.infowaypi.ecare.services.honorarios.validators;

import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

public class GuiaHonorarioInseriuPeloMenosUmProcedimentoValidate extends
		CommandValidator {

	@Override
	public void execute() throws ValidateException {
		Assert.isFalse(getProcedimentosOutros().isEmpty(), "Adicione pelo menos um procedimento na guia.");
	}

}
