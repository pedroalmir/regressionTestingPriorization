package br.com.infowaypi.ecare.enums;


public enum MensagemErroEnumSR {
    
    	GUIA_CANCELADA("Caro usu�rio, n�o � poss�vel autorizar exames/procedimentos em guias CANCELADAS."),

	REGULARIZACAO_PRECOCE("Caro Usu�rio ainda n�o � necess�rio regularizar o dependente. \nData prevista para a pr�xima regulariza��o: {0}."),
	ERRO_DEPENDENTE_COM_IDADE_SUPERIOR_A_PERMITIDA("O grau de parentesco informado n�o permite dependentes com idade superior a {0} anos."),
	MATRICULA_DUPLICADA("J� existe um BENEFICI�RIO cadastrado com esta matricula."),
	SEGURADO_NAO_RECADASTRADO("Prezado Prestador, este atendimento n�o ser� autorizado porque este benefici�rio  n�o realizou o Recadastramento Obrigat�rio."+
			" Por gentileza, oriente-o a entrar em contato com a CENTRAL DE RELACIONAMENTO atrav�s do telefone 0800 28 12345."),
	
	SEGURADO_SEM_DATA_DE_ADESAO("Prezado Prestador, este atendimento n�o ser� autorizado porque este benefici�rio n�o possui sua DATA DE ADES�O cadastrada no sistema."+
	" Por gentileza, oriente-o a entrar em contato com a CENTRAL DE RELACIONAMENTO atrav�s do telefone 0800 28 12345."),
			
	INDICE_NULO("Todos os �ndices devem ser informados!"),
	
	SEGURADO_POSSUI_2VIA_PENDENTE("Prezado(a) usuario(a), o benefici�rio j� possui um cart�o para ser enviado na pr�xima remessa."),
	
	PROFISSIONAL_DIFERENTE("Prezado usu�rio, preencha apenas um dos campos: M�dico(Conselho) ou M�dico(Nome) para o m�dico."),
	
	ERRO_EXAME_ESPECIAL_EM_GUIA_EXTERNA("O EXAME de c�digo {0} � especial e necessita de autoriza��o."),
	
	ERRO_ATUALIZACAO_BASE_SALARIAL("Um erro ocorreu durante a atualiza��o da base salarial. Foi informado um arquivo inv�lido. Reinicie o fluxo e informe um arquivo v�lido."),
	
	SEGURADO_CANCELADO_NO_SISTEMA("O benefici�rio est� cancelado no sistema."),
	
	ERRO_SEXO_INVALIDO_PREVENCAO("Consultas para a especialidade GINECOLOGIA podem ser realizadas apenas para beneficiarios do sexo FEMININO."),
	TIPO_DE_CONSULTA_REQUERIDA("Tipo de consulta deve ser informado"),
	BENEFICIARIO_REQUERIDO("Beneficiario deve ser informado."),
	BENEFICIARIO_SEM_CARENCIA_PARA_CONSULTA_INFORMADA("O Benefici�rio(a) n�o cumpriu car�ncia para o tipo de consulta informado."),
	QUANTIDADE("Quantidade informada est� incorreta. Insira uma quantidade de sess�es maior ou igual a 1."),
	USUARIO_SEM_NIVEL_DE_AUTORIZACAO("Essa guia possui procedimentos com n�vel de autoriza��o {0}." +
			" Voc� n�o possui privil�gios para autorizar procedimentos de nivel {0}."),
	
	OBRIGATORIEDADE_NO_PREENCHIMENTO_DOS_CAMPOS("� obrigat�rio o preenchimento de todos os campos abaixo."),
	PREENCHER_PELO_MENOS_1_CAMPO("� obrigat�rio o preenchimento de pelo menos 1(um) campo."),
	MAIS_DE_UM_BENEFICIARIO_PARA_O_CPF("Foram encontrados mais de um Benefici�rio Ativo com o CPF {0}"),
	BENEFICIARIO_NAO_ENCONTRADO_PARA_O_CPF("N�o foi encontrado Benefici�rio com o CPF {0}"),
	DATA_DE_MARCACAO_COM_PRAZO_ULTRAPASSADO("Prezado prestador, a guia {0} foi marcada h� mais de {1} dias e n�o pode mais ser CONFIRMADA."),
	MOTIVO_REQUERIDO("A n�o autoriza��o do procedimento {0} requer uma justificativa. Por favor informe o motivo da n�o autoriza��o."),
	MOTIVO_REQUERIDO_PENDENTE("A pend�ncia do procedimento {0} requer uma justificativa. Por favor informe o motivo da pend�ncia."),
	
	//Consignacoes
	SEM_EMPRESAS_PARA_GERACAO_DE_CONSIGNACOES("N�o h� empresas com consigna�oes a serem geradas para a competencia informada: {0}"),
	
	//Baixa Manual de Boletos
	BENEFICIARIO_SEM_MATRICULA_BOLETO("O Benefici�rio {0} n�o tem matr�culas com tipo de pagamento em Boletos"),
	BENEFICIARIO_COM_COBRANCA_NA_COMPETENCIA("J� existe uma cobran�a Paga deste Benefici�rio para esta compet�ncia."),
	COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_POSTERIOR("N�o foi encontrada cobran�as para esta compet�ncia e n�o � poss�vel gerar cobran�a para uma compet�ncia superior � atual"),
	COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_ANTIGA("N�o foi encontrada cobran�as para esta compet�ncia e n�o � poss�vel gerar cobran�a para uma compet�ncia muito antiga"),
	COBRANCA_NAO_ENCONTRADA_E_IMPOSSIBILIDADE_DE_GERACAO_COBRANCA_EM_COMPETENCIA_ANTERIOR_DA_PRIMEIRA_COBRANCA("N�o foi encontrado cobran�as para esta compet�ncia e n�o � poss�vel gerar cobran�a para uma compet�ncia inferior � compet�ncia da primeira cobran�a"),
	VALOR_PAGO_MENOR_QUE_VALOR_COBRADO("O Valor Pago n�o pode ser menor do que o Valor Cobrado no Boleto"), 
	BAIXA_MANUAL_COBRANCA_CANCELADA("N�o � poss�vel efetuar baixa manual de boletos cancelados."),
	COBRANCA_NAO_ENCONTRADA("N�o foram encontradas cobran�as para esta compet�ncia."),

	//Lote de Guias
	LOTE_NAO_ENCONTRADO("N�o foi encontrado lote com identificador {0}."), 
	LOTE_JA_RECEBIDO("O lote de identificador {0} j� foi recebido."), 
	LOTE_VAZIO("O lote n�o possui guias."), 
	NENHUMA_GUIA_APTA_A_SER_ENTREGUE("N�o h� nenhuma guia apta a ser entregue na compet�ncia {0}."),
	
	//Boletos
	NAO_E_POSSIVEL_CANCELAR_REMESSA_COM_BOLETO_PAGO("N�o � poss�vel cancelar remessa que contenha boleto pago"), 
	
	MOTIVO_DEVOLUCAO_REQUERIDO("A devolu��o da guia {0} requer uma justificativa. Por favor informe o motivo da devolu��o."),
	OPCAO_NAO_MARCADA("Marque uma op��o para cada guia."),
	
	//HONOR�RIOS EXTERNOS
	NAO_HA_PROCEDIMENTOS_APTOS_PARA_GERAR_HONORARIOS("A guia informada n�o possui nenhum procedimento apto para gerar honor�rios."),	
	GUIA_EM_SITUACAO_NAO_PERMITIDA("A guia {0} est� em situa��o {1}, portanto, n�o pode entrar no fluxo."),
	
	//Tratamento seriado
	INFORME_SE_A_GUIA_SERA_AUTORIZADA_OU_NAO("Caro usu�rio, informe se a guia ser� autorizada ou n�o."),
	GUIA_CONTEM_MAIS_DE_UM_TIPO("Caro usu�rio, a guia possui mais de um tipo de tratamento seriado."),
	NAO_E_POSSIVEL_AUTORIZAR_MAIS_GUIAS_QUE_O_SOLICITADO(" Caro usu�rio, o n�mero de sess�es autorizadas n�o pode ser superior ao n�mero de sess�es solicitadas."),
	NUMERO_DE_GUIAS_AUTORIZADAS_DEVE_SER_MAIOR_QUE_ZERO(" Caro usu�rio, o n�mero de sess�es autorizadas deve ser maior que 0(zero)."),
	LIMITE_EVENTUAL_ESTOURADO(" Caro usu�rio, a quantidade m�xima permitida de sess�es para o procedimento {0} � {1} "),
	
	//EXAMES
	PROFISSIONAL_NAO_CREDENCIADO("Caro usu�rio, o profissional selecionado n�o � credenciado."),
	NENHUM_PROCEDIMENTO_PACOTE_NA_GUIA("Insira pelo menos um procedimento ou pacote dentro da guia."),
	NENHUM_PROCEDIMENTO_APTO_A_SOLICITACAO_DE_ACOMPANHAMENTO_ANESTESICO("N�o � permitido solicitar acompanhamento anest�sico para esta guia pois a mesma n�o possui nenhum procedimento que permita isso."),
	
	//DATA GUIA
	DATA_INICIO_ATENDIMENTO_REQUERIDA("DATA DE IN�CIO DO ATENDIMENTO deve ser informada."),
	DATA_INICIO_ATENDIMENTO_POSTERIOR_A_DATA_TERMINO_ATENDIMENTO("DATA DE IN�CIO DO ATENDIMENTO n�o pode ser posterior � data de termino de atendimento."),
	DATA_CIRURGIA_POSTERIOR_A_DATA_FINAL("A DATA DE Realiza��o do procedimento {0} n�o pode ser superior a data termino de atendimento informada."),
	
	//Faturamento
	COMPETENCIA_NAO_INFORMADA("A compet�ncia deve ser informada."),
	NAO_EXISTE_FATURAMENTO_PARA_COMPETENCIA_ANTERIOR("N�o existe faturamento gerado para a compet�ncia: {0}"),
	JA_EXISTE_FATURAMENTO_PARA_COMPETENCIA_ATUAL("O Faturamento para a compet�ncia {0} j� foi processado."),
	NAO_E_POSSIVEL_GERAR_FATURAMENTO_PARA_COMPETENCIA_POSTERIOR("N�o � poss�vel gerar faturamento para um m�s corrente ou posterior."),
	
	//Auditoria GHM
	GUIA_SEM_HONORARIO("A guia selecionada n�o possui honor�rio(s) {0}."),
	NENHUM_TIPO_HONORARIO_SELECIONADO("Pelo menos um tipo de honor�rio deve ser escolhido para ser auditado."),
	
	ACESSO_NEGADO_PORTAL("Acesso Negado! D�vidas entrar em contato com a Central de Relacionamento pelo numero 0800-28-12345.")
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


