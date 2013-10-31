package br.com.infowaypi.ecarebc.enums;

/**
 * @author Idelvane
 *
 */
public enum MotivoEnum {
	
	AGENDAMENTO_CONSULTA_ELETIVA("Agendamento de consulta eletiva."),
	AGENDAMENTO_TRATAMENTO_SERIADO("Agendamento de tratamento seriado."),
	AGENDAMENTO_EXAME("Agendamento de Exame."),
	AGENDADA_NO_ecare("Agendada no ecare."),
	AGENDADA_NO_PRESTADOR("Agendada no Prestador."),
	AGENDADA_NO_LANCAMENTO_MANUAL("Agendada durante o lançamento manual."),
	AGENDADA_PELO_TI("Agendada pelo Ti."),
	AGENDADA_PELO_DIGITADOR("Agendada Pelo Digitador."),
	AGENDADA_PELA_CENTRAL_DE_SERVICOS("Agendada pela Central de Serviços."),
	AGENDADA_PELO_AUDITOR("Agendada pelo auditor."),
	AGENDADA_PELO_DIRETOR("Agendada pelo diretor."),
	AGENDAMENTO_AUDITOR("Agendada pelo Auditor"),
	AGENDAMENTO_INTERNACAO_ELETIVA("Agendamento de internação eletiva."),
	AGENDAMENTO_INTERNACAO_URGENCIA("Agendamento de internação de urgência."),
	ATENDIMENTO_URGENCIA("Atendimento Subsequente."),
	INCLUSAO_DE_EXAMES_SIMPLES("Inclusão de exames simples."),
	AUTORIZADO_PELO_AUDITOR("Autorizado pelo auditor."),
	AUTORIZADO_PELO_ROLE_INFORMADO("Autorizado pelo {0}."),
	NAO_AUTORIZADO_PELO_ROLE_INFORMADO("Não autorizado pelo {0}."),
	AUTORIZADO_COM_PERICIA_FINAL("Autorizado com perícia final."),
	AUTORIZADO_PELO_TI("Autorizado pelo Ti."),
	AUTORIZADO_PELA_DIRETORIA("Autorizado pelo diretor."),
	AUTORIZADO_PELA_CENTRAL_DE_SERVICOS("Autorizado pela CENTRAL DE SERVIÇOS."),
	SOLICITADO_PELA_CENTRAL_DE_SERVICOS("Solicitado pela CENTRAL DE SERVIÇOS"),
	SOLICITADO_PELO_PRESTADOR("Solicitado pelo prestador."),
	AUTORIZADO_PELO_DIGITADOR("Autorizado Pelo Digitador."),
	AUTORIZADO_PELA_EXISTENCIA_DE_CIDS_E_PROCEDIMENTOS_URGENCIA("Autorização justificada pela existência de CID e PROCEDIMENTO de urgência."),
	AUTORIZADO_NO_REGISTRO_DE_HONORARIOS("Autorizado no registro de honorários."),
	
	AUTORIZACAO_PRORROGACAO_GUIA("Autorização de Prorrogação de guia."),
	AUTORIZACAO_PROCEDIMENTO_CIRURGICO("Autorização de Procedimento."),
	AUTORIZACAO_SOLICITACAO_PRORROGACAO("Autorização de Solicitação de Prorrogação"),
	AUTORIZACAO_DE_PROCEDIMENTO_ODONTO("Autorização de procedimentos odontológicos."),
	INDICE_LIMITE_PROC_POR_CONSULTA_ULTRAPASSADO("Média-limite de procedimentos por consulta ultrapassado."),
	AUDITORIA_DE_HONORARIOS_CIRURGICOS("Auditado procedimentos cirúrgicos."),
	AUDITORIA_DE_HONORARIOS_CLINICOS("Auditado procedimentos clínicos."),
	AUDITORIA_DE_HONORARIOS_CLINICOS_E_CIRURGICOS("Auditado procedimentos clínicos e cirúrgicos."),
	
	AUDITORIA_DE_HONORARIOS("Auditoria de honorários {0}."),
	
	AUDITADO_AUTOMATICAMENTE("Guia auditada automaticamente."),
	GERADO_AUTOMATICAMENTE_DURANTE_AUDITORIA("Guia gerada automaticamente durante o processo de auditoria de guias de honorários médicos"),
	AUTORIZADO_EM_SOLICITACAO_DE_EXAMES_EM_INTERNACAO_ABERTA("Autorizado na solicitação de exames em internações abertas."),
	
	MIGRACAO_PROFISSIONAIS("Migração de profissionais."),
	
	CORRECAO_DE_CADASTRO("Correção do cadastro de beneficiário."),
	
	CADASTRO_PRESTADOR("Cadastro do Prestador."),
	CADASTRO_PROFISSIONAL("Cadastro do Profissional."),
	CADASTRO_NOVO("Cadastro novo."),
	CANCELADO_DURANTE_AUDITORIA("Cancelado(a) durante auditoria"),
	CANCELADO_OBITO("Cancelado(a) devido a Óbito."),
	CANCELADO_NO_PRESTADOR("Cancelada no Prestador: "),
	CADASTRO_ITEM_FARMACEUTICO_GUIA("Cadastro de item farmaceutico para a guia"),
	CADASTRO_AUTOMATICO("Cadastro Automático."),
	CANCELADO_NO_LANCAMENTO(""),
	CONSULTA_PROMOCIONAL_UTILIZADA("Utilizado pelo segurado."),
	CONSULTA_URGENCIA("Consulta de Urgência."),
	ATENDIMENTO_SUBSEQUENTE("Atendimento Subsequente."),
	CONFIRMADA_NO_PRESTADOR("Confirmada no Prestador."),
	CONFIRMADA_NO_ecare("Confirmada no ecare."),
	CONFIRMADA_NO_LANCAMENTO_MANUAL("Confirmada durante o lançamento manual."),
	CANCELAMENTO_DE_FATURAS("Cancelamento de Faturas"),
	CANCELAMENTO_HONORARIO("Cancelamento da guia de honorário."),
	
	EXCLUSAO_EXAME_PROCEDIMENTO("Exclusão de Exame/Procedimento/Tratamento"),
	EXECUTADO_NO_PRESTADOR("Executado no prestador"), 
	
	FECHAMENTO_GUIA("Fechamento de guia."),
	FECHAMENTO_GUIA_ANESTESISTA("Fechamento de guia pelo anestesista."),
	FECHAMENTO_PARCIAL("Fechamento parcial."),
	GUIA_EXAME_AGUARDANDO_FECHAMENTO("Guia confirmada e aguardando fechamento."),
	GUIA_AUDITADA("Guia auditada"),
	GUIA_VENCIDA("Guia expirada."),
	GUIA_REALIZADA("Guia realizada."),
	GUIA_EXAME_EXTERNO("Guia de exame externo"),
	GERACAO_FATURAMENTO("Geracao de faturamento"),
	
	INCLUIDO_PELO_AUDITOR("Incluído pelo Auditor."),
	INCLUSAO_ITEM("Inclusão do Item."),
	INTERNACAO_ELETIVA("Internação Eletiva."),
	INTERNACAO_URGENCIA("Internação de Urgência."),
	CIRURGIA_ODONTOLOGICA("Cirurgia Odontológica."),
	
	LIBERACAO_CONSULTA_PROMOCIONAL("Liberado para uso."),
	
	MIGRACAO_PARA_DEPENDENTE_SUPLEMENTAR("Migração de dependente para dependente suplementar {0}."),
	MIGRACAO_PARA_PENSIONISTA("Migração de dependente para pensionista."),
	
	NAO_AUTORIZADO_AUDITOR("Não Autorizado(a) pelo Auditor."),
	CANCELADO_AUDITOR("Cancelado(a) pelo Auditor."),
	NAO_AUTORIZACAO_SOLICITACAO_PRORROGACAO("Não Autorização de Solicitação de Prorrogação."),
	GUIA_PRE_AUDITADA_NO_FECHAMENTO("Guia auditada automaticamente."),
	
	PACIENTE_ATENDIMENTO_SUBSEQUENTE("Paciente em atendimento subsequente."),
	PACIENTE_CONSULTA_URGENCIA("Paciente em consulta de urgência."),
	PACIENTE_INTERNACAO_ELETIVA("Paciente em internação eletiva."),
	PACIENTE_CIRURGIA_ODONTO("Paciente em cirurgia odontológica."),
	PACIENTE_INTERNACAO_URGENCIA("Paciente em internação de urgência."),
	PRORROGACAO_GUIA_NAO_AUTORIZADA("Prorrogação de guia não autorizada."),
	PROCEDIMENTO_AUDITADO("Procedimento auditado."),
	PROCEDIMENTO_CONFIRMADO("Procedimento confirmado."),
	PROCEDIMENTO_GLOSADO("Procedimento Glosado."),
	
	SUJEITO_AUDITORIA("Sujeito a auditoria."),
	SOLICITACAO_DE_CONSULTA_URGENCIA("Solicitacao de consulta urgencia no prestador."),
	SOLICITACAO_NAO_AUTORIZADA("Solicitação não autorizada."),
	SOLICITACAO_PRORROGACAO("Solicitação de Prorrogação."),
	SOLICITACAO_DE_EXAMES_EXTERNOS_ESPECIAIS("Solicitação de exames externos especiais."),
	SOLICITACAO_DE_TRATAMENTO_ODONTOLOGICO("Solicitação de tratamento(s) odontológico(s)."),
	
	TRATAMENTO_SERIADO_INICIADO("Tratamento iniciado."),
	
	UTILIZADO_CONFIRMACAO_CONSULTA("Utilizado durante uma confirmaçao de Consulta."),
	UTILIZADO_CONFIRMACAO_CONSULTA_URGENCIA("Utilizado durante uma confirmaçao de Consulta de Urgência."),

	BENEFICIARIO_NAO_RECADASTRADO("Não recadastrado"),
	VENCIMENTO_CONSULTA_PROMOCIONAL("Vencido, prazo de validade expirado."),

	GUIA_GLOSADA_DURANTE_AUDITORIA("Guia glosada durante auditoria."), 
	GLOSADO_DURANTE_AUDITORIA("Glosado(a) durante auditoria."), 
	GUIA_AUDITADA_NA_CONFIRMACAO("Guia auditada automaticamente durante confirmação"), 
	GUIA_AUDITADA_AUTOMATICAMENTE("Guia auditada automaticamente."),
	PROCESSAMENTO_FECHAR_ORDENAMENTO("Processado(a) durante o fechamento de ordenamento"),
	AUTORIZADO_NA_AUDITORIA_DE_HONORARIOS("Inserido pelo auditor durante auditoria de honorário médico"),
	
	//Acordos
	ACORDO_ATIVO("Acordo ativo"), 
	
	EXCLUSAO_ITEM_PACOTE("Exclusão de Pacote."), 
	INCLUSAO_ITEM_PACOTE("Inclusao de Pacote"),
	GERACAO_DE_HONORARIO("Geração de Honorário"),
	HONORARIO_GLOSADO("Honorário Glosado"),
	VERIFICACAO_AUDITOR_EXTERNO("Verificado pelo auditor externo"),
	GLOSADO_AUTOMATICAMENTE("Glosado automaticamente devido à glosa do procedimento origem."),
	GUIA_DE_HONORARIO_GERADA_NA_AUDITORIA("Guia criada durante auditoria de honorário médico"),

	//Motivos para autorização parcial de guias de exames e tratamentos odontológicos.
	PROCEDIMENTO_PENDENTE_AUDITOR("Pendente de autorização ou documentação para autorizar."),
	PENDENTE_AUDITOR("Guia com procedimentos pedentes."),
	PARCIALMENTE_AUTORIZADA_AUDITOR("Guia parcialmente autorizada pelo auditor.");

	private String descricao;
	
	MotivoEnum(String descricao) {
		this.descricao = descricao;
	}

	public String getMessage(String... param) {
		String mensagem = this.descricao();
		for(int i = 0; i < param.length; i++){
			if(mensagem.contains("{" + i + "}"))
				mensagem = mensagem.replace("{" + i + "}", (String)param[i]);
		}
		return mensagem;
	}

	private String descricao(){
		return descricao;
	}
}