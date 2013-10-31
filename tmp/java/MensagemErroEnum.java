 package br.com.infowaypi.ecarebc.enums;

/**
 * Enumeration para encapsular a biblioteca de mensagens de erro do sistema
 * @author Danilo Nogueira Portela
 */
public enum MensagemErroEnum{
	  
	//Mensagens de erro para Segurados
	SEGURADO_COM_SEXO_INVALIDO_PARA_ESPECIALIDADE("O sexo do benefici�rio � inv�lido para a especialidade {0}."),
	SEGURADO_COM_SEXO_INVALIDO_PARA_PROCEDIMENTO("O sexo do benefici�rio � inv�lido para o procedimento {0}"),
	SEGURADO_NAO_INFORMADO("O benefici�rio deve ser informado!"),
	SEGURADO_INATIVO_NO_SISTEMA("O benefici�rio n�o est� ativo no sistema."),
	SEGURADO_COM_TITULAR_INATIVO_NO_SISTEMA("O titular referente ao benefici�rio n�o est� ativo no sistema."),
	SEGURADO_NAO_CUMPRIU_CARENCIA_PARA_O_PROCEDIMENTO("O benefici�rio ainda n�o cumpriu a CAR�NCIA para o PROCEDIMENTO e s� poder� realiz�-lo a partir do DIA {1}."),
	SEGURADO_COM_IDADE_INFERIOR_A_MINIMA_PARA_O_PROCEDIMENTO("A IDADE do BENEFICI�RIO � inferior � idade M�NIMA permitida para se realizar o PROCEDIMENTO {0} ({1} anos)."),
	SEGURADO_COM_IDADE_SUPERIOR_A_MAXIMA_PARA_O_PROCEDIMENTO("A IDADE do BENEFICI�RIO � superior � idade M�XIMA permitida para se realizar o PROCEDIMENTO {0} ({1} anos)."),
	SEGURADO_COM_LIMITE_ESTOURADO_ODONTO("Esta CONSULTA j� foi realizada para o BENEFICI�RIO no DIA {0} s� poder� ser realizada a partir do DIA {1}"),	
	SEGURADO_NAO_BENEFICIARIO("Este(a) segurado(a) n�o � benefici�rio(a)."),
	SEGURADO_COM_LIMITE_ESTOURADO("O BENEFICI�RIO j� atingiu seu LIMITE {0}"),
	SEGURADO_COM_LIMITE_ESTOURADO_INFORMA_PROXIMA_DATA("O BENEFICI�RIO j� atingiu seu LIMITE {0}. Sua �ltima consulta foi no dia {1} e s� poder� ser realizada a partir do dia {2}."),
	SEGURADO_CRIACAO_CONSUMO("Ocorreu um erro na cria��o do CONSUMO para a COMPET�NCIA {0}"),
	SEGURADO_SEM_LIMITE_PARA_CONSUMO_DE_EXAMES("Prezado(a) usuario(a), para essa guia s� ser� permitido a inser��o de {0} {1} devido ao limite do benefici�rio."),
	SEGURADO_SEM_ADESAO("BENEFICI�RIO sem data de ades�o."),
	SEGURADO_COM_NUMERO_DE_CONSULTAS_PROMOCIONAIS_LIBERADAS_EXTRAPOLADAS("Prezado(a) usuario(a), o beneficiario informado j� possui 2 (duas) consultas sem co-particpa��o liberadas. N�o � permitida a libera��o de uma terceira."),
	BENEFICIARIO_NAO_CUMPRIU_CARENCIA_PROCEDIMENTO("O BENEFICI�RIO ainda n�o cumpriu CAR�NCIA para o procedimento {0}. Este s� poder� ser realizado a partir de {1}."),
	BENEFICIARIO_NAO_CUMPRIU_CARENCIA_INTERNACAO("O BENEFICI�RIO ainda n�o cumpriu CAR�NCIA para {0}. A car�ncia termina dia {1}."),
	DEPENDENTE_COM_MENOS_DE_21_ANOS("O benefici�rio ainda n�o completou 21 anos."),
	DEPENDENTE_COM_TITULAR_ATIVO("Prezado usu�rio, o benefici�rio n�o est� apto a ser Pensionista, pois o seu Titular est� Ativo(a)."),
	DEPENDENTE_NAO_APTO_A_SER_PENSIONISTA("Prezado usu�rio, o benefici�rio n�o est� apto a ser Pensionista, pois o grau de parentesco deve ser apenas FILHO(A) OU C�NJUGE."),
	DEPENDENTE_SEM_GRAU_DE_PARENTESCO("Prezado usu�rio, o benefici�rio n�o possui nenhum grau de parentesco. V� at� o cadastro do benefici�rio e atualize o campo GRAU DE PARENTESCO."),
	BENEFICIARIO_NAO_ENCONTRADO("Prezado usu�rio, n�o foi encontrado nenhum benefici�rio DEPENDENTE com os par�metros informados."),
	
	//Mensagens de erro para Prestador
	PRESTADOR_NAO_INFORMADO("O PRESTADOR deve ser informado."),
	PRESTADOR_INATIVO_NO_SISTEMA("O PRESTADOR n�o est� ativo no sistema!"),
	PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE("O PRESTADOR n�o est� habilitado para realizar {0}!"),
	PRESTADOR_COM_TETOS_FINANCEIROS_INVALIDOS("Os TETOS FINANCEIROS informados s�o inv�lidos."),
	PRESTADOR_COM_LOGIN_INVALIDO("O login informado j� existe. Escolha outro nome para o login e tente novamente."),
	PRESTADOR_SEM_ATIVIDADE("O prestador deve fazer algum tipo de atendimento!"), 
	PRESTADOR_NAO_ATENDE_ESPECIALIDADE("O PRESTADOR informado n�o atende � ESPECIALIDADE {0}!"),
	PRESTADOR_SEM_ACORDO_ATIVO_PARA_O_PACOTE("Este prestador n�o possui acordo ativo para o pacote {0}."),
	ROLE_DO_USUARIO_DIFERENTE_DO_ROLE_DO_PRESTADOR("N�o � permitido cadastrar usu�rios com role diferente do role do prestador ao qual ele estar� vinculado."),
	TETO_PRESTADOR_MENSAL_EXAMES_ESTOUROU("Teto de realiza��o mensal de exames estourado. N�o � mais poss�vel realizar exames neste prestador durante o m�s corrente."),
	TETO_PRESTADOR_MENSAL_CONSULTAS_ESTOUROU("Teto de realiza��o mensal de consultas estourado. N�o � mais poss�vel realizar consultas neste prestador durante o m�s de {0}."),
	
	//Mensagens de erro para Profissional/Solicitante
	SOLICITANTE_INVALIDO("Solicitante inv�lido"),
	PROFISSIONAL_NAO_RESPONSAVEL("O profissional informado n�o foi respons�vel pela realiza��o de nenhum dos procedimentos cir�rgicos desta guia, portanto, n�o � poss�vel que o mesmo solicite procedimentos cl�nicos."),
	PROFISSIONAL_NAO_INFORMADO("O PROFISSIONAL deve ser informado."),
	SOLICITANTE_NAO_INFORMADO("O SOLICITANTE deve ser informado!"),
	PROFISSIONAL_SOLICITANTE_NAO_INFORMADO("O PROFISSIONAL solicitante deve ser informado!"),
	PROFISSIONAL_INATIVO_NO_SISTEMA("O PROFISSIONAL n�o est� ativo no sistema."),
	SOLICITANTE_INATIVO_NO_SISTEMA("O SOLICITANTE n�o est� ativo no sistema."),
	PROFISSIONAL_NAO_POSSUI_UMA_ESPECIALIDADE_DO_PRESTADOR("O PROFISSIONAL {0} n�o � cadastrado para nenhuma ESPECIALIDADE do prestador."),
	PROFISSIONAL_NAO_ATENDE_ESPECIALIDADE("O PROFISSIONAL informado n�o atende � ESPECIALIDADE {0}!"),
	PROFISSIONAL_NAO_HABILITADO_PARA_ESTA_OPERACAO("O Profissional {0} � perito e n�o pode realizar essa opera��o."),
	PROFISSIONAL_NAO_SOLICITOU_CONSULTA("O m�dico {0} n�o foi respons�vel por nenhuma consulta nos �ltimos 60 dias e, portanto, n�o pode solicitar exames para o segurado {1}."),
	
	//Mensagens de erro para Especialidade 
	ESPECIALIDADE_NAO_INFORMADA("A ESPECIALIDADE deve ser informada."),
	ESPECIALIDADE_NAO_CADASTRADA_PARA_O_PROFISSIONAL("O PROFISSIONAL {0} n�o atende a especialidade {1}."),
	ESPECIALIDADE_NAO_CADASTRADA_PARA_O_PRESTADOR("Este PRESTADOR n�o atende a especialidade {0}."),
	ESPECIALIDADE_CONSULTA_INVALIDA("A ESPECIALIDADE da consulta eletiva n�o pode ser {0}!"),
	
	//Mensagens de erro para Datas
	NAO_E_POSSIVEL_GERAR_REMESSA_PARA_COMPETENCIA_ANTERIOR_A_ULTIMA_INFORMADA("N�o � possivel gerar remessa para uma compet�ncia anterior � �ltima processada."),
	NAO_E_POSSIVEL_GERAR_REMESSA_PARA_COMPETENCIA_POSTERIOR_A_ULTIMA_INFORMADA("N�o � possivel gerar remessa para uma compet�ncia posterior � �ltima processada."),
	NAO_E_POSSIVEL_GERAR_REMESSA_PARA_A_COMPETENCIA_INFORMADA("N�o � poss�vel gerar remessa para a compet�ncia informada"),
	DATA_INICIAL_INVALIDA("DATA DE ABERTURA inv�lida."),
	DATA_CIRURGIA_NAO_PODE_SER_ALTERADA("A DATA DE REALIZA��O do procedimento {0} n�o pode ser alterada."),
	DATA_FINAL_INVALIDA("DATA DE FECHAMENTO inv�lida."),
	DATA_CIRURGIA_INFORMADA_PARA_PROCEDIMENTO_QUE_NAO_IRA_GERAR_HONORARIO("N�o � permitido informar a DATA DE REALIZA��O para o procedimento {0} pois o mesmo n�o foi selecionado para gerar honor�rio"),
	DATA_ABERTURA_REQUERIDA("DATA DE ABERTURA deve ser informada."),
	DATA_FECHAMENTO_REQUERIDA("DATA DE FECHAMENTO deve ser informada."),
	DATA_CIRURGIA_SUPERIOR_A_DATA_ATUAL("A DATA DE Realiza��o do procedimento {0} n�o pode ser superior a data atual."),
	DATA_CIRURGIA_INFERIOR_A_DATA_ATUAL("A DATA DE Realiza��o do procedimento n�o pode ser inferior a data atual."),
	DATA_CIRURGIA_INFERIOR_A_DATA_DE_ATENDIMENTO("A DATA DE REALIZA��O do procedimento {0} n�o pode ser inferior a data de atendimento."),
	DATA_FECHAMENTO_SUPERIOR_A_DATA_ATUAL("DATA DE FECHAMENTO n�o pode ser superior a data atual."),
	DATA_FECHAMENTO_INFERIOR_A_DATA_ATENDIMENTO("DATA DE FECHAMENTO n�o pode ser inferior a data de atendimento."),
	DATA_ATENDIMENTO_SUPERIOR_A_DATA_ATUAL("DATA DE ATENDIMENTO n�o pode ser superior a data atual."),
	PROFISSIONAL_SOLICITANDO_VISITA_COM_MENOS_DE_DEZ_DIAS("O profissional informado realizou o procedimento {0} h� menos de 10 dias, portanto, s� ser� poss�vel solicitar procedimentos cl�nicos a partir do dia {1}."),
	DATA_PROCEDIMENTO_MAIOR_QUE_DATA_AUTORIZACAO("DATA DE REALIZA��O do procedimento n�o pode ser inferior a data atual."),
	DATA_ATENDIMENTO_INFERIOR_A_DATA_MARCACAO("DATA DE ATENDIMENTO n�o pode ser inferior a data de autoriza��o da guia."),
	DATA_CIRURGIA_SUPERIOR_A_DATA_FECHAMENTO("DATA DA CIRURGIA n�o pode ser superior a DATA DE FECHAMENTO da guia."),
	DATA_CIRURGIA_SUPERIOR_A_DATA_ALTA("A data de realiza��o do procedimento {0} n�o pode ser superior a DATA DE ALTA da guia."),
	DATA_CIRURGIA_INFERIOR_A_DATA_ATENDIMENTO("DATA DA CIRURGIA n�o pode ser inferior a data de atendimento."),
	DATA_ATENDIMENTO_INVALIDA("DATA de ATENDIMENTO inv�lida."),
	DATA_ATENDIMENTO_NAO_INFORMADA("DATA de ATENDIMENTO n�o informada."),
	DATA_DE_ATENDIMENTO_COM_PRAZO_ULTRAPASSADO("A DATA de ATENDIMENTO ultrapassou o PRAZO para realiza��o da guia."),
	DATA_DE_AUTORIZACAO_COM_PRAZO_ULTRAPASSADO("Caro usu�rio, essa guia foi autorizada h� {0}" +
	" dias. Uma guia de interna��o pode ser confirmada em no m�ximo {1} dias ap�s a data de autoriza��o. "),
	DATA_DE_ATENDIMENTO_INFERIOR_A_MARCACAO("A DATA de ATENDIMENTO n�o pode ser menor que a DATA de MARCA��O da guia!"),
	PRAZO_EXCEDIDO_PARA_INCLUSAO_DE_PROCEDIMENTOS("O PRAZO para solicita��o de PROCEDIMENTOS foi excedido."),
	DATA_DE_ADESAO_INVALIDA("A data de ades�o n�o deve ser superior a data de hoje."),
	DATA_INICIAL_MAIOR_Q_A_FINAL("A DATA INICIAL n�o pode ser maior que a DATA FINAL."),
	DATA_CIRURGIA_REQUERIDA("A DATA DE REALIZA��O deve ser informada para o procedimento {0}."),
	DATA_REALIZACAO_REQUERIDA("A DATA DE REALIZA��O deve ser informada."),
	GUIA_NAO_PODE_SER_FECHADA_NO_DIA_QUE_FOI_GERADA("Uma guia parcial n�o pode ser fechada no mesmo dia em que foi aberta!"),
	DATA_PROCEDIMENTO_DEVE_ESTAR_ENTRE_INICIO_E_TERMINO_DO_ATENDIMENTO("DATA DE REALIZA��O do procedimento {0} deve estar entre a data de atendimento({1}) e a data de termino do atendimento({2})."),
	NECESSARIO_INFORMAR_SE_FOI_REALIZADO_NA_PARCIAL("� necess�rio informar se o procedimento {0} foi realizado nesta parcial."),
	NECESSARIO_INFORMAR_PACOTE_FOI_REALIZADO_NA_PARCIAL("� necess�rio informar se o pacote {0} foi realizado nesta parcial."),
	DATA_CIRURGIA_INCOMPATIVEL_COM_A_PARCIAL_INFORMADA("Data de realiza��o do procedimento {0} � incompat�vel com a parcial informada."),
	DATA_DE_TERMINO_ATENDIMENTO_INFERIOR_A_DATA_ATENDIMENTO("A DATA de T�RMINO DE ATENDIMENTO n�o pode ser menor que a DATA de ATENDIMENTO da guia!"),
	DATA_DE_RECEBIMENTO_INFERIOR_A_DATA_DE_TERMINO_ATENDIMENTO("A DATA de RECEBIMENTO n�o pode ser menor que a DATA de T�RMINO DE ATENDIMENTO da guia!"),
	DATA_TERMINO_ATENDIMENTO_NAO_PREENCHIDA_PARA_MUDANCA_SITUACAO("A data de t�rmino de atendimento deve ser preenchida para a mudan�a de situa��o"),
	DATA_RECEBIMENTO_NAO_PREENCHIDA_PARA_MUDANCA_SITUACAO("A data de recebimento deve ser preenchida para a mudan�a de situa��o"),
	
	//Mensagens de erro para Guia
	GUIA_SEM_PROCEDIMENTOS("Nenhum PROCEDIMENTO foi informado!"),
	GUIA_SEM_PROCEDIMENTOS_PARA_CANCELAR("A guia informada n�o possui procedimentos para serem cancelados."),
	NENHUMA_GUIA_ENCONTRADA("Nenhuma GUIA foi encontrada!"),
	NENHUMA_GUIA_SELECIONADA("Nenhuma GUIA foi selecionada!"),
	GUIA_SEGURADO_DIFERENTE("O BENEFICI�RIO deve ser o mesmo da guia de atendimento de urg�ncia."),
	GUIA_PRESTADOR_DIFERENTE("A guia de urg�ncia deve ser do mesmo prestador."),
	GUIA_NAO_PERTENCE_AO_TIPO("O n�mero da autoriza��o n�o se refere a uma guia de {0}."),
	GUIA_SEM_PROCEDIMENTOS_PARA_EXCLUSAO("A guia informada n�o possui procedimentos para serem exclu�dos."),
	GUIA_NAO_PODE_SOLICITAR_PROCEDIMENTOS("N�o � possivel solicitar PROCEDIMENTOS para a SITUA��O atual da GUIA."),
	GUIA_COM_PROCEDIMENTOS_DEMAIS("A QUANTIDADE de PROCEDIMENTOS da guia � SUPERIOR � permitida."),
	GUIA_EXAME_COM_PROCEDIMENTOS_ESPECIAIS_DEMAIS("A guia pode conter apenas 1(UM) PROCEDIMENTO ESPECIAL!"),
	GUIA_EXAME_COM_PROCEDIMENTO_ESPECIAL_E_OUTROS("Uma guia de EXAMES com procedimento ESPECIAL n�o pode conter OUTROS PROCEDIMENTOS!"),
	GUIA_URGENCIA_SEM_CIDS("Nenhum CID foi informado!"),
	GUIA_URGENCIA_SEM_QUADRO_CLINICO("Nenhum QUADRO CL�NICO foi informado!"),
	GUIA_NAO_ENCONTRADA("A guia com n�mero de autoriza��o {0} n�o foi encontrada."),
	GUIA_NAO_PODE_SER_CANCELADA("A guia de autoriza��o {0} est� {1} e n�o pode ser CANCELADA."),
	GUIA_NAO_PODE_SER_CONFIRMADA("A guia de autoriza��o {0} n�o pode ser confirmada por um prestador ANESTESISTA."),
	GUIA_NAO_CRIADA("A guia de {0} n�o foi criada!"),
	GUIA_INVALIDA("Guia de {0} invalida!"),
	GUIAS_NAO_ENCONTRADAS_PARA_AUDITORIA("Nenhuma GUIA para AUDITORIA foi encontrada."),
	GUIAS_NAO_ENCONTRADAS_PARA_AUTORIZACAO("Nenhuma GUIA para AUTORIZA��O foi encontrada."),
	GUIAS_NAO_ENCONTRADAS_PARA_REALIMENTACAO("N�o existem GUIAS desse BENEFICI�RIO para serem REALIMENTADAS!"),
	GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO("N�o existem GUIAS desse BENEFICI�RIO para serem {0}!"),
	GUIA_JA_ORIGINOU_INTERNACAO("Esta Guia j� originou uma INTERNA��O antes."),
	IMPOSSIVEL_ADICIONAR_EXAMES_NESTA_GUIA("Prezado usu�rio(a), n�o � permitido adicionar EXAMES em uma guia {0}."),
	IMPOSSIVEL_EXCLUIR_EXAMES_NESTA_GUIA("Prezado usu�rio(a), n�o � permitido excluir EXAMES em uma guia {0}."),
	IMPOSSIVEL_FECHAR_GUIA_PARA_ANESTESISTA("Prezado usu�rio(a), n�o � permitido FECHAR uma guia {0}."),
	EXAMES_EXTERNOS_SOMENTE_PARA_GUIA_EXTERNAS("Exames externos s� podem ser realizados para guias de outro prestador."),
	PACOTE_OU_ACOMODACAO_DEVE_SER_INFORMADO("Para esta interna��o caso n�o seja informado PACOTE deve ser informado pelo menos ACOMODA��O."),
	GUIA_NAO_E_DE_INTERNACAO("A autoriza��o informada n�o � de uma guia de interna��o."),
	GUIA_SEM_PROCEDIMENTOS_PARA_FLUXO("Esta guia n�o possui procedimentos {0} para serem {1}."),
	GUIA_NAO_CONFIRMADA_PARA_FECHAMENTO("Esta guia n�o est� ABERTA. Somente guias ABERTA podem ser FECHADAS."),
	GUIA_PROCEDIMENTOS_NIVEIS_MISTURADOS("Caro usu�rio, uma guia de solicita��o de exames n�o pode conter procedimentos de Nivel 1 junto a procedimentos de outros N�veis."),
	GUIA_NAO_PODE_SER_FECHADA("Caro usu�rio, a guia {0} n�o pode ser fechada, pois a diferen�a entre sua data de atendimento e sua data de fechamento supera o limite de {1} dias."),
	GUIA_SOLICITACAO_PENDENTE("Caro usu�rio, n�o � poss�vel solicitar prorroga��o para essa guia. Essa guia possui uma solicita��o de prorroga��o pendente."),
	GUIA_NAO_PODE_SER_FECHADA_PARCIALMENTE("Caro usu�rio, n�o � poss�vel fechar parcialmente esta guia."),
	NAO_E_POSSIVEL_ALTERAR_DATA_FECHAMENTO_DA_GUIA("N�o � poss�vel alterar a data de t�rmino de atendimento de uma guia parcial."),
	GUIA_DEVE_SER_GLOSADA_TOTALMENTE("Todos os itens da guias foram marcados para ser glosados. Por favor, glose a guia."),
	GUIA_ACOMPANHAMENTO_ANESTESICO_JA_EXISTE("J� existe guia de acompanhamento anestesico gerado para esta guia."),
	DATA_ATENDIMENTO_NAO_PREENCHIDA_PARA_MUDANCA_SITUACAO("A data de atendimento deve ser preenchida para a mudan�a de situa��o"),
	ERRO_EXCLUSAO_TODOS_PROCEDIMENTOS_GUIA_EXAME("Voc� tentou remover todos os procedimentos desta guia. Neste caso voc� deve cancelar a guia de exame"),
	ERRO_NENHUM_PROCEDIMENTO_SELECIONADO_PARA_EXCLUSAO_GUIA_EXAME("Voc� n�o selecionou procedimentos. � necess�rio selecionar pelo menos um procedimento para a remo��o."),

	
	//Mensagens de erro para Consulta/Atendimento
	CONSULTA_NAO_CUMPRIU_PERIODICIDADE("O BENEFICI�RIO j� possui uma consulta de {0} no DIA {1} e s� poder� realizar outra para esta mesma especialidade a partir do DIA {2}."),
	CONSULTA_PROMOCIONAL_INFORMAR_TODOS_OS_PARAMETROS("Para liberar uma consulta sem co-parti��o � necess�rio informar tamb�m o parm�tro {0}"),
	CONSULTA_URGENCIA_NAO_CUMPRIU_PERIODICIDADE("O(a) benefici�rio(a) {0} j� realizou consulta de urg�ncia DIA {1} e s� poder� realizar outra novamente a partir do DIA {2}, portanto est� apto(a) apenas a realizar atendimentos subsequentes."),
	ATENDIMENTO_URGENCIA_SEM_CONSULTA_ORIGEM("Prezado usu�rio(a), para realizar um atendimento subsequente � necess�rio registrar, previamente, uma consulta de urg�ncia."),
	GUIA_SEM_CONSULTA_ORIGEM("Nenhuma consulta foi realizada por este solicitante para este BENEFICI�RIO nos �ltimos {0} dias."),
	GUIA_ORIGEM_ULTRAPASSOU_30_DIAS("Sua consulta j� ultrapassou o prazo de 30 dias para realiza��o dos exames."),
	SOLICITACAO_APOS_PRAZO_ATENDIMENTO("N�o � permitido solicitar procedimentos para Consulta de Urgencias {0} horas ap�s seu atendimento. Guia: {1}, Atendimento: {2}"),
	BENEFICIARIO_JA_REALIZOU_CONSULTA_COM_PROFISSIONAL_ODONTO("O(a) segurado(a) {0} j� realizou consulta com o(a) profissional {1} nos �ltimos 30 dias"),
	BENEFICIARIO_NAO_POSSUI_CONSULTA_ODONTO_ANTERIOR("O(a) segurado(a) {0} n�o possui consulta odontol�gica com especialidade {1} nos �ltimos {2} dias"),

	//Mensagens de erro para Procedimento
	PROCEDIMENTO_NAO_INFORMADO("Nenhum PROCEDIMENTO foi informado."),
	PROCEDIMENTO_NAO_SELECIONADO("Nenhum PROCEDIMENTO foi selecionado."),
	PROCEDIMENTO_ESPECIAL_NAO_SOLICITADO("O PROCEDIMENTO {0} necessita de autoriza��o."),
	PROCEDIMENTO_INATIVO_NO_SISTEMA("O PROCEDIMENTO {0} n�o est� ativo no sistema."),
	PROCEDIMENTO_NAO_PODE_SER_BILATERAL("O PROCEDIMENTO {0} n�o pode ser BILATERAL."),
	PROCEDIMENTO_COM_ELEMENTO_JA_APLICADO_UNICIDADE("O TRATAMENTO {0} n�o pode ser realizado para o ELEMENTO {}, pois j� existe outro tratamento com UNICIDADE para o mesmo."),
	PROCEDIMENTO_UNICIDADE_JA_APLICADO_PARA_BENEFICIARIO("O PROCEDIMENTO {0} j� foi realizado para o BENEFICI�RIO e n�o pode ser realizado novamente."),
	PROCEDIMENTO_NAO_CUMPRIU_PERIODICIDADE("O PROCEDIMENTO {0} j� foi solicitado, agendado ou realizado na GUIA {3} para o BENEFICI�RIO no DIA {1} e s� poder� ser solicitado, agendado ou realizado a partir do DIA {2} ."),
	PROCEDIMENTO_INVALIDO_PARA_CONSULTA("O PROCEDIMENTO de c�digo {0} � inv�lido para uma guia de consulta."),
	PROCEDIMENTO_PADRAO_NAO_PODE_SER_EXCLUIDO("N�o � possivel excluir o PROCEDIMENTO {0}"),
	EXCLUSAO_PROCEDIMENTO_FATURADO("N�o � possivel excluir o PROCEDIMENTO {0} porque ele encontra-se FATURADO(a)"),
	PROCEDIMENTO_SOLICITADO_OUTRO_MODULO("O PROCEDIMENTO {0} s� pode ser solicitado no m�dulo {1}."),
	PROCEDIMENTO_VISIBILIDADE_INVALIDA("O PROCEDIMENTO {0} deve conter uma VISIBILIDADE!"),
	PROCEDIMENTO_QUANTIDADE_INVALIDA("A QUANTIDADE m�xima permitida para realizar esse exame � {0}"),
	PROCEDIMENTO_CIRURGICO_PODE_SER_INSERIDO_SOMENTE_PARA_INTERNACAO("Procedimentos cir�rgicos podem ser inseridos somente em guias de interna��o"),
	ERRO_EXAME_ESPECIAL_EM_GUIA_EXTERNA("O EXAME de c�digo {0} � especial e necessita de autoriza��o."),
	PROCEDIMENTO_ODONTO_SEM_ESTRUTURAS("Nenhuma ESTRUTURA odontol�gica foi informada para o procedimento: ({0})!"),
	PROCEDIMENTO_QUANTIDADE_ESTRUTURAS_INVALIDA("O procedimento ({0}) s� pode ser aplicado em at� {1} {2}(s)!"),
	PROCEDIMENTO_COM_ESTRUTURAS_INVALIDAS("O procedimento ({0}) s� pode ser aplicado em um mesmo {1}!"),
	PROCEDIMENTO_JA_REALIZOU_PERICIA_INCIAL("O TRATAMENTO {0} j� passou por per�cia inicial!"),
	PROCEDIMENTO_EM_PERICIA_INICIAL_NAO_AUTORIZADO("O TRATAMENTO {0} deve passar pelo processo de autoriza��o na PER�CIA INCIAL!"),
	PROCEDIMENTO_EM_PERICIA_FINAL_NAO_AUTORIZADO("O TRATAMENTO {0} deve ser AUTORIZADO para ser marcado como PER�CIA FINAL!"),
	PROCEDIMENTO_SOLICITADO_NAO_AUTORIZADO("Todos os TRATAMENTOS devem passar pelo processo de AUTORIZA��O antes de finaliz�-lo!"),
	PROCEDIMENTO_VISITA_HOSPITALAR("Prezado(a) usuario(a), o procedimento {0} s� pode ser vinculado a um profissional {1} credenciado."),
	PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA("O motivo da Altera��o/Glosa para o procedimento {0} deve ser preenchido."),
	ITEM_GLOSADO_SEM_MOTIVO_DE_GLOSA("O motivo da Altera��o/Glosa para o item {0} deve ser preenchido."),
	PROCEDIMENTO_GLOSA_DESFEITA_SEM_MOTIVO_DE_GLOSA("O motivo para desfazer a glosa do procedimento {0} deve ser preenchido."),
	PROCEDIMENTO_CANCELADO_SEM_MOTIVO("O motivo do cancelamento do procedimento {0} deve ser informado."),
	PROCEDIMENTO_NAO_MARCADO_PARA_SER_GLOSADO("O Procedimento {0} n�o foi marcado para ser glosado. S� ser� poss�vel substitu�-lo se a op��o \"Alterar/Glosar\" estiver marcada"),
	MAIS_DE_UM_PROCEDIMENTO_A_100_PORCENTO_NA_MESMA_DATA("Caro usu�rio, n�o � permitido autoriza��o de mais de um procedimento cirurgico 100% na mesma data ({0})."),
	NENHUM_PROCEDIMENTO_A_100_PORCENTO_NA_GUIA("Caro usu�rio, � necess�rio que ao menos um procedimento cirurgico seja de 100%"),
	NENHUM_PACOTE_A_100_PORCENTO_NA_GUIA("Caro usu�rio, � necess�rio que ao menos um pacote seja de 100%"),
	ERRO_NO_PROCEDIMENTO_AO_FECHAR_GUIA("Voc� tentou fechar uma guia com procedimento de N�vel 2 ainda em situa��o Solicitado(a). N�o � poss�vel fechar uma guia com procedimento ainda pendente de regula��o. Aguarde o posicionamento do regulador do Sa�de Recife."),
	ERRO_NO_PROCEDIMENTO_AO_REGISTRAR_ALTA("Voc� tentou registrar Alta/Evolu��o para uma guia com procedimento de N�vel 2 ainda em situa��o Solicitado(a). N�o � poss�vel registrar Alta/Evolu��o para uma guia com procedimento ainda pendente de regula��o. Aguarde o posicionamento do regulador do Sa�de Recife."),
	
	//HONORARIOS
	PROCEDIMENTO_NAO_PODE_GERAR_HONORARIO_DATA_DE_REALIZACAO("N�o � poss�vel gerar honor�rio para o procedimento {0} pois o mesmo j� foi realizado h� mais de {1} dias"),
	HONORARIO_DUPLICADO ("J� foi gerado um honor�rio de {0} com o mesmo procedimento {1}"),
	HONORARIO_DUPLICADO_PACOTE ("J� foi gerado um honor�rio para o pacote {0}."),
	NAO_GERAR_HONORARIO_PARA_PORTE_ANESTESICO_ZERO ("Procedimentos com porte anest�sico 0 (ZERO) n�o geram honor�rio individual."),
	GERAR_HONORARIO_ANESTESISTA_AUXILIAR_APENAS_PARA_PORTE_ANESTESICO_SETE_OU_OITO ("Apenas procedimentos de porte anest�sico 7 (SETE) e 8 (OITO) geram honor�rio para AUXILIAR ANESTESISTA."),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_RESPONSAVEL_DIFERENTE ("N�o se pode gerar guia de honor�rio individual pra um cirurgi�o diferente do informado no procedimento."),
	PROCEDIMENTO_COM_HONORARIO_NAO_PODE_SER_GLOSADO ("O procedimento {0} possui honor�rios e n�o pode ser glosado.\n As seguintes guias devem ser glosadas ou canceladas: {1}"),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_RESPONSAVEL_DIFERENTE_PROFISSIONAL_DO_PRESTADOR_MEDICO("N�o se pode gerar guia de honor�rio individual pra um CIRURGI�O que n�o seja {0}."),
	PRESTADOR_PESSOA_FISICA_NAO_ENCONTRADO_PARA_O_PROFISSIONAL_INFORMADO("Caro usu�rio, voc� n�o possui v�nculo com o profissional informado. Por favor certifique-se de que o cadastro de ambos est� atualizado."),
	PROCEDIMENTO_COM_HONORARIO_NAO_PODE_SER_EXCLUIDO ("Este procedimento possui ou j� possuiu honor�rios e n�o pode ser exclu�do, apenas glosado."),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_PRESTADOR_PESSOA_FISICA_DIFERENTE_DO_INFORMADO_NO_PROCEDIMENTO("N�o se pode gerar guia de honor�rio individual para um cirurgi�o diferente do informado no procedimento {0}."),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_PRESTADOR_PESSOA_FISICA_DIFERENTE("O profissional {0} n�o realizou nenhum procedimento na guia {1} e portanto n�o pode gerar honor�rios para a mesma."),
	GUIA_SEM_PROCEDIMENTOS_COM_ACOMPANHAMENTO_ANESTESICO("Esta guia n�o possui procedimentos com porte anest�sico para gerar honor�rios."),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_RESPONSAVEL_PRA_PROCEDIMENTO_GLOSADO("N�o � permitido inserir honor�rio para grau de cirurgi�o para um procimento em situa��o Glosado(a)"),
	
	//Mensagens de erro para Elemento
	ELEMENTO_NAO_INFORMADO("O(a) {0} deve ser informado para o TRATAMENTO {1}!"),
	ESTRUTURA_NAO_INFORMADA("O(a) {0} deve ser informado para o TRATAMENTO {1}!"),
	PROCEDIMENTO_APLICADO_A_ELEMENTO_UNICIDADE("O TRATAMENTO {0} est� sendo aplicado a um ELEMENTO inv�lido."),
	ELEMENTO_JA_APLICADO_UNICIDADE_NA_GUIA("Um ELEMENTO que recebeu o TRATAMENTO ({0}) n�o pode receber outros tratamentos!"),
	
	//Mensagens de erro para Pessoa F�sica
	NOME_INVALIDO("O NOME informado � inv�lido."),
	PESSOA_FISICA_COM_CPF_INVALIDO("O CPF informado n�o est� correto."),
	
	//Mensagens de erro para Profissional
	CAMPO_CPF_JA_CADASTRADO("Este CPF j� foi cadastrado para outro Profissional"),
	CAMPO_CRM_JA_CADASTRADO("Este n�mero de conselho j� foi cadastrado para outro Profissional"),
	PROFISSIONAl_NAO_ASSOCIADO_AO_PRESTADOR("O profissional {0} n�o faz parte do corpo cl�nico do prestador {1}."),
	PROFISSIONAl_NAO_CREDENCIADO("O profissional {0} n�o � credenciado."),
	
	//Mensagens de erro para Pessoa Jur�dica
	NOME_FANTASIA_INVALIDO("O NOME FANTASIA informado � inv�lido."),
	PESSOA_JURIDICA_COM_CNPJ_INVALIDO("O CNPJ informado n�o est� correto."),
	
	//Mensagens de erro para urg�ncia
	DIARIA_NAO_INFORMADA("Informe uma Acomoda��o com a quantidade de Horas."),
	URGENCIA_DIARIA_24H("Guias de urg�ncia s� podem conter acomoda��es de at� 1 dia!"),
	URGENCIA_DIARIAS_DEMAIS("Apenas 1 (uma) Acomoda��o/Di�ria deve ser informada!"),
	URGENCIA_APENAS_PACOTES_OU_PROCEDIMENTOS_COM_DIARIA("Guias de urg�ncia devem conter apenas PACOTES ou PROCEDIMENTOS Cir�rgicos COM DI�RIA."),
	INTERNACAO_APENAS_PACOTES_OU_ACOMODACAO("Para guias de interna��o deve ser informado PACOTES ou ACOMODA��ES."),
	INTERNACAO_CIRURGICA_DEVE_CONTER_PROCEDIMENTOS("A guia de interna��o deve conter pelo menos 1 (um) PROCEDIMENTO."),
	INTERNACAO_VAZIA_NAO_PERMITIDA("Prezado(a) usuario(a), n�o � permitida a cria��o de guias vazias."),
	URGENCIA_COM_PROCEDIMENTOS_SEM_DIARIA("Guias de Urg�ncia devem conter PROCEDIMENTO(S) Cir�rgico(s) COM DI�RIA."),
	QUADRO_CLINICO_NAO_INFORMADO("O quadro cl�nico deve ser preenchido!"),
	SEGURADO_SUSPESO_NAO_PODE_SOLICITAR_EXAME_COMPLEXO("Prezado(a) usu�rio(a), n�o � poss�vel solicitar exames complexos para um segurado suspenso."),
	
	//Outras mensagens de erro
	VALIDACAO_MINUTOS("O Campo Hora/Minuto foi preenchido com uma valor inv�lido para minutos"),
	PROFISSIONAL_PELO_MENOS_UMA_OPCAO("Preencha pelo menos uma das op��es referentes ao m�dico escolhido"),
	CAMPO_NAO_PODE_SER_ALTERADO("O campo {0} n�o pode ser alterado."),
	NAO_FOI_POSSIVEL_GERAR_AUTENTICACAO("N�o foi poss�vel gerar n�mero de autentica��o."),
	CODIGO_INVALIDO("O C�DIGO informado � inv�lido."),
	NUMERO_AUTORIZACAO_INVALIDO("N�mero de AUTORIZA��O inv�lido!"),
	DESCRICAO_INVALIDA("A DESCRI��O informada � inv�lida."),
	SEXO_INVALIDO("O SEXO informado � inv�lido."),
	DOCUMENTO_INVALIDO("O DOCUMENTO informado � inv�lido."),
	VALOR_MATERIAIS_INVALIDO("O campo Valor Materiais est� preenchido com valor inv�lido."),
	VALOR_MEDICAMENTOS_INVALIDO("O campo Valor Medicamentos est� preenchido com valor inv�lido."),
	SEGURADO_INTERNADO_OU_COM_SOLICITACAO("Este BENEFICI�RIO j� est� internado ou possui uma solicita��o de interna��o."),
	AUSENCIA_DE_PARAMETROS("Pelo menos um par�metro deve ser informado."),
	LIMITE_DIAS_ULTRAPASSADO("Prezado usu�rio, a diferen�a entre a data final e a data inicial n�o pode ser superior a 31 dias."),
	LIMITE_DIAS_ULTRAPASSADO_UNIPLAM("Prezado usu�rio, a diferen�a entre a data final e a data inicial n�o pode ser superior a 90 dias."),
	LIMITE_IDADE_NAO_PERMITIDO_ESPECIALIDADE("Prezado usu�rio, essa especialidade n�o � v�lida para segurados com idade fora do intervalo de {1} � {2} anos. Idade do segurado {0} anos."),
	LIMITE_DIAS_ULTRAPASSADO_RECURSO_GLOSA("Prezado usu�rio, a diferen�a entre a data final e a data inicial n�o pode ser superior a 60 dias."),
	CAMPO_MOTIVO_DE_GLOSA_REQUERIDO("O campo \"Motivo da glosa\" deve ser preenchido ao se escolher \"Glosar guia\"."),
	CAMPOS_DE_PREENCHIMENTO_OBRIGATORIO("Caro usu�rio, os campos \"Operador L�gico\" e \"Permanencia\" devem ser preenchidos juntos."),
	CAMPOS_DE_PREENCHIMENTO_DESNECESSARIOS("Caro usu�rio, os campos \"Operador L�gico\" e \"Permanencia\" s� devem ser preenchidos caso a consulta seja sobre guias do tipo INTERNA��O."),
	VALOR_ACORDO_INVALIDO("O valor do acordo n�o pode ser igual a zero."),
	MOTIVO_AUTORIZACAO_ACOMODACAO_REQUERIDO("Caro usu�rio, por favor informe o motivo da (n�o)autoriza��o da acomoda��o {0}."),
	
	//Mensagens de erro para buscar segurado
	SEGURADO_NAO_ENCONTRADO("N�o foi encontrado BENEFICI�RIO com o n�mero de CART�O {0} e CPF {1}."),
	CPF_SEGURADO_NAO_ENCONTRADO("N�o foi encontrado BENEFICI�RIO com o n�mero de CPF {0}"),
	CARTAO_SEGURADO_NAO_ENCONTRADO("N�o foi encontrado BENEFICI�RIO com o n�mero de CART�O {0}"),
	PROPOSTA_NAO_INFORMADA("O N�mero de CART�O deve ser informado."),
	CART�O_INVALIDO("N�mero de CART�O inv�lido!"),
	EMPRESA_NAO_INFORMADA("O nome da EMPRESA deve ser informado."),
	SEGMENTACAO_INCOMPATIVEL("A cobertura do segurado n�o permite esse tipo de Atendimento."),

	ERRO_AO_GERAR_CARTAO("Erro ao gerar n�mero de cart�o."),
	ERRO_CID_DE_URGENCIA_NAO_ENCONTRADA("As cids informadas n�o justificam a cria��o de uma guia de urg�ncia."),
	
	NAO_EXISTEM_GUIAS_A_FATURAR("N�o existem guias para ser faturadas."),
	FATURAMENTOS_NAO_GERADOS("N�o foi encontrado nenhum faturamento para essa compent�ncia!"),
	FATURAMENTO_NAO_GERADO_PARA_O_PRESTADOR("N�o foi encontrado nenhum faturamento desse prestador para essa compent�ncia!"),
	FATURAMENTO_DUPLICADO("Caro usu�rio, foi detectada a gera��o de faturamento em duplicidade para um prestador. Contacte a equipe de suporte para a resolu��o do problema."),
	
	//Mensagens de erro para buscar guias
	SOLICITACAO_PRORROGACAO_NAO_PERMITIDA("A prorroga��o desta guia s� poder� ser solicitada a partir de {0}"),
	MOTIVO_CANCELAMENTO_NAO_INFORMADO("O MOTIVO do CANCELAMENTO deve ser preenchido.") ,
	NAO_E_POSSIVEL_CANCELAR_TODOS_OS_PROCEDIMENTOS("N�o � poss�vel cancelar todos os procedimentos da guia. Nesse caso � necess�rio cancelar a guia."),
	
	//Mensagens de erro para consumos
	ERRO_CRIACAO_CONSUMO("Ocorreu um erro na cria��o do consumo para a compet�ncia {0}!"),
	
	//Mensagens de erro para �ndices
	MEDIA_PROCEDIMENTOS_POR_CONSULTA_ANORMAL("A m�dia de exames diverge da m�dia adotada pelo Iapep Sa�de. " +
			"Por este motivo a marca��o do(s) exame(s) dever� ser realizada em outro Prestador"),
			
	//Mesagens de erros para �ndices
	INDICE_NULO("Todos os �ndices devem ser informados!"),
	INDICE_NAO_ENCONTRADO("Nenhum �NDICE relacionado a {0} foi encontrado para A COMPET�NCIA {1}!"),
	
	// Mensagem de erro para LoteDeGuias
	SITUACAO_GUIAS_LOTE("Somente guias fechadas ou devolvidas s�o aceitas em um lote."),
	PRAZO_DE_ENTREGA("Lote com prazo de entrega ultrapassado. Prazo de entrega: {0} a {1}."),
	DIAS_UTEIS("A entrega de lotes � permitida somente em dias �teis."),
	EXCESSO_DE_GUIAS("O lote possui mais de {0} guias."),
	MINIMO_DE_GUIAS("O lote deve possuir pelo menos uma guia."),
	TIPO_DE_GUIAS("As guias do lote devem ser do mesmo tipo."),
	GUIAS_PRESTADOR("As guias devem pertencer ao mesmo prestador."),
	GUIA_ULTRAPASSADA("A guia {0} ultrapassou o prazo de entrega. Essa guia deveria ser entregue at� o dia {1}."),
	TIPO_DE_GUIA_NAO_INFORMADO("O tipo de guia deve ser informado."),
	TIPO_DE_GUIA_INVALIDO("A guia deve ser de urg�ncia, de interna��o ou de exame."),
	DATAS_INCOMPATIVEIS("O lote n�o pode ser entregue antes de ter sido criado."), 
	LOTE_NAO_ENCONTRADO("Nenhum lote encontrado com o identificador: {0}."), 
	GUIA_NAO_POSSUI_LOTE("A guia {0} n�o pertence a nenhum lote."), 
	LOTE_VAZIO("O lote n�o possui guias."),
	MOTIVO_DEVOLUCAO_JA_CADASTRADO("Motivo de devolu��o j� cadastrado."),
	
	GUIA_INAPTA_PARA_ALTERACAO_PROCEDIMENTOS("N�o � permitido alterar guias de {0} com a situa��o {1}."),
	
	//TERAPIA SERIADA
	LIMITE_TRATAMENTO_SERIADO_ESTOURADO("Caro usu�rio, o segurado j� atingiu o limite de solicita��es para este procedimento"),
	LIMITE_TIPO_PERIODO_ULTRAPASSADO("Caro usu�rio, o benefici�rio atingiu o limite de sess�es permitidas para o tratamento de {0}"),
	
	//Mensagem de erro para nova hierarquia de guia de exame
	GUIA_EXAME_NAO_POSSUI_DIARIA("Guia de exame n�o possui di�ria."),
	
	
	//Fechamento de guia
	FECHAMENTO_EXIGE_ALTA("Caro usu�rio, para realizar o fechamento total desta guia � necess�rio registrar a alta hospitalar."),
	DESCRICAO_VALOR_OUTROS_DEVE_SER_INFORMADA("Quando o campo OUTROS VALORES for informado o campo DESCRI��O OUTROS tamb�m deve ser informado."), 
	GUIA_ORIGEM_NAO_POSSUI_DIARIA("A guia de origem n�o possui a di�ria solicitada: {0}."), 
	GUIA_ORIGEM_NAO_POSSUI_DIARIAS("A guia de origem n�o possui nenhuma di�ria."),
	FECHAMENTO_PARCIAL_APOS_PRAZO("A data final ara uma guia parcial deve ser na m�ximo ap�s {0} dias."),
	FECHAMENTO_DE_GUIAS_COM_PROCEDIMENTOS_SOLICITADOS("A guia n�o pode ser fechado pois os seguintes procedimentos est�o na situa��o \"Solicitado(a)\": {0}. A guia s� poder� ser fechada ap�s a autoriza��o, ou n�o autoriza��o, destes procedimentos."),
	
	//PAGAMENTO
	SOMENTE_MATRICULA_BOLETO_PODE_SER_REATIVADA("Apenas matr�culas que pagam em boleto podem ser (re)ativadas."),
	NAO_HA_BOLETO_A_SER_GERADO("N�o h� boleto a ser gerado!"),
	BOLETO_ABERTO_PARA_SEGURADO("O benefici�rio {0} j� possui um boleto aberto e n�o vencido para esta compet�ncia, por favor, reimprima-o."),
	SEGURADO_POSSUI_BOLETO_PAGO("O benefici�rio {0} j� possui um boleto pago para esta compet�ncia. N�o � poss�vel reger�-lo."),
	
	
	//ORDENADOR
	VALOR_TETO_MAIOR_QUE_LIMITE_INFORMADO("O Valor R$ {0} do faturamento {1} supera o Limite informado de R$ {2}"),
	
	
	//Boleto
	IMAGEM_DE_COMUNICACAO_ALTURA_ERRADA("A imagem de comunicacao deve possuir {0} pixels de altura."),
	IMAGEM_DE_COMUNICACAO_LARGURA_ERRADA("A imagem de comunicacao deve possuir {0} pixels de largura."),
	IMAGEM_DE_ENVELOPE_ALTURA_ERRADA("A imagem do envelope deve possuir {0} pixels de altura."),
	IMAGEM_DE_ENVELOPE_LARGURA_ERRADA("A imagem do envelope deve possuir {0} pixels de largura."),
	QUESTIONARIO_ALERTA_CID("O benefici�rio possue DLPs ligadas ao(s) CID(s) contido(s) nesta guia, favor consulte o cadastro do benefici�rio para verifica��o"),
	QUESTIONARIO_ALERTA_SUB_GRUPO("O benefici�rio possui DLPs ligadas ao(s) procedimento(s) contido(s) nesta guia, favor consulte o cadastro do benefici�rio para verifica��o. C�digo: {0}"),
	
	// Registro de honorarios de procedimentos contidos em guias Faturado(a) ou Pago(a)
	PROCEDIMENTO_COM_HONORARIO_PAGO("N�o � poss�vel registrar honor�rio para o procedimento {0} para o grau cirurgi�o. Os honor�rios de cirurgi�o referentes a este procedimento j� foram pagos dentro da guia de origem."),
	
	NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__CBHPM("Caro usu�rio, voc� tentou alterar o procedimento de c�digo {0} para o procedimento de c�digo {1}. N�o � permitido alterar procedimentos contidos em guias nas situa��es 'Faturado(a)' ou 'Pago(a)'."),
	NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__PORCENTAGEM("Caro usu�rio voc� tentou alterar a porcentagem do procedimento de c�digo {0}. N�o � permitido alterar a porcentagem de procedimentos contidos em guias nas situa��es 'Faturado(a)' ou 'Pago(a)'."),
	NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__GLOSA("Caro usu�rio voc� tentou glosar o procedimento de c�digo {0}. N�o � permitido glosar procedimentos contidos em guias nas situa��es 'Faturado(a)' ou 'Pago(a)'."),
	NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__DATA_REALIZACAO("Caro usu�rio voc� tentou alterar a data de realiza��o do procedimento de c�digo {0}. N�o � permitido alterar a data de procedimentos contidos em guias nas situa��es 'Faturado(a)' ou 'Pago(a)'."),
	
	//Mensagens que t�m rela��o com o fluxo de auditar guia, especificamente atendimento de Gasoterapia
	GASOTERAPIA_GLOSADA_SEM_MOTIVO_DE_GLOSA("O motivo da Altera��o/Glosa para gasoterapia {0} deve ser preenchido."),
	TAXA_GLOSADA_SEM_MOTIVO_DE_GLOSA("O motivo da Altera��o/Glosa para taxa {0} deve ser preenchido."),
	
	//Cadastro de Item Especialidade
	NAO_MARCOU_MODALIDADE("N�o foi poss�vel cadastrar a especialidade pois nenhuma modalidade foi marcada. Marque pelo menos uma modalidade."),
	
	NENHUMA_MENSAGEM_ENCONTRADA("Nenhuma mensagem foi encontrada."),
	SELECIONAR_MENSAGEM_LER("Selecione apenas uma mensagem para ser lida."),
	MENSAGEM_NAO_SELECIONADA("Selecione pelo menos uma mensagem para ser lida."),
	INFORMAR_PELO_MENOS_UM_DOS_PARAMETROS("Informe pelo menos um dos par�metros."),
	
	PERIODICIDADE_TRATAMENTO_ODONTO("Novos procedimentos s� podem ser confirmados a partir do dia {0}."),
	PERIODICIDADE_TRATAMENTO_ODONTO_PARCIAL("S� podem ser realizados {0} procedimentos. Os demais s� podem ser confirmados a partir do dia {1}."),
	
	//Painel de controle
	
	INTERVALOS_IGUAIS("Intervalo inv�lido! In�cio e fim iguais!"),
	INTERVALOS_CONFLITANTES("Intervalo inserido \"{0}\" conflita com intervalo \"{1}\" j� cadastrado!"),
	HORA_INVALIDA("Hor�rio \"{0}\" inv�lido!"),
	;
	
	private String descricao;
	
	public String getMessage(String ... param) {
		String mensagem = this.descricao();
		for(int i = 0; i < param.length; i++) {
			if(mensagem.contains("{" + i + "}")) {
				mensagem = mensagem.replace("{" + i + "}", (String)param[i]);
			}
		}
		return mensagem;
	}
	
	MensagemErroEnum(String descricao) {
		this.descricao = descricao;
	}
	
	private String descricao() {
		return descricao;
	}
	
	
}
