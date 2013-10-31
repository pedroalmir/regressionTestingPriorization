package br.com.infowaypi.ecarebc.atendimentos.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import br.com.infowaypi.ecare.atendimentos.TipoGuiaEnum;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.validators.AtendimentoValidator;

/**
 * Classe responsável por concentrar todos as validações realizadas em um tipo de guia. Faz parte do novo modelo de validações do SR.
 * O método mais importante é o getValidacoes(TipoGuiaEnum), utilizado na chamada das validações através do polimorfismo.
 * @author Idelvane/ Dannylvan
 *
 */
@SuppressWarnings("unchecked")
public enum GuiaFactoryValidateEnum {
	
	GUIA_ATENDIMENTO_URGENCIA(TipoGuiaEnum.ATENDIMENTO_URGENCIA, 
								ValidateGuiaEnum.ATENDIMENTO_URGENCIA_PRESTADOR_VALIDATOR.getValidator(),
								ValidateGuiaEnum.GUIA_COMPLETA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.ATENDIMENTO_URGENCIA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CID_QUESTIONARIO_VALIDATOR.getValidator()),
								
								//TODO essa guia implementa honorário e não tem templateValidator = será que fica assim mesmo? Com os validators de guia completa?								
	GUIA_CIRURGIA_ODONTO(TipoGuiaEnum.CIRURGIA_ODONTOLOGICA,
								ValidateGuiaEnum.GUIA_COMPLETA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CARENCIA_INTERNACAO_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CID_QUESTIONARIO_VALIDATOR.getValidator()),
								
	GUIA_EXAME(TipoGuiaEnum.EXAME, 
			   					ValidateGuiaEnum.EXAME_VALIDATOR.getValidator(),
			   					ValidateGuiaEnum.EXAME_USUARIO_VALIDATOR.getValidator(),
			   					ValidateGuiaEnum.EXAME_PRESTADOR_VALIDATOR.getValidator(),
			   					ValidateGuiaEnum.CONSULTA_ORIGEM_VALIDATOR.getValidator(),
			   					ValidateGuiaEnum.CID_QUESTIONARIO_VALIDATOR.getValidator()),

	GUIA_EXAME_ODONTO(TipoGuiaEnum.EXAME_ODONTOLOGICO,
								ValidateGuiaEnum.EXAME_VALIDATOR.getValidator(),
								ValidateGuiaEnum.EXAME_USUARIO_VALIDATOR.getValidator(),
								ValidateGuiaEnum.EXAME_PRESTADOR_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_ORIGEM_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CID_QUESTIONARIO_VALIDATOR.getValidator()),
								
	GUIA_INTERNACAO_ELETIVA(TipoGuiaEnum.INTERNACAO_ELETIVA,
								ValidateGuiaEnum.GUIA_COMPLETA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CARENCIA_INTERNACAO_VALIDATOR.getValidator(),
								ValidateGuiaEnum.GUIA_ESPECIALIDADE_VALIDATOR.getValidator(),
								ValidateGuiaEnum.INTERNACAO_ELETIVA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.INTERNACAO_ELETIVA_PRESTADOR_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CID_QUESTIONARIO_VALIDATOR.getValidator(),
								ValidateGuiaEnum.ESPECIALIDADE_LIMITE_IDADE_VALIDATOR.getValidator()),
								
	GUIA_INTERNACAO_URGENCIA(TipoGuiaEnum.INTERNACAO_URGENCIA,
								ValidateGuiaEnum.GUIA_COMPLETA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CARENCIA_INTERNACAO_VALIDATOR.getValidator(),
								ValidateGuiaEnum.INTERNACAO_URGENCIA_PRESTADOR_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CID_QUESTIONARIO_VALIDATOR.getValidator(),
								ValidateGuiaEnum.ESPECIALIDADE_LIMITE_IDADE_VALIDATOR.getValidator()),
								
	GUIA_CONSULTA(TipoGuiaEnum.CONSULTA,
								ValidateGuiaEnum.CONSULTA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_PRESTADOR_VALIDATOR.getValidator(),
								ValidateGuiaEnum.GUIA_ESPECIALIDADE_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_PERIODICIDADE_VALIDATOR.getValidator(),
								ValidateGuiaEnum.ESPECIALIDADE_LIMITE_IDADE_VALIDATOR.getValidator()),
								
	GUIA_CONSULTA_URGENCIA(TipoGuiaEnum.CONSULTA_URGENCIA,
								ValidateGuiaEnum.GUIA_COMPLETA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_URGENCIA_PRESTADOR_VALIDATOR.getValidator(),
								ValidateGuiaEnum.GUIA_CONSULTA_URGENCIA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CID_QUESTIONARIO_VALIDATOR.getValidator(),
								ValidateGuiaEnum.ESPECIALIDADE_LIMITE_IDADE_VALIDATOR.getValidator()),
								
	GUIA_CONSULTA_ODONTO(TipoGuiaEnum.CONSULTA_ODONTOLOGIA,
								ValidateGuiaEnum.CONSULTA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_PRESTADOR_VALIDATOR.getValidator(),
								ValidateGuiaEnum.GUIA_ESPECIALIDADE_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_PERIODICIDADE_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_ODONTO_VALIDATOR.getValidator()),
								
	GUIA_CONSULTA_ODONTO_URGENCIA(TipoGuiaEnum.CONSULTA_ODONTOLOGICA_URGENCIA,
								ValidateGuiaEnum.CONSULTA_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_PRESTADOR_VALIDATOR.getValidator(),
								ValidateGuiaEnum.GUIA_ESPECIALIDADE_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_PERIODICIDADE_VALIDATOR.getValidator(),
								ValidateGuiaEnum.CONSULTA_ODONTO_VALIDATOR.getValidator()),
								
	GUIA_HONORARIO_MEDICO(TipoGuiaEnum.HONORARIO_MEDICO),
	
	GUIA_ACOMPANHAMENTO_ANESTESICO(TipoGuiaEnum.ACOMPANHAMENTO_ANESTESICO),
	
	GUIA_TRATAMENTO_SERIADO(TipoGuiaEnum.TRATAMENTO_SERIADO, ValidateGuiaEnum.LIMITE_TRATAMENTO_SERIADO_VALIDATOR.getValidator());
	
	
	private TipoGuiaEnum tipo;
	
	@SuppressWarnings("rawtypes")
	private List<AtendimentoValidator> validacoes = new ArrayList<AtendimentoValidator>();
	
	@SuppressWarnings("rawtypes")
	GuiaFactoryValidateEnum(TipoGuiaEnum tipo, AtendimentoValidator<GuiaSimples>... validacoes){
		this.tipo = tipo;
		this.addValidatorsGuiaSimples();
		this.validacoes.addAll(Arrays.asList(validacoes));
	}

	private void addValidatorsGuiaSimples() {
		EnumSet<ValidateGuiaEnum> validatorsGuiaSimples = EnumSet.of(
				ValidateGuiaEnum.GUIA_SIMPLES_VALIDATOR,
				ValidateGuiaEnum.SEGURADO_VALIDATOR,
				ValidateGuiaEnum.PRESTADOR_VALIDATOR,
				ValidateGuiaEnum.PROFISSIONAL_VALIDATOR,
				ValidateGuiaEnum.CONSUMO_SEGURADO_VALIDATOR,
				ValidateGuiaEnum.TETO_CONSUMO_E_EXAME_CONSULTA_VALIDATOR,
				ValidateGuiaEnum.DATA_GUIA_VALIDATOR
				);
		
		for (ValidateGuiaEnum validateEnum : validatorsGuiaSimples) {
			this.validacoes.add(validateEnum.getValidator());
		}
	}
	
	/**
	 * Método responsável por retornar as validações por tipo de guia. Utilizado no método validate() da classe {@link GuiaSimples}
	 * @param tipo
	 */
	@SuppressWarnings("rawtypes")
	public static List<AtendimentoValidator> getValidacoes(TipoGuiaEnum tipo){
		for (GuiaFactoryValidateEnum validate : GuiaFactoryValidateEnum.values()) {
			if(validate.getTipo().equals(tipo)){
				return validate.getValidacoes();
			}
		}
		return null;
	}
	
	public TipoGuiaEnum getTipo() {
		return tipo;
	}
	
	@SuppressWarnings("rawtypes")
	public List<AtendimentoValidator> getValidacoes() {
		return validacoes;
	}
	
}
