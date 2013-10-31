package br.com.infowaypi.ecarebc.atendimentos.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.validators.ValidateProcedimentoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.validators.AbstractProcedimentoValidator;

/**
 * Classe respons�vel por concentrar todos as valida��es realizadas em um tipo de procedimento. Faz parte do novo modelo de valida��es do SR.
 * O m�todo mais importante � o getValidacoes(TipoProcedimentoEnum), utilizado na chamada das valida��es atrav�s do polimorfismo.
 * 
 * @author Dannylvan
 * @changes Leonardo Sampaio
 */
@SuppressWarnings({"rawtypes"})
public enum ProcedimentoFactoryValidateEnum {
	//verifiar atrav�s de MAIN se isso fuciona, verificar se as valida��es continuar�o sendo feitas nos lugares corretos
	PROCEDIMENTO(TipoProcedimentoEnum.PROCEDIMENTO, 
			ValidateProcedimentoEnum.PROCEDIMENTO_SIMPLES_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_ESPECIAL_USUARIO_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_CARENCIA_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_UNICIDADE_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_PERIODICIDADE_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_SEXO_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_IDADE_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_ASSOCIADOS_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_SUBGRUPO_QUESTIONARIO_VALIDATOR.getValidator()),
			
	PROCEDIMENTO_ODONTO(TipoProcedimentoEnum.PROCEDIMENTO_ODONTO,
			/* if[PROCEDIMENTO_ODONTO_ESPECIALIDADE_VALIDADOR]
			ValidateProcedimentoEnum.PROCEDIMENTO_ODONTO_ESPECIALIDADE_VALIDADOR.getValidator(),
			end[PROCEDIMENTO_ODONTO_ESPECIALIDADE_VALIDADOR] */			
			ValidateProcedimentoEnum.PROCEDIMENTO_ODONTO_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_ODONTO_UNICIDADE_VALIDATOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_ODONTO_PERIODICIDADE_VALIDADOR.getValidator(),
			ValidateProcedimentoEnum.PROCEDIMENTO_ESTRUTURAS_ODONTO_VALIDATOR.getValidator());
			
	
	private TipoProcedimentoEnum tipo;
	private Set<AbstractProcedimentoValidator> validacoes = new HashSet<AbstractProcedimentoValidator>();
	
	ProcedimentoFactoryValidateEnum(TipoProcedimentoEnum tipo, AbstractProcedimentoValidator... validacoes){
		this.tipo = tipo;
		this.addValidatorsProcedimento();
		this.validacoes.addAll(Arrays.asList(validacoes));
	}
	
	/**
	 * Adiciona as valida��es de PROCEDIMENTO
	 */
	private void addValidatorsProcedimento() {
		if (PROCEDIMENTO != null){
			for (AbstractProcedimentoValidator validate : PROCEDIMENTO.validacoes) {
				this.validacoes.add(validate);
			}
		}
	}
	/**
	 * M�todo respons�vel por retornar as valida��es por tipo de procedimento. Utilizado no m�todo validate() da classe {@link Procedimento}
	 * @param tipo
	 */
	public static Set<AbstractProcedimentoValidator> getValidacoes(TipoProcedimentoEnum tipo){
		for (ProcedimentoFactoryValidateEnum validate : ProcedimentoFactoryValidateEnum.values()) {
			if(validate.getTipo().equals(tipo)){
				return validate.getValidacoes();
			}
		}
		return null;
	}
	
	public TipoProcedimentoEnum getTipo() {
		return tipo;
	}
	
	public Set<AbstractProcedimentoValidator> getValidacoes() {
		return validacoes;
	}
}
