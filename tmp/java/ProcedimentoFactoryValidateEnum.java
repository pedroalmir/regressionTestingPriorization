package br.com.infowaypi.ecarebc.atendimentos.enums;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.validators.ValidateProcedimentoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.validators.AbstractProcedimentoValidator;

/**
 * Classe responsável por concentrar todos as validações realizadas em um tipo de procedimento. Faz parte do novo modelo de validações do SR.
 * O método mais importante é o getValidacoes(TipoProcedimentoEnum), utilizado na chamada das validações através do polimorfismo.
 * 
 * @author Dannylvan
 * @changes Leonardo Sampaio
 */
@SuppressWarnings({"rawtypes"})
public enum ProcedimentoFactoryValidateEnum {
	//verifiar através de MAIN se isso fuciona, verificar se as validações continuarão sendo feitas nos lugares corretos
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
	 * Adiciona as validações de PROCEDIMENTO
	 */
	private void addValidatorsProcedimento() {
		if (PROCEDIMENTO != null){
			for (AbstractProcedimentoValidator validate : PROCEDIMENTO.validacoes) {
				this.validacoes.add(validate);
			}
		}
	}
	/**
	 * Método responsável por retornar as validações por tipo de procedimento. Utilizado no método validate() da classe {@link Procedimento}
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
