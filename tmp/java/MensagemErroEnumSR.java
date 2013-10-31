package br.com.infowaypi.ecare.enums;


public enum MensagemErroEnumSR {
    
    	GUIA_CANCELADA("Caro usuário, não é possível autorizar exames/procedimentos em guias CANCELADAS."),

	REGULARIZACAO_PRECOCE("Caro Usuário ainda não é necessário regularizar o dependente. \nData prevista para a próxima regularização: {0}."),
	ERRO_DEPENDENTE_COM_IDADE_SUPERIOR_A_PERMITIDA("O grau de parentesco informado não permite dependentes com idade superior a {0} anos."),
	MATRICULA_DUPLICADA("Já existe um BENEFICIÀRIO cadastrado com esta matricula."),
	SEGURADO_NAO_RECADASTRADO("Prezado Prestador, este atendimento não será autorizado porque este beneficiário  não realizou o Recadastramento Obrigatório."+
			" Por gentileza, oriente-o a entrar em contato com a CENTRAL DE RELACIONAMENTO através do telefone 0800 28 12345."),
	
	SEGURADO_SEM_DATA_DE_ADESAO("Prezado Prestador, este atendimento não será autorizado porque este beneficiário não possui sua DATA DE ADESÃO cadastrada no sistema."+
	" Por gentileza, oriente-o a entrar em contato com a CENTRAL DE RELACIONAMENTO através do telefone 0800 28 12345."),
			
	INDICE_NULO("Todos os índices devem ser informados!"),
	
	SEGURADO_POSSUI_2VIA_PENDENTE("Prezado(a) usuario(a), o beneficiário já possui um cartão para ser enviado na próxima remessa."),
	
	PROFISSIONAL_DIFERENTE("Prezado usuário, preencha apenas um dos campos: Médico(Conselho) ou Médico(Nome) para o médico."),
	
	ERRO_EXAME_ESPECIAL_EM_GUIA_EXTERNA("O EXAME de código {0} é especial e necessita de autorização."),
	
	ERRO_ATUALIZACAO_BASE_SALARIAL("Um erro ocorreu durante a atualização da base salarial. Foi informado um arquivo inválido. Reinicie o fluxo e informe um arquivo válido."),
	
	SEGURADO_CANCELADO_NO_SISTEMA("O beneficiário está cancelado no sistema."),
	
	ERRO_SEXO_INVALIDO_PREVENCAO("Consultas para a especialidade GINECOLOGIA podem ser realizadas apenas para beneficiarios do sexo FEMININO."),
	TIPO_DE_CONSULTA_REQUERIDA("Tipo de consulta deve ser informado"),
	BENEFICIARIO_REQUERIDO("Beneficiario deve ser informado."),
	BENEFICIARIO_SEM_CARENCIA_PARA_CONSULTA_INFORMADA("O Beneficiário(a) não cumpriu carência para o tipo de consulta informado."),
	QUANTIDADE("Quantidade informada está incorreta. Insira uma quantidade de sessões maior ou igual a 1."),
	USUARIO_SEM_NIVEL_DE_AUTORIZACAO("Essa guia possui procedimentos com nível de autorização {0}." +
			" Você não possui privilégios para autorizar procedimentos de nivel {0}."),
	
	OBRIGATORIEDADE_NO_PREENCHIMENTO_DOS_CAMPOS("É obrigatório o preenchimento de todos os campos abaixo."),
	PREENCHER_PELO_MENOS_1_CAMPO("É obrigatório o preenchimento de pelo menos 1(um) campo."),
	MAIS_DE_UM_BENEFICIARIO_PARA_O_CPF("Foram encontrados mais de um Beneficiário Ativo com o CPF {0}"),
	BENEFICIARIO_NAO_ENCONTRADO_PARA_O_CPF("Não foi encontrado Beneficiário com o CPF {0}"),
	DATA_DE_MARCACAO_COM_PRAZO_ULTRAPASSADO("Prezado prestador, a guia {0} foi marcada há mais de {1} dias e não pode mais ser CONFIRMADA."),
	MOTIVO_REQUERIDO("A não autorização do procedimento {0} requer uma justificativa. Por favor informe o motivo da não autorização."),
	MOTIVO_REQUERIDO_PENDENTE("A pendência do procedimento {0} requer uma justificativa. Por favor informe o motivo da pendência."),
	
	//Consignacoes
	SEM_EMPRESAS_PARA_GERACAO_DE_CONSIGNACOES("Não há empresas com consignaçoes a serem geradas para a competencia informada: {0}"),
	
	//Baixa Manual de Boletos
	BENEFICIARIO_SEM_MATRICULA_BOLETO("O Beneficiário {0} não tem matrículas com tipo de pagamento em Boletos"),
	BENEFICIARIO_COM_COBRANCA_NA_COMPETENCIA("Já existe uma cobrança Paga deste Beneficiário para esta competência."),
	COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_POSTERIOR("Não foi encontrada cobranças para esta competência e não é possível gerar cobrança para uma competência superior à atual"),
	COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_ANTIGA("Não foi encontrada cobranças para esta competência e não é possível gerar cobrança para uma competência muito antiga"),
	COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_ANTERIOR_DA_PRIMEIRA_COBRANCA("Não foi encontrado cobranças para esta competência e não é possível gerar cobrança para uma competência inferior à competência da primeira cobrança"),
	VALOR_PAGO_MENOR_QUE_VALOR_COBRADO("O Valor Pago não pode ser menor do que o Valor Cobrado no Boleto"), 
	BAIXA_MANUAL_COBRANCA_CANCELADA("Não é possível efetuar baixa manual de boletos cancelados."),
	COBRANCA_NAO_ENCONTRADA("Não foram encontradas cobranças para esta competência."),

	//Lote de Guias
	LOTE_NAO_ENCONTRADO("Não foi encontrado lote com identificador {0}."), 
	LOTE_JA_RECEBIDO("O lote de identificador {0} já foi recebido."), 
	LOTE_VAZIO("O lote não possui guias."), 
	NENHUMA_GUIA_APTA_A_SER_ENTREGUE("Não há nenhuma guia apta a ser entregue na competência {0}."),
	
	//Boletos
	NAO_E_POSSIVEL_CANCELAR_REMESSA_COM_BOLETO_PAGO("Não é possível cancelar remessa que contenha boleto pago"), 
	
	MOTIVO_DEVOLUCAO_REQUERIDO("A devolução da guia {0} requer uma justificativa. Por favor informe o motivo da devolução."),
	OPCAO_NAO_MARCADA("Marque uma opção para cada guia."),
	
	//HONORÁRIOS EXTERNOS
	NAO_HA_PROCEDIMENTOS_APTOS_PARA_GERAR_HONORARIOS("A guia informada não possui nenhum procedimento apto para gerar honorários."),	
	GUIA_EM_SITUACAO_NAO_PERMITIDA("A guia {0} está em situação {1}, portanto, não pode entrar no fluxo."),
	
	//Tratamento seriado
	INFORME_SE_A_GUIA_SERA_AUTORIZADA_OU_NAO("Caro usuário, informe se a guia será autorizada ou não."),
	GUIA_CONTEM_MAIS_DE_UM_TIPO("Caro usuário, a guia possui mais de um tipo de tratamento seriado."),
	NAO_E_POSSIVEL_AUTORIZAR_MAIS_GUIAS_QUE_O_SOLICITADO(" Caro usuário, o número de sessões autorizadas não pode ser superior ao número de sessões solicitadas."),
	NUMERO_DE_GUIAS_AUTORIZADAS_DEVE_SER_MAIOR_QUE_ZERO(" Caro usuário, o número de sessões autorizadas deve ser maior que 0(zero)."),
	LIMITE_EVENTUAL_ESTOURADO(" Caro usuário, a quantidade máxima permitida de sessões para o procedimento {0} é {1} "),
	
	//EXAMES
	PROFISSIONAL_NAO_CREDENCIADO("Caro usuário, o profissional selecionado não é credenciado."),
	NENHUM_PROCEDIMENTO_PACOTE_NA_GUIA("Insira pelo menos um procedimento ou pacote dentro da guia."),
	NENHUM_PROCEDIMENTO_APTO_A_SOLICITACAO_DE_ACOMPANHAMENTO_ANESTESICO("Não é permitido solicitar acompanhamento anestésico para esta guia pois a mesma não possui nenhum procedimento que permita isso."),
	
	//DATA GUIA
	DATA_INICIO_ATENDIMENTO_REQUERIDA("DATA DE INÍCIO DO ATENDIMENTO deve ser informada."),
	DATA_INICIO_ATENDIMENTO_POSTERIOR_A_DATA_TERMINO_ATENDIMENTO("DATA DE INÍCIO DO ATENDIMENTO não pode ser posterior à data de termino de atendimento."),
	DATA_CIRURGIA_POSTERIOR_A_DATA_FINAL("A DATA DE Realização do procedimento {0} não pode ser superior a data termino de atendimento informada."),
	
	//Faturamento
	COMPETENCIA_NAO_INFORMADA("A competência deve ser informada."),
	NAO_EXISTE_FATURAMENTO_PARA_COMPETENCIA_ANTERIOR("Não existe faturamento gerado para a competência: {0}"),
	JA_EXISTE_FATURAMENTO_PARA_COMPETENCIA_ATUAL("O Faturamento para a competência {0} já foi processado."),
	NAO_E_POSSIVEL_GERAR_FATURAMENTO_PARA_COMPETENCIA_POSTERIOR("Não é possível gerar faturamento para um mês corrente ou posterior."),
	
	//Auditoria GHM
	GUIA_SEM_HONORARIO("A guia selecionada não possui honorário(s) {0}."),
	NENHUM_TIPO_HONORARIO_SELECIONADO("Pelo menos um tipo de honorário deve ser escolhido para ser auditado."),
	
	ACESSO_NEGADO_PORTAL("Acesso Negado! Dúvidas entrar em contato com a Central de Relacionamento pelo numero 0800-28-12345.")
	;
	private String descricao;
	
	public String getMessage(String ... param){
		String mensagem = this.descricao();
		for(int i = 0; i < param.length; i++){
			if(mensagem.contains("{" + i + "}"))
				mensagem = mensagem.replace("{" + i + "}", (String)param[i]);
		}
		return mensagem;
	}
	
	MensagemErroEnumSR(String descricao){
		this.descricao = descricao;
	}
	
	private String descricao(){
		return descricao;
	}
}


