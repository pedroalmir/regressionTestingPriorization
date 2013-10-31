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
	AGENDADA_NO_LANCAMENTO_MANUAL("Agendada durante o lan�amento manual."),
	AGENDADA_PELO_TI("Agendada pelo Ti."),
	AGENDADA_PELO_DIGITADOR("Agendada Pelo Digitador."),
	AGENDADA_PELA_CENTRAL_DE_SERVICOS("Agendada pela Central de Servi�os."),
	AGENDADA_PELO_AUDITOR("Agendada pelo auditor."),
	AGENDADA_PELO_DIRETOR("Agendada pelo diretor."),
	AGENDAMENTO_AUDITOR("Agendada pelo Auditor"),
	AGENDAMENTO_INTERNACAO_ELETIVA("Agendamento de interna��o eletiva."),
	AGENDAMENTO_INTERNACAO_URGENCIA("Agendamento de interna��o de urg�ncia."),
	ATENDIMENTO_URGENCIA("Atendimento Subsequente."),
	INCLUSAO_DE_EXAMES_SIMPLES("Inclus�o de exames simples."),
	AUTORIZADO_PELO_AUDITOR("Autorizado pelo auditor."),
	AUTORIZADO_PELO_ROLE_INFORMADO("Autorizado pelo {0}."),
	NAO_AUTORIZADO_PELO_ROLE_INFORMADO("N�o autorizado pelo {0}."),
	AUTORIZADO_COM_PERICIA_FINAL("Autorizado com per�cia final."),
	AUTORIZADO_PELO_TI("Autorizado pelo Ti."),
	AUTORIZADO_PELA_DIRETORIA("Autorizado pelo diretor."),
	AUTORIZADO_PELA_CENTRAL_DE_SERVICOS("Autorizado pela CENTRAL DE SERVI�OS."),
	SOLICITADO_PELA_CENTRAL_DE_SERVICOS("Solicitado pela CENTRAL DE SERVI�OS"),
	SOLICITADO_PELO_PRESTADOR("Solicitado pelo prestador."),
	AUTORIZADO_PELO_DIGITADOR("Autorizado Pelo Digitador."),
	AUTORIZADO_PELA_EXISTENCIA_DE_CIDS_E_PROCEDIMENTOS_URGENCIA("Autoriza��o justificada pela exist�ncia de CID e PROCEDIMENTO de urg�ncia."),
	AUTORIZADO_NO_REGISTRO_DE_HONORARIOS("Autorizado no registro de honor�rios."),
	
	AUTORIZACAO_PRORROGACAO_GUIA("Autoriza��o de Prorroga��o de guia."),
	AUTORIZACAO_PROCEDIMENTO_CIRURGICO("Autoriza��o de Procedimento."),
	AUTORIZACAO_SOLICITACAO_PRORROGACAO("Autoriza��o de Solicita��o de Prorroga��o"),
	AUTORIZACAO_DE_PROCEDIMENTO_ODONTO("Autoriza��o de procedimentos odontol�gicos."),
	INDICE_LIMITE_PROC_POR_CONSULTA_ULTRAPASSADO("M�dia-limite de procedimentos por consulta ultrapassado."),
	AUDITORIA_DE_HONORARIOS_CIRURGICOS("Auditado procedimentos cir�rgicos."),
	AUDITORIA_DE_HONORARIOS_CLINICOS("Auditado procedimentos cl�nicos."),
	AUDITORIA_DE_HONORARIOS_CLINICOS_E_CIRURGICOS("Auditado procedimentos cl�nicos e cir�rgicos."),
	
	AUDITORIA_DE_HONORARIOS("Auditoria de honor�rios {0}."),
	
	AUDITADO_AUTOMATICAMENTE("Guia auditada automaticamente."),
	GERADO_AUTOMATICAMENTE_DURANTE_AUDITORIA("Guia gerada automaticamente durante o processo de auditoria de guias de honor�rios m�dicos"),
	AUTORIZADO_EM_SOLICITACAO_DE_EXAMES_EM_INTERNACAO_ABERTA("Autorizado na solicita��o de exames em interna��es abertas."),
	
	MIGRACAO_PROFISSIONAIS("Migra��o de profissionais."),
	
	CORRECAO_DE_CADASTRO("Corre��o do cadastro de benefici�rio."),
	
	CADASTRO_PRESTADOR("Cadastro do Prestador."),
	CADASTRO_PROFISSIONAL("Cadastro do Profissional."),
	CADASTRO_NOVO("Cadastro novo."),
	CANCELADO_DURANTE_AUDITORIA("Cancelado(a) durante auditoria"),
	CANCELADO_OBITO("Cancelado(a) devido a �bito."),
	CANCELADO_NO_PRESTADOR("Cancelada no Prestador: "),
	CADASTRO_ITEM_FARMACEUTICO_GUIA("Cadastro de item farmaceutico para a guia"),
	CADASTRO_AUTOMATICO("Cadastro Autom�tico."),
	CANCELADO_NO_LANCAMENTO(""),
	CONSULTA_PROMOCIONAL_UTILIZADA("Utilizado pelo segurado."),
	CONSULTA_URGENCIA("Consulta de Urg�ncia."),
	ATENDIMENTO_SUBSEQUENTE("Atendimento Subsequente."),
	CONFIRMADA_NO_PRESTADOR("Confirmada no Prestador."),
	CONFIRMADA_NO_ecare("Confirmada no ecare."),
	CONFIRMADA_NO_LANCAMENTO_MANUAL("Confirmada durante o lan�amento manual."),
	CANCELAMENTO_DE_FATURAS("Cancelamento de Faturas"),
	CANCELAMENTO_HONORARIO("Cancelamento da guia de honor�rio."),
	
	EXCLUSAO_EXAME_PROCEDIMENTO("Exclus�o de Exame/Procedimento/Tratamento"),
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
	
	INCLUIDO_PELO_AUDITOR("Inclu�do pelo Auditor."),
	INCLUSAO_ITEM("Inclus�o do Item."),
	INTERNACAO_ELETIVA("Interna��o Eletiva."),
	INTERNACAO_URGENCIA("Interna��o de Urg�ncia."),
	CIRURGIA_ODONTOLOGICA("Cirurgia Odontol�gica."),
	
	LIBERACAO_CONSULTA_PROMOCIONAL("Liberado para uso."),
	
	MIGRACAO_PARA_DEPENDENTE_SUPLEMENTAR("Migra��o de dependente para dependente suplementar {0}."),
	MIGRACAO_PARA_PENSIONISTA("Migra��o de dependente para pensionista."),
	
	NAO_AUTORIZADO_AUDITOR("N�o Autorizado(a) pelo Auditor."),
	CANCELADO_AUDITOR("Cancelado(a) pelo Auditor."),
	NAO_AUTORIZACAO_SOLICITACAO_PRORROGACAO("N�o Autoriza��o de Solicita��o de Prorroga��o."),
	GUIA_PRE_AUDITADA_NO_FECHAMENTO("Guia auditada automaticamente."),
	
	PACIENTE_ATENDIMENTO_SUBSEQUENTE("Paciente em atendimento subsequente."),
	PACIENTE_CONSULTA_URGENCIA("Paciente em consulta de urg�ncia."),
	PACIENTE_INTERNACAO_ELETIVA("Paciente em interna��o eletiva."),
	PACIENTE_CIRURGIA_ODONTO("Paciente em cirurgia odontol�gica."),
	PACIENTE_INTERNACAO_URGENCIA("Paciente em interna��o de urg�ncia."),
	PRORROGACAO_GUIA_NAO_AUTORIZADA("Prorroga��o de guia n�o autorizada."),
	PROCEDIMENTO_AUDITADO("Procedimento auditado."),
	PROCEDIMENTO_CONFIRMADO("Procedimento confirmado."),
	PROCEDIMENTO_GLOSADO("Procedimento Glosado."),
	
	SUJEITO_AUDITORIA("Sujeito a auditoria."),
	SOLICITACAO_DE_CONSULTA_URGENCIA("Solicitacao de consulta urgencia no prestador."),
	SOLICITACAO_NAO_AUTORIZADA("Solicita��o n�o autorizada."),
	SOLICITACAO_PRORROGACAO("Solicita��o de Prorroga��o."),
	SOLICITACAO_DE_EXAMES_EXTERNOS_ESPECIAIS("Solicita��o de exames externos especiais."),
	SOLICITACAO_DE_TRATAMENTO_ODONTOLOGICO("Solicita��o de tratamento(s) odontol�gico(s)."),
	
	TRATAMENTO_SERIADO_INICIADO("Tratamento iniciado."),
	
	UTILIZADO_CONFIRMACAO_CONSULTA("Utilizado durante uma confirma�ao de Consulta."),
	UTILIZADO_CONFIRMACAO_CONSULTA_URGENCIA("Utilizado durante uma confirma�ao de Consulta de Urg�ncia."),

	BENEFICIARIO_NAO_RECADASTRADO("N�o recadastrado"),
	VENCIMENTO_CONSULTA_PROMOCIONAL("Vencido, prazo de validade expirado."),

	GUIA_GLOSADA_DURANTE_AUDITORIA("Guia glosada durante auditoria."), 
	GLOSADO_DURANTE_AUDITORIA("Glosado(a) durante auditoria."), 
	GUIA_AUDITADA_NA_CONFIRMACAO("Guia auditada automaticamente durante confirma��o"), 
	GUIA_AUDITADA_AUTOMATICAMENTE("Guia auditada automaticamente."),
	PROCESSAMENTO_FECHAR_ORDENAMENTO("Processado(a) durante o fechamento de ordenamento"),
	AUTORIZADO_NA_AUDITORIA_DE_HONORARIOS("Inserido pelo auditor durante auditoria de honor�rio m�dico"),
	
	//Acordos
	ACORDO_ATIVO("Acordo ativo"), 
	
	EXCLUSAO_ITEM_PACOTE("Exclus�o de Pacote."), 
	INCLUSAO_ITEM_PACOTE("Inclusao de Pacote"),
	GERACAO_DE_HONORARIO("Gera��o de Honor�rio"),
	HONORARIO_GLOSADO("Honor�rio Glosado"),
	VERIFICACAO_AUDITOR_EXTERNO("Verificado pelo auditor externo"),
	GLOSADO_AUTOMATICAMENTE("Glosado automaticamente devido � glosa do procedimento origem."),
	GUIA_DE_HONORARIO_GERADA_NA_AUDITORIA("Guia criada durante auditoria de honor�rio m�dico"),

	//Motivos para autoriza��o parcial de guias de exames e tratamentos odontol�gicos.
	PROCEDIMENTO_PENDENTE_AUDITOR("Pendente de autoriza��o ou documenta��o para autorizar."),
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