package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecare.services.honorariomedico.AlteracaoProcedimentosGuiaPagaValidator;
import br.com.infowaypi.ecare.services.honorariomedico.HonorarioAptorAuditarValidator;
import br.com.infowaypi.ecare.services.honorariomedico.HonorarioSelecionadoAuditoriaValidator;
import br.com.infowaypi.ecarebc.associados.validators.AbstractValidator;
 /**
  * EnumFlowValidator provê validadotes para classes de fluxos do sistema. 
  * Os validadores devem implementar AbstractValidator
  * @author jefferson
  */

public enum EnumFlowValidator {
	
	DATA_TERMINO_ATENDIMENTO_GUIA(new DataTerminoAtendimentoValidator()),
	DATA_RECEBIMENTO_GUIA(new DataRecebimentoValidator()),
	EXISTE_HONORARIO_APTO_AUDITAR(new HonorarioAptorAuditarValidator()),
	ALTERACAO_HONORARIOS_SELECIONADOS_AUDITORIA(new HonorarioSelecionadoAuditoriaValidator()),
	ALTERACAO_PROCEDIMENTOS_EM_GUIAS_FATURADAS_OU_PAGAS(new AlteracaoProcedimentosGuiaPagaValidator());
	
	
	@SuppressWarnings("rawtypes")
	public AbstractValidator getValidator() {
		return validator;
	}

	private EnumFlowValidator(AbstractValidator validator) {
		this.validator = validator;
	}

	private AbstractValidator validator;
	
	

}
