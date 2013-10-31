package br.com.infowaypi.ecarebc.atendimentos.enums;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.atendimentos.validators.AtendimentoUrgenciaPrestadorValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.BasicEspecialidadeValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.EspecialidadeLimiteIdadeValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.BasicPrestadorValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.BasicProfissionalValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.BasicSeguradoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.CarenciaInternacaoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ConsultaOrigemValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ConsultaPeriodicidadeValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ConsultaPrestadorValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ConsultaUrgenciaPrestadorValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ConsumoSeguradoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.DataGuiaSimplesValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.EspecialidadeLimiteIdadeValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ExamePrestadorValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ExameUsuarioValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaAtendimentoUrgenciaValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaCompletaValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaConsultaOdontoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaConsultaUrgenciaPeriodicidadeValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaConsultaUrgenciaValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaConsultaValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaExameValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaInternacaoEletivaValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaPrazoProcedimentosValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.GuiaSimplesValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoEletivaPrestadorValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoPossuiPacoteSemProcedimentoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoPossuiProcedimentoSemPacoteValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.InternacaoUrgenciaPrestadorValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.LimiteTratamentoSeriadoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.ProcedimentoNivel2SituacaoValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.TetoConsumoEExamesValidator;
import br.com.infowaypi.ecarebc.atendimentos.validators.TetoExamesPrestadorValidator;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.validators.AtendimentoValidator;
import br.com.infowaypi.ecarebc.procedimentos.validators.QuestionarioCIDsValidator;

/**
 * Esta Enum concentra todos os validates de Guia ({@link GuiaSimples}) e Procedimento ({@link Procedimento}). Todo novo validate criado
 * deve ser inserido nesta enum para que ela possa servir como ponto central de validações.
 * </br>
 * Caso seja necessária uma validação em um ponto específico do SR (em um fluxo específico), não se deve chamar um "new Validator()" e sim:
 *  ValidateEnum.VALIDACAO_QUE_QUERO_CHAMAR.getValidator();
 * 
 * @author Idelvane/ Dannylvan
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public enum ValidateGuiaEnum {
	
	GUIA_SIMPLES_VALIDATOR (new GuiaSimplesValidator()),   
	
	ATENDIMENTO_URGENCIA_PRESTADOR_VALIDATOR (new AtendimentoUrgenciaPrestadorValidator()),
	ATENDIMENTO_URGENCIA_VALIDATOR (new GuiaAtendimentoUrgenciaValidator()),
	
	CONSULTA_ORIGEM_VALIDATOR (new ConsultaOrigemValidator()),
	CONSULTA_PERIODICIDADE_VALIDATOR (new ConsultaPeriodicidadeValidator()),
	CONSULTA_PRESTADOR_VALIDATOR (new ConsultaPrestadorValidator()),
	CONSULTA_URGENCIA_PRESTADOR_VALIDATOR (new ConsultaUrgenciaPrestadorValidator()),
	CONSULTA_ODONTO_VALIDATOR (new GuiaConsultaOdontoValidator()),
	CONSULTA_URGENCIA_PERIODICIDADE_VALIDATOR (new GuiaConsultaUrgenciaPeriodicidadeValidator()),
	CONSULTA_VALIDATOR (new GuiaConsultaValidator()),
	
	EXAME_PRESTADOR_VALIDATOR (new ExamePrestadorValidator()),
	EXAME_VALIDATOR (new GuiaExameValidator()),

	INTERNACAO_ELETIVA_VALIDATOR (new GuiaInternacaoEletivaValidator()),
	INTERNACAO_ELETIVA_PRESTADOR_VALIDATOR (new InternacaoEletivaPrestadorValidator()),
	INTERNACAO_URGENCIA_PRESTADOR_VALIDATOR (new InternacaoUrgenciaPrestadorValidator()),
	
	GUIA_ESPECIALIDADE_VALIDATOR (new BasicEspecialidadeValidator()),
	ESPECIALIDADE_LIMITE_IDADE_VALIDATOR (new EspecialidadeLimiteIdadeValidator()),
	
	PRESTADOR_VALIDATOR (new BasicPrestadorValidator()),
	PROFISSIONAL_VALIDATOR (new BasicProfissionalValidator()),
	SEGURADO_VALIDATOR (new BasicSeguradoValidator()),
	
	CARENCIA_INTERNACAO_VALIDATOR (new CarenciaInternacaoValidator()),
	
	CONSUMO_SEGURADO_VALIDATOR (new ConsumoSeguradoValidator()),
	DATA_GUIA_VALIDATOR (new DataGuiaSimplesValidator()),
	GUIA_PRAZO_PROCEDIMENTOS_VALIDATOR (new GuiaPrazoProcedimentosValidator()),

	LIMITE_TRATAMENTO_SERIADO_VALIDATOR (new LimiteTratamentoSeriadoValidator()),
	
	TETO_CONSUMO_E_EXAME_CONSULTA_VALIDATOR(new TetoConsumoEExamesValidator()),
	
	EXAME_USUARIO_VALIDATOR (new ExameUsuarioValidator()),
	GUIA_COMPLETA_VALIDATOR (new GuiaCompletaValidator()),
	GUIA_CONSULTA_URGENCIA_VALIDATOR (new GuiaConsultaUrgenciaValidator()),
	CID_QUESTIONARIO_VALIDATOR (new QuestionarioCIDsValidator()),

	TETO_EXAME_PRESTADOR (new TetoExamesPrestadorValidator()),
	
	INTERNACAO_POSSUI_PACOTE_SEM_PROCEDIMENTO (new InternacaoPossuiPacoteSemProcedimentoValidator()),
	INTERNACAO_POSSUI_PROCEDIMENTO_SEM_PACOTE (new InternacaoPossuiProcedimentoSemPacoteValidator()),
	PROCEDIMENTO_NIVEL_2_SITUACAO_VALIDATOR (new ProcedimentoNivel2SituacaoValidator());
	
	
	private AtendimentoValidator validator;
	
	ValidateGuiaEnum(AtendimentoValidator validator){
		this.validator = validator;
	}

	public AtendimentoValidator<GuiaSimples> getValidator() {
		return validator;
	}
	
}
