package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.procedimentos.validators.AbstractProcedimentoValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoAtendimentoUrgenciaValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoCarenciaValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoEspecialUsuarioValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoEstruturasOdontoValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoIdadeValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoOdontoEspecialidadeValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoOdontoPeriodicidadeValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoOdontoUnicidadeValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoOdontoValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoPeriodicidadeValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoSexoValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoSimplesValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoSubgrupoQuestionarioValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoUnicidadeValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentoVisibilidadeValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.ProcedimentosAssociadosValidator;

@SuppressWarnings({"rawtypes"})
public enum ValidateProcedimentoEnum {
	
	PROCEDIMENTO_SIMPLES_VALIDATOR (new ProcedimentoSimplesValidator()),
	PROCEDIMENTO_ESPECIAL_USUARIO_VALIDATOR (new ProcedimentoEspecialUsuarioValidator()),
	PROCEDIMENTO_CARENCIA_VALIDATOR (new ProcedimentoCarenciaValidator()),
	PROCEDIMENTO_UNICIDADE_VALIDATOR (new ProcedimentoUnicidadeValidator()),
	PROCEDIMENTO_PERIODICIDADE_VALIDATOR (new ProcedimentoPeriodicidadeValidator()),
	PROCEDIMENTO_SEXO_VALIDATOR (new ProcedimentoSexoValidator()),
	PROCEDIMENTO_IDADE_VALIDATOR (new ProcedimentoIdadeValidator()),
	PROCEDIMENTO_ASSOCIADOS_VALIDATOR (new ProcedimentosAssociadosValidator()),
	
	PROCEDIMENTO_ODONTO_VALIDATOR (new ProcedimentoOdontoValidator()),
	PROCEDIMENTO_ODONTO_UNICIDADE_VALIDATOR (new ProcedimentoOdontoUnicidadeValidator()),
	PROCEDIMENTO_ODONTO_PERIODICIDADE_VALIDADOR (new ProcedimentoOdontoPeriodicidadeValidator()),
	PROCEDIMENTO_ESTRUTURAS_ODONTO_VALIDATOR (new ProcedimentoEstruturasOdontoValidator()),
	PROCEDIMENTO_ODONTO_ESPECIALIDADE_VALIDADOR (new ProcedimentoOdontoEspecialidadeValidator()),
	
	PROCEDIMENTO_VISIBILIDADE_VALIDATOR (new ProcedimentoVisibilidadeValidator()),
	PROCEDIMENTO_SUBGRUPO_QUESTIONARIO_VALIDATOR (new ProcedimentoSubgrupoQuestionarioValidator()),
	
	PROCEDIMENTO_ATENDIMENTO_URGENCIA (new ProcedimentoAtendimentoUrgenciaValidator());
	
	private AbstractProcedimentoValidator validator;
	
	ValidateProcedimentoEnum(AbstractProcedimentoValidator validator){
		this.validator = validator;
	}

	public AbstractProcedimentoValidator getValidator() {
		return validator;
	}
}
