 package br.com.infowaypi.ecarebc.enums;

/**
 * Enumeration para encapsular a biblioteca de mensagens de erro do sistema
 * @author Danilo Nogueira Portela
 */
public enum MensagemErroEnum{
	  
	//Mensagens de erro para Segurados
	SEGURADO_COM_SEXO_INVALIDO_PARA_ESPECIALIDADE("O sexo do beneficiário é inválido para a especialidade {0}."),
	SEGURADO_COM_SEXO_INVALIDO_PARA_PROCEDIMENTO("O sexo do beneficiário é inválido para o procedimento {0}"),
	SEGURADO_NAO_INFORMADO("O beneficiário deve ser informado!"),
	SEGURADO_INATIVO_NO_SISTEMA("O beneficiário não está ativo no sistema."),
	SEGURADO_COM_TITULAR_INATIVO_NO_SISTEMA("O titular referente ao beneficiário não está ativo no sistema."),
	SEGURADO_NAO_CUMPRIU_CARENCIA_PARA_O_PROCEDIMENTO("O beneficiário ainda não cumpriu a CARÊNCIA para o PROCEDIMENTO e só poderá realizá-lo a partir do DIA {1}."),
	SEGURADO_COM_IDADE_INFERIOR_A_MINIMA_PARA_O_PROCEDIMENTO("A IDADE do BENEFICIÁRIO é inferior à idade MÍNIMA permitida para se realizar o PROCEDIMENTO {0} ({1} anos)."),
	SEGURADO_COM_IDADE_SUPERIOR_A_MAXIMA_PARA_O_PROCEDIMENTO("A IDADE do BENEFICIÁRIO é superior à idade MÁXIMA permitida para se realizar o PROCEDIMENTO {0} ({1} anos)."),
	SEGURADO_COM_LIMITE_ESTOURADO_ODONTO("Esta CONSULTA já foi realizada para o BENEFICIÁRIO no DIA {0} só poderá ser realizada a partir do DIA {1}"),	
	SEGURADO_NAO_BENEFICIARIO("Este(a) segurado(a) não é beneficiário(a)."),
	SEGURADO_COM_LIMITE_ESTOURADO("O BENEFICIÁRIO já atingiu seu LIMITE {0}"),
	SEGURADO_COM_LIMITE_ESTOURADO_INFORMA_PROXIMA_DATA("O BENEFICIÁRIO já atingiu seu LIMITE {0}. Sua última consulta foi no dia {1} e só poderá ser realizada a partir do dia {2}."),
	SEGURADO_CRIACAO_CONSUMO("Ocorreu um erro na criação do CONSUMO para a COMPETÊNCIA {0}"),
	SEGURADO_SEM_LIMITE_PARA_CONSUMO_DE_EXAMES("Prezado(a) usuario(a), para essa guia só será permitido a inserção de {0} {1} devido ao limite do beneficiário."),
	SEGURADO_SEM_ADESAO("BENEFICIÁRIO sem data de adesão."),
	SEGURADO_COM_NUMERO_DE_CONSULTAS_PROMOCIONAIS_LIBERADAS_EXTRAPOLADAS("Prezado(a) usuario(a), o beneficiario informado já possui 2 (duas) consultas sem co-particpação liberadas. Não é permitida a liberação de uma terceira."),
	BENEFICIARIO_NAO_CUMPRIU_CARENCIA_PROCEDIMENTO("O BENEFICIÁRIO ainda não cumpriu CARÊNCIA para o procedimento {0}. Este só poderá ser realizado a partir de {1}."),
	BENEFICIARIO_NAO_CUMPRIU_CARENCIA_INTERNACAO("O BENEFICIÁRIO ainda não cumpriu CARÊNCIA para {0}. A carência termina dia {1}."),
	DEPENDENTE_COM_MENOS_DE_21_ANOS("O beneficiário ainda não completou 21 anos."),
	DEPENDENTE_COM_TITULAR_ATIVO("Prezado usuário, o beneficiário não está apto a ser Pensionista, pois o seu Titular está Ativo(a)."),
	DEPENDENTE_NAO_APTO_A_SER_PENSIONISTA("Prezado usuário, o beneficiário não está apto a ser Pensionista, pois o grau de parentesco deve ser apenas FILHO(A) OU CÔNJUGE."),
	DEPENDENTE_SEM_GRAU_DE_PARENTESCO("Prezado usuário, o beneficiário não possui nenhum grau de parentesco. Vá até o cadastro do beneficiário e atualize o campo GRAU DE PARENTESCO."),
	BENEFICIARIO_NAO_ENCONTRADO("Prezado usuário, não foi encontrado nenhum beneficiário DEPENDENTE com os parâmetros informados."),
	
	//Mensagens de erro para Prestador
	PRESTADOR_NAO_INFORMADO("O PRESTADOR deve ser informado."),
	PRESTADOR_INATIVO_NO_SISTEMA("O PRESTADOR não está ativo no sistema!"),
	PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE("O PRESTADOR não está habilitado para realizar {0}!"),
	PRESTADOR_COM_TETOS_FINANCEIROS_INVALIDOS("Os TETOS FINANCEIROS informados são inválidos."),
	PRESTADOR_COM_LOGIN_INVALIDO("O login informado já existe. Escolha outro nome para o login e tente novamente."),
	PRESTADOR_SEM_ATIVIDADE("O prestador deve fazer algum tipo de atendimento!"), 
	PRESTADOR_NAO_ATENDE_ESPECIALIDADE("O PRESTADOR informado não atende à ESPECIALIDADE {0}!"),
	PRESTADOR_SEM_ACORDO_ATIVO_PARA_O_PACOTE("Este prestador não possui acordo ativo para o pacote {0}."),
	ROLE_DO_USUARIO_DIFERENTE_DO_ROLE_DO_PRESTADOR("Não é permitido cadastrar usuários com role diferente do role do prestador ao qual ele estará vinculado."),
	TETO_PRESTADOR_MENSAL_EXAMES_ESTOUROU("Teto de realização mensal de exames estourado. Não é mais possível realizar exames neste prestador durante o mês corrente."),
	TETO_PRESTADOR_MENSAL_CONSULTAS_ESTOUROU("Teto de realização mensal de consultas estourado. Não é mais possível realizar consultas neste prestador durante o mês de {0}."),
	
	//Mensagens de erro para Profissional/Solicitante
	SOLICITANTE_INVALIDO("Solicitante inválido"),
	PROFISSIONAL_NAO_RESPONSAVEL("O profissional informado não foi responsável pela realização de nenhum dos procedimentos cirúrgicos desta guia, portanto, não é possível que o mesmo solicite procedimentos clínicos."),
	PROFISSIONAL_NAO_INFORMADO("O PROFISSIONAL deve ser informado."),
	SOLICITANTE_NAO_INFORMADO("O SOLICITANTE deve ser informado!"),
	PROFISSIONAL_SOLICITANTE_NAO_INFORMADO("O PROFISSIONAL solicitante deve ser informado!"),
	PROFISSIONAL_INATIVO_NO_SISTEMA("O PROFISSIONAL não está ativo no sistema."),
	SOLICITANTE_INATIVO_NO_SISTEMA("O SOLICITANTE não está ativo no sistema."),
	PROFISSIONAL_NAO_POSSUI_UMA_ESPECIALIDADE_DO_PRESTADOR("O PROFISSIONAL {0} não é cadastrado para nenhuma ESPECIALIDADE do prestador."),
	PROFISSIONAL_NAO_ATENDE_ESPECIALIDADE("O PROFISSIONAL informado não atende à ESPECIALIDADE {0}!"),
	PROFISSIONAL_NAO_HABILITADO_PARA_ESTA_OPERACAO("O Profissional {0} é perito e não pode realizar essa operação."),
	PROFISSIONAL_NAO_SOLICITOU_CONSULTA("O médico {0} não foi responsável por nenhuma consulta nos últimos 60 dias e, portanto, não pode solicitar exames para o segurado {1}."),
	
	//Mensagens de erro para Especialidade 
	ESPECIALIDADE_NAO_INFORMADA("A ESPECIALIDADE deve ser informada."),
	ESPECIALIDADE_NAO_CADASTRADA_PARA_O_PROFISSIONAL("O PROFISSIONAL {0} não atende a especialidade {1}."),
	ESPECIALIDADE_NAO_CADASTRADA_PARA_O_PRESTADOR("Este PRESTADOR não atende a especialidade {0}."),
	ESPECIALIDADE_CONSULTA_INVALIDA("A ESPECIALIDADE da consulta eletiva não pode ser {0}!"),
	
	//Mensagens de erro para Datas
	NAO_E_POSSIVEL_GERAR_REMESSA_PARA_COMPETENCIA_ANTERIOR_A_ULTIMA_INFORMADA("Não é possivel gerar remessa para uma competência anterior à última processada."),
	NAO_E_POSSIVEL_GERAR_REMESSA_PARA_COMPETENCIA_POSTERIOR_A_ULTIMA_INFORMADA("Não é possivel gerar remessa para uma competência posterior à última processada."),
	NAO_E_POSSIVEL_GERAR_REMESSA_PARA_A_COMPETENCIA_INFORMADA("Não é possível gerar remessa para a competência informada"),
	DATA_INICIAL_INVALIDA("DATA DE ABERTURA inválida."),
	DATA_CIRURGIA_NAO_PODE_SER_ALTERADA("A DATA DE REALIZAÇÃO do procedimento {0} não pode ser alterada."),
	DATA_FINAL_INVALIDA("DATA DE FECHAMENTO inválida."),
	DATA_CIRURGIA_INFORMADA_PARA_PROCEDIMENTO_QUE_NAO_IRA_GERAR_HONORARIO("Não é permitido informar a DATA DE REALIZAÇÃO para o procedimento {0} pois o mesmo não foi selecionado para gerar honorário"),
	DATA_ABERTURA_REQUERIDA("DATA DE ABERTURA deve ser informada."),
	DATA_FECHAMENTO_REQUERIDA("DATA DE FECHAMENTO deve ser informada."),
	DATA_CIRURGIA_SUPERIOR_A_DATA_ATUAL("A DATA DE Realização do procedimento {0} não pode ser superior a data atual."),
	DATA_CIRURGIA_INFERIOR_A_DATA_ATUAL("A DATA DE Realização do procedimento não pode ser inferior a data atual."),
	DATA_CIRURGIA_INFERIOR_A_DATA_DE_ATENDIMENTO("A DATA DE REALIZAÇÃO do procedimento {0} não pode ser inferior a data de atendimento."),
	DATA_FECHAMENTO_SUPERIOR_A_DATA_ATUAL("DATA DE FECHAMENTO não pode ser superior a data atual."),
	DATA_FECHAMENTO_INFERIOR_A_DATA_ATENDIMENTO("DATA DE FECHAMENTO não pode ser inferior a data de atendimento."),
	DATA_ATENDIMENTO_SUPERIOR_A_DATA_ATUAL("DATA DE ATENDIMENTO não pode ser superior a data atual."),
	PROFISSIONAL_SOLICITANDO_VISITA_COM_MENOS_DE_DEZ_DIAS("O profissional informado realizou o procedimento {0} há menos de 10 dias, portanto, só será possível solicitar procedimentos clínicos a partir do dia {1}."),
	DATA_PROCEDIMENTO_MAIOR_QUE_DATA_AUTORIZACAO("DATA DE REALIZAÇÃO do procedimento não pode ser inferior a data atual."),
	DATA_ATENDIMENTO_INFERIOR_A_DATA_MARCACAO("DATA DE ATENDIMENTO não pode ser inferior a data de autorização da guia."),
	DATA_CIRURGIA_SUPERIOR_A_DATA_FECHAMENTO("DATA DA CIRURGIA não pode ser superior a DATA DE FECHAMENTO da guia."),
	DATA_CIRURGIA_SUPERIOR_A_DATA_ALTA("A data de realização do procedimento {0} não pode ser superior a DATA DE ALTA da guia."),
	DATA_CIRURGIA_INFERIOR_A_DATA_ATENDIMENTO("DATA DA CIRURGIA não pode ser inferior a data de atendimento."),
	DATA_ATENDIMENTO_INVALIDA("DATA de ATENDIMENTO inválida."),
	DATA_ATENDIMENTO_NAO_INFORMADA("DATA de ATENDIMENTO não informada."),
	DATA_DE_ATENDIMENTO_COM_PRAZO_ULTRAPASSADO("A DATA de ATENDIMENTO ultrapassou o PRAZO para realização da guia."),
	DATA_DE_AUTORIZACAO_COM_PRAZO_ULTRAPASSADO("Caro usuário, essa guia foi autorizada há {0}" +
	" dias. Uma guia de internação pode ser confirmada em no máximo {1} dias após a data de autorização. "),
	DATA_DE_ATENDIMENTO_INFERIOR_A_MARCACAO("A DATA de ATENDIMENTO não pode ser menor que a DATA de MARCAÇÃO da guia!"),
	PRAZO_EXCEDIDO_PARA_INCLUSAO_DE_PROCEDIMENTOS("O PRAZO para solicitação de PROCEDIMENTOS foi excedido."),
	DATA_DE_ADESAO_INVALIDA("A data de adesão não deve ser superior a data de hoje."),
	DATA_INICIAL_MAIOR_Q_A_FINAL("A DATA INICIAL não pode ser maior que a DATA FINAL."),
	DATA_CIRURGIA_REQUERIDA("A DATA DE REALIZAÇÃO deve ser informada para o procedimento {0}."),
	DATA_REALIZACAO_REQUERIDA("A DATA DE REALIZAÇÃO deve ser informada."),
	GUIA_NAO_PODE_SER_FECHADA_NO_DIA_QUE_FOI_GERADA("Uma guia parcial não pode ser fechada no mesmo dia em que foi aberta!"),
	DATA_PROCEDIMENTO_DEVE_ESTAR_ENTRE_INICIO_E_TERMINO_DO_ATENDIMENTO("DATA DE REALIZAÇÃO do procedimento {0} deve estar entre a data de atendimento({1}) e a data de termino do atendimento({2})."),
	NECESSARIO_INFORMAR_SE_FOI_REALIZADO_NA_PARCIAL("É necessário informar se o procedimento {0} foi realizado nesta parcial."),
	NECESSARIO_INFORMAR_PACOTE_FOI_REALIZADO_NA_PARCIAL("É necessário informar se o pacote {0} foi realizado nesta parcial."),
	DATA_CIRURGIA_INCOMPATIVEL_COM_A_PARCIAL_INFORMADA("Data de realização do procedimento {0} é incompatível com a parcial informada."),
	DATA_DE_TERMINO_ATENDIMENTO_INFERIOR_A_DATA_ATENDIMENTO("A DATA de TÉRMINO DE ATENDIMENTO não pode ser menor que a DATA de ATENDIMENTO da guia!"),
	DATA_DE_RECEBIMENTO_INFERIOR_A_DATA_DE_TERMINO_ATENDIMENTO("A DATA de RECEBIMENTO não pode ser menor que a DATA de TÉRMINO DE ATENDIMENTO da guia!"),
	DATA_TERMINO_ATENDIMENTO_NAO_PREENCHIDA_PARA_MUDANCA_SITUACAO("A data de término de atendimento deve ser preenchida para a mudança de situação"),
	DATA_RECEBIMENTO_NAO_PREENCHIDA_PARA_MUDANCA_SITUACAO("A data de recebimento deve ser preenchida para a mudança de situação"),
	
	//Mensagens de erro para Guia
	GUIA_SEM_PROCEDIMENTOS("Nenhum PROCEDIMENTO foi informado!"),
	GUIA_SEM_PROCEDIMENTOS_PARA_CANCELAR("A guia informada não possui procedimentos para serem cancelados."),
	NENHUMA_GUIA_ENCONTRADA("Nenhuma GUIA foi encontrada!"),
	NENHUMA_GUIA_SELECIONADA("Nenhuma GUIA foi selecionada!"),
	GUIA_SEGURADO_DIFERENTE("O BENEFICIÁRIO deve ser o mesmo da guia de atendimento de urgência."),
	GUIA_PRESTADOR_DIFERENTE("A guia de urgência deve ser do mesmo prestador."),
	GUIA_NAO_PERTENCE_AO_TIPO("O número da autorização não se refere a uma guia de {0}."),
	GUIA_SEM_PROCEDIMENTOS_PARA_EXCLUSAO("A guia informada não possui procedimentos para serem excluídos."),
	GUIA_NAO_PODE_SOLICITAR_PROCEDIMENTOS("Não é possivel solicitar PROCEDIMENTOS para a SITUAÇÃO atual da GUIA."),
	GUIA_COM_PROCEDIMENTOS_DEMAIS("A QUANTIDADE de PROCEDIMENTOS da guia é SUPERIOR à permitida."),
	GUIA_EXAME_COM_PROCEDIMENTOS_ESPECIAIS_DEMAIS("A guia pode conter apenas 1(UM) PROCEDIMENTO ESPECIAL!"),
	GUIA_EXAME_COM_PROCEDIMENTO_ESPECIAL_E_OUTROS("Uma guia de EXAMES com procedimento ESPECIAL não pode conter OUTROS PROCEDIMENTOS!"),
	GUIA_URGENCIA_SEM_CIDS("Nenhum CID foi informado!"),
	GUIA_URGENCIA_SEM_QUADRO_CLINICO("Nenhum QUADRO CLÍNICO foi informado!"),
	GUIA_NAO_ENCONTRADA("A guia com número de autorização {0} não foi encontrada."),
	GUIA_NAO_PODE_SER_CANCELADA("A guia de autorização {0} está {1} e não pode ser CANCELADA."),
	GUIA_NAO_PODE_SER_CONFIRMADA("A guia de autorização {0} não pode ser confirmada por um prestador ANESTESISTA."),
	GUIA_NAO_CRIADA("A guia de {0} não foi criada!"),
	GUIA_INVALIDA("Guia de {0} invalida!"),
	GUIAS_NAO_ENCONTRADAS_PARA_AUDITORIA("Nenhuma GUIA para AUDITORIA foi encontrada."),
	GUIAS_NAO_ENCONTRADAS_PARA_AUTORIZACAO("Nenhuma GUIA para AUTORIZAÇÃO foi encontrada."),
	GUIAS_NAO_ENCONTRADAS_PARA_REALIMENTACAO("Não existem GUIAS desse BENEFICIÁRIO para serem REALIMENTADAS!"),
	GUIAS_NAO_ENCONTRADAS_PARA_O_SEGURADO("Não existem GUIAS desse BENEFICIÁRIO para serem {0}!"),
	GUIA_JA_ORIGINOU_INTERNACAO("Esta Guia já originou uma INTERNAÇÃO antes."),
	IMPOSSIVEL_ADICIONAR_EXAMES_NESTA_GUIA("Prezado usuário(a), não é permitido adicionar EXAMES em uma guia {0}."),
	IMPOSSIVEL_EXCLUIR_EXAMES_NESTA_GUIA("Prezado usuário(a), não é permitido excluir EXAMES em uma guia {0}."),
	IMPOSSIVEL_FECHAR_GUIA_PARA_ANESTESISTA("Prezado usuário(a), não é permitido FECHAR uma guia {0}."),
	EXAMES_EXTERNOS_SOMENTE_PARA_GUIA_EXTERNAS("Exames externos só podem ser realizados para guias de outro prestador."),
	PACOTE_OU_ACOMODACAO_DEVE_SER_INFORMADO("Para esta internação caso não seja informado PACOTE deve ser informado pelo menos ACOMODAÇÃO."),
	GUIA_NAO_E_DE_INTERNACAO("A autorização informada não é de uma guia de internação."),
	GUIA_SEM_PROCEDIMENTOS_PARA_FLUXO("Esta guia não possui procedimentos {0} para serem {1}."),
	GUIA_NAO_CONFIRMADA_PARA_FECHAMENTO("Esta guia não está ABERTA. Somente guias ABERTA podem ser FECHADAS."),
	GUIA_PROCEDIMENTOS_NIVEIS_MISTURADOS("Caro usuário, uma guia de solicitação de exames não pode conter procedimentos de Nivel 1 junto a procedimentos de outros Níveis."),
	GUIA_NAO_PODE_SER_FECHADA("Caro usuário, a guia {0} não pode ser fechada, pois a diferença entre sua data de atendimento e sua data de fechamento supera o limite de {1} dias."),
	GUIA_SOLICITACAO_PENDENTE("Caro usuário, não é possível solicitar prorrogação para essa guia. Essa guia possui uma solicitação de prorrogação pendente."),
	GUIA_NAO_PODE_SER_FECHADA_PARCIALMENTE("Caro usuário, não é possível fechar parcialmente esta guia."),
	NAO_E_POSSIVEL_ALTERAR_DATA_FECHAMENTO_DA_GUIA("Não é possível alterar a data de término de atendimento de uma guia parcial."),
	GUIA_DEVE_SER_GLOSADA_TOTALMENTE("Todos os itens da guias foram marcados para ser glosados. Por favor, glose a guia."),
	GUIA_ACOMPANHAMENTO_ANESTESICO_JA_EXISTE("Já existe guia de acompanhamento anestesico gerado para esta guia."),
	DATA_ATENDIMENTO_NAO_PREENCHIDA_PARA_MUDANCA_SITUACAO("A data de atendimento deve ser preenchida para a mudança de situação"),
	ERRO_EXCLUSAO_TODOS_PROCEDIMENTOS_GUIA_EXAME("Você tentou remover todos os procedimentos desta guia. Neste caso você deve cancelar a guia de exame"),
	ERRO_NENHUM_PROCEDIMENTO_SELECIONADO_PARA_EXCLUSAO_GUIA_EXAME("Você não selecionou procedimentos. É necessário selecionar pelo menos um procedimento para a remoção."),

	
	//Mensagens de erro para Consulta/Atendimento
	CONSULTA_NAO_CUMPRIU_PERIODICIDADE("O BENEFICIÁRIO já possui uma consulta de {0} no DIA {1} e só poderá realizar outra para esta mesma especialidade a partir do DIA {2}."),
	CONSULTA_PROMOCIONAL_INFORMAR_TODOS_OS_PARAMETROS("Para liberar uma consulta sem co-partição é necessário informar também o parmêtro {0}"),
	CONSULTA_URGENCIA_NAO_CUMPRIU_PERIODICIDADE("O(a) beneficiário(a) {0} já realizou consulta de urgência DIA {1} e só poderá realizar outra novamente a partir do DIA {2}, portanto está apto(a) apenas a realizar atendimentos subsequentes."),
	ATENDIMENTO_URGENCIA_SEM_CONSULTA_ORIGEM("Prezado usuário(a), para realizar um atendimento subsequente é necessário registrar, previamente, uma consulta de urgência."),
	GUIA_SEM_CONSULTA_ORIGEM("Nenhuma consulta foi realizada por este solicitante para este BENEFICIÁRIO nos últimos {0} dias."),
	GUIA_ORIGEM_ULTRAPASSOU_30_DIAS("Sua consulta já ultrapassou o prazo de 30 dias para realização dos exames."),
	SOLICITACAO_APOS_PRAZO_ATENDIMENTO("Não é permitido solicitar procedimentos para Consulta de Urgencias {0} horas após seu atendimento. Guia: {1}, Atendimento: {2}"),
	BENEFICIARIO_JA_REALIZOU_CONSULTA_COM_PROFISSIONAL_ODONTO("O(a) segurado(a) {0} já realizou consulta com o(a) profissional {1} nos últimos 30 dias"),
	BENEFICIARIO_NAO_POSSUI_CONSULTA_ODONTO_ANTERIOR("O(a) segurado(a) {0} não possui consulta odontológica com especialidade {1} nos últimos {2} dias"),

	//Mensagens de erro para Procedimento
	PROCEDIMENTO_NAO_INFORMADO("Nenhum PROCEDIMENTO foi informado."),
	PROCEDIMENTO_NAO_SELECIONADO("Nenhum PROCEDIMENTO foi selecionado."),
	PROCEDIMENTO_ESPECIAL_NAO_SOLICITADO("O PROCEDIMENTO {0} necessita de autorização."),
	PROCEDIMENTO_INATIVO_NO_SISTEMA("O PROCEDIMENTO {0} não está ativo no sistema."),
	PROCEDIMENTO_NAO_PODE_SER_BILATERAL("O PROCEDIMENTO {0} não pode ser BILATERAL."),
	PROCEDIMENTO_COM_ELEMENTO_JA_APLICADO_UNICIDADE("O TRATAMENTO {0} não pode ser realizado para o ELEMENTO {}, pois já existe outro tratamento com UNICIDADE para o mesmo."),
	PROCEDIMENTO_UNICIDADE_JA_APLICADO_PARA_BENEFICIARIO("O PROCEDIMENTO {0} já foi realizado para o BENEFICIÁRIO e não pode ser realizado novamente."),
	PROCEDIMENTO_NAO_CUMPRIU_PERIODICIDADE("O PROCEDIMENTO {0} já foi solicitado, agendado ou realizado na GUIA {3} para o BENEFICIÁRIO no DIA {1} e só poderá ser solicitado, agendado ou realizado a partir do DIA {2} ."),
	PROCEDIMENTO_INVALIDO_PARA_CONSULTA("O PROCEDIMENTO de código {0} é inválido para uma guia de consulta."),
	PROCEDIMENTO_PADRAO_NAO_PODE_SER_EXCLUIDO("Não é possivel excluir o PROCEDIMENTO {0}"),
	EXCLUSAO_PROCEDIMENTO_FATURADO("Não é possivel excluir o PROCEDIMENTO {0} porque ele encontra-se FATURADO(a)"),
	PROCEDIMENTO_SOLICITADO_OUTRO_MODULO("O PROCEDIMENTO {0} só pode ser solicitado no módulo {1}."),
	PROCEDIMENTO_VISIBILIDADE_INVALIDA("O PROCEDIMENTO {0} deve conter uma VISIBILIDADE!"),
	PROCEDIMENTO_QUANTIDADE_INVALIDA("A QUANTIDADE máxima permitida para realizar esse exame é {0}"),
	PROCEDIMENTO_CIRURGICO_PODE_SER_INSERIDO_SOMENTE_PARA_INTERNACAO("Procedimentos cirúrgicos podem ser inseridos somente em guias de internação"),
	ERRO_EXAME_ESPECIAL_EM_GUIA_EXTERNA("O EXAME de código {0} é especial e necessita de autorização."),
	PROCEDIMENTO_ODONTO_SEM_ESTRUTURAS("Nenhuma ESTRUTURA odontológica foi informada para o procedimento: ({0})!"),
	PROCEDIMENTO_QUANTIDADE_ESTRUTURAS_INVALIDA("O procedimento ({0}) só pode ser aplicado em até {1} {2}(s)!"),
	PROCEDIMENTO_COM_ESTRUTURAS_INVALIDAS("O procedimento ({0}) só pode ser aplicado em um mesmo {1}!"),
	PROCEDIMENTO_JA_REALIZOU_PERICIA_INCIAL("O TRATAMENTO {0} já passou por perícia inicial!"),
	PROCEDIMENTO_EM_PERICIA_INICIAL_NAO_AUTORIZADO("O TRATAMENTO {0} deve passar pelo processo de autorização na PERÍCIA INCIAL!"),
	PROCEDIMENTO_EM_PERICIA_FINAL_NAO_AUTORIZADO("O TRATAMENTO {0} deve ser AUTORIZADO para ser marcado como PERÍCIA FINAL!"),
	PROCEDIMENTO_SOLICITADO_NAO_AUTORIZADO("Todos os TRATAMENTOS devem passar pelo processo de AUTORIZAÇÃO antes de finalizá-lo!"),
	PROCEDIMENTO_VISITA_HOSPITALAR("Prezado(a) usuario(a), o procedimento {0} só pode ser vinculado a um profissional {1} credenciado."),
	PROCEDIMENTO_GLOSADO_SEM_MOTIVO_DE_GLOSA("O motivo da Alteração/Glosa para o procedimento {0} deve ser preenchido."),
	ITEM_GLOSADO_SEM_MOTIVO_DE_GLOSA("O motivo da Alteração/Glosa para o item {0} deve ser preenchido."),
	PROCEDIMENTO_GLOSA_DESFEITA_SEM_MOTIVO_DE_GLOSA("O motivo para desfazer a glosa do procedimento {0} deve ser preenchido."),
	PROCEDIMENTO_CANCELADO_SEM_MOTIVO("O motivo do cancelamento do procedimento {0} deve ser informado."),
	PROCEDIMENTO_NAO_MARCADO_PARA_SER_GLOSADO("O Procedimento {0} não foi marcado para ser glosado. Só será possível substituí-lo se a opção \"Alterar/Glosar\" estiver marcada"),
	MAIS_DE_UM_PROCEDIMENTO_A_100_PORCENTO_NA_MESMA_DATA("Caro usuário, não é permitido autorização de mais de um procedimento cirurgico 100% na mesma data ({0})."),
	NENHUM_PROCEDIMENTO_A_100_PORCENTO_NA_GUIA("Caro usuário, é necessário que ao menos um procedimento cirurgico seja de 100%"),
	NENHUM_PACOTE_A_100_PORCENTO_NA_GUIA("Caro usuário, é necessário que ao menos um pacote seja de 100%"),
	ERRO_NO_PROCEDIMENTO_AO_FECHAR_GUIA("Você tentou fechar uma guia com procedimento de Nível 2 ainda em situação Solicitado(a). Não é possível fechar uma guia com procedimento ainda pendente de regulação. Aguarde o posicionamento do regulador do Saúde Recife."),
	ERRO_NO_PROCEDIMENTO_AO_REGISTRAR_ALTA("Você tentou registrar Alta/Evolução para uma guia com procedimento de Nível 2 ainda em situação Solicitado(a). Não é possível registrar Alta/Evolução para uma guia com procedimento ainda pendente de regulação. Aguarde o posicionamento do regulador do Saúde Recife."),
	
	//HONORARIOS
	PROCEDIMENTO_NAO_PODE_GERAR_HONORARIO_DATA_DE_REALIZACAO("Não é possível gerar honorário para o procedimento {0} pois o mesmo já foi realizado há mais de {1} dias"),
	HONORARIO_DUPLICADO ("Já foi gerado um honorário de {0} com o mesmo procedimento {1}"),
	HONORARIO_DUPLICADO_PACOTE ("Já foi gerado um honorário para o pacote {0}."),
	NAO_GERAR_HONORARIO_PARA_PORTE_ANESTESICO_ZERO ("Procedimentos com porte anestésico 0 (ZERO) não geram honorário individual."),
	GERAR_HONORARIO_ANESTESISTA_AUXILIAR_APENAS_PARA_PORTE_ANESTESICO_SETE_OU_OITO ("Apenas procedimentos de porte anestésico 7 (SETE) e 8 (OITO) geram honorário para AUXILIAR ANESTESISTA."),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_RESPONSAVEL_DIFERENTE ("Não se pode gerar guia de honorário individual pra um cirurgião diferente do informado no procedimento."),
	PROCEDIMENTO_COM_HONORARIO_NAO_PODE_SER_GLOSADO ("O procedimento {0} possui honorários e não pode ser glosado.\n As seguintes guias devem ser glosadas ou canceladas: {1}"),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_RESPONSAVEL_DIFERENTE_PROFISSIONAL_DO_PRESTADOR_MEDICO("Não se pode gerar guia de honorário individual pra um CIRURGIÃO que não seja {0}."),
	PRESTADOR_PESSOA_FISICA_NAO_ENCONTRADO_PARA_O_PROFISSIONAL_INFORMADO("Caro usuário, você não possui vínculo com o profissional informado. Por favor certifique-se de que o cadastro de ambos está atualizado."),
	PROCEDIMENTO_COM_HONORARIO_NAO_PODE_SER_EXCLUIDO ("Este procedimento possui ou já possuiu honorários e não pode ser excluído, apenas glosado."),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_PRESTADOR_PESSOA_FISICA_DIFERENTE_DO_INFORMADO_NO_PROCEDIMENTO("Não se pode gerar guia de honorário individual para um cirurgião diferente do informado no procedimento {0}."),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_PRESTADOR_PESSOA_FISICA_DIFERENTE("O profissional {0} não realizou nenhum procedimento na guia {1} e portanto não pode gerar honorários para a mesma."),
	GUIA_SEM_PROCEDIMENTOS_COM_ACOMPANHAMENTO_ANESTESICO("Esta guia não possui procedimentos com porte anestésico para gerar honorários."),
	NAO_GERAR_HONORARIO_INDIVIDUAL_PARA_RESPONSAVEL_PRA_PROCEDIMENTO_GLOSADO("Não é permitido inserir honorário para grau de cirurgião para um procimento em situação Glosado(a)"),
	
	//Mensagens de erro para Elemento
	ELEMENTO_NAO_INFORMADO("O(a) {0} deve ser informado para o TRATAMENTO {1}!"),
	ESTRUTURA_NAO_INFORMADA("O(a) {0} deve ser informado para o TRATAMENTO {1}!"),
	PROCEDIMENTO_APLICADO_A_ELEMENTO_UNICIDADE("O TRATAMENTO {0} está sendo aplicado a um ELEMENTO inválido."),
	ELEMENTO_JA_APLICADO_UNICIDADE_NA_GUIA("Um ELEMENTO que recebeu o TRATAMENTO ({0}) não pode receber outros tratamentos!"),
	
	//Mensagens de erro para Pessoa Física
	NOME_INVALIDO("O NOME informado é inválido."),
	PESSOA_FISICA_COM_CPF_INVALIDO("O CPF informado não está correto."),
	
	//Mensagens de erro para Profissional
	CAMPO_CPF_JA_CADASTRADO("Este CPF já foi cadastrado para outro Profissional"),
	CAMPO_CRM_JA_CADASTRADO("Este número de conselho já foi cadastrado para outro Profissional"),
	PROFISSIONAl_NAO_ASSOCIADO_AO_PRESTADOR("O profissional {0} não faz parte do corpo clínico do prestador {1}."),
	PROFISSIONAl_NAO_CREDENCIADO("O profissional {0} não é credenciado."),
	
	//Mensagens de erro para Pessoa Jurídica
	NOME_FANTASIA_INVALIDO("O NOME FANTASIA informado é inválido."),
	PESSOA_JURIDICA_COM_CNPJ_INVALIDO("O CNPJ informado não está correto."),
	
	//Mensagens de erro para urgência
	DIARIA_NAO_INFORMADA("Informe uma Acomodação com a quantidade de Horas."),
	URGENCIA_DIARIA_24H("Guias de urgência só podem conter acomodações de até 1 dia!"),
	URGENCIA_DIARIAS_DEMAIS("Apenas 1 (uma) Acomodação/Diária deve ser informada!"),
	URGENCIA_APENAS_PACOTES_OU_PROCEDIMENTOS_COM_DIARIA("Guias de urgência devem conter apenas PACOTES ou PROCEDIMENTOS Cirúrgicos COM DIÁRIA."),
	INTERNACAO_APENAS_PACOTES_OU_ACOMODACAO("Para guias de internação deve ser informado PACOTES ou ACOMODAÇÕES."),
	INTERNACAO_CIRURGICA_DEVE_CONTER_PROCEDIMENTOS("A guia de internação deve conter pelo menos 1 (um) PROCEDIMENTO."),
	INTERNACAO_VAZIA_NAO_PERMITIDA("Prezado(a) usuario(a), não é permitida a criação de guias vazias."),
	URGENCIA_COM_PROCEDIMENTOS_SEM_DIARIA("Guias de Urgência devem conter PROCEDIMENTO(S) Cirúrgico(s) COM DIÁRIA."),
	QUADRO_CLINICO_NAO_INFORMADO("O quadro clínico deve ser preenchido!"),
	SEGURADO_SUSPESO_NAO_PODE_SOLICITAR_EXAME_COMPLEXO("Prezado(a) usuário(a), não é possível solicitar exames complexos para um segurado suspenso."),
	
	//Outras mensagens de erro
	VALIDACAO_MINUTOS("O Campo Hora/Minuto foi preenchido com uma valor inválido para minutos"),
	PROFISSIONAL_PELO_MENOS_UMA_OPCAO("Preencha pelo menos uma das opções referentes ao médico escolhido"),
	CAMPO_NAO_PODE_SER_ALTERADO("O campo {0} não pode ser alterado."),
	NAO_FOI_POSSIVEL_GERAR_AUTENTICACAO("Não foi possível gerar número de autenticação."),
	CODIGO_INVALIDO("O CÓDIGO informado é inválido."),
	NUMERO_AUTORIZACAO_INVALIDO("Número de AUTORIZAÇÃO inválido!"),
	DESCRICAO_INVALIDA("A DESCRIÇÃO informada é inválida."),
	SEXO_INVALIDO("O SEXO informado é inválido."),
	DOCUMENTO_INVALIDO("O DOCUMENTO informado é inválido."),
	VALOR_MATERIAIS_INVALIDO("O campo Valor Materiais está preenchido com valor inválido."),
	VALOR_MEDICAMENTOS_INVALIDO("O campo Valor Medicamentos está preenchido com valor inválido."),
	SEGURADO_INTERNADO_OU_COM_SOLICITACAO("Este BENEFICIÁRIO já está internado ou possui uma solicitação de internação."),
	AUSENCIA_DE_PARAMETROS("Pelo menos um parâmetro deve ser informado."),
	LIMITE_DIAS_ULTRAPASSADO("Prezado usuário, a diferença entre a data final e a data inicial não pode ser superior a 31 dias."),
	LIMITE_DIAS_ULTRAPASSADO_UNIPLAM("Prezado usuário, a diferença entre a data final e a data inicial não pode ser superior a 90 dias."),
	LIMITE_IDADE_NAO_PERMITIDO_ESPECIALIDADE("Prezado usuário, essa especialidade não é válida para segurados com idade fora do intervalo de {1} à {2} anos. Idade do segurado {0} anos."),
	LIMITE_DIAS_ULTRAPASSADO_RECURSO_GLOSA("Prezado usuário, a diferença entre a data final e a data inicial não pode ser superior a 60 dias."),
	CAMPO_MOTIVO_DE_GLOSA_REQUERIDO("O campo \"Motivo da glosa\" deve ser preenchido ao se escolher \"Glosar guia\"."),
	CAMPOS_DE_PREENCHIMENTO_OBRIGATORIO("Caro usuário, os campos \"Operador Lógico\" e \"Permanencia\" devem ser preenchidos juntos."),
	CAMPOS_DE_PREENCHIMENTO_DESNECESSARIOS("Caro usuário, os campos \"Operador Lógico\" e \"Permanencia\" só devem ser preenchidos caso a consulta seja sobre guias do tipo INTERNAÇÃO."),
	VALOR_ACORDO_INVALIDO("O valor do acordo não pode ser igual a zero."),
	MOTIVO_AUTORIZACAO_ACOMODACAO_REQUERIDO("Caro usuário, por favor informe o motivo da (não)autorização da acomodação {0}."),
	
	//Mensagens de erro para buscar segurado
	SEGURADO_NAO_ENCONTRADO("Não foi encontrado BENEFICIÁRIO com o número de CARTÃO {0} e CPF {1}."),
	CPF_SEGURADO_NAO_ENCONTRADO("Não foi encontrado BENEFICIÁRIO com o número de CPF {0}"),
	CARTAO_SEGURADO_NAO_ENCONTRADO("Não foi encontrado BENEFICIÁRIO com o número de CARTÃO {0}"),
	PROPOSTA_NAO_INFORMADA("O Número de CARTÃO deve ser informado."),
	CARTÃO_INVALIDO("Número de CARTÃO inválido!"),
	EMPRESA_NAO_INFORMADA("O nome da EMPRESA deve ser informado."),
	SEGMENTACAO_INCOMPATIVEL("A cobertura do segurado não permite esse tipo de Atendimento."),

	ERRO_AO_GERAR_CARTAO("Erro ao gerar número de cartão."),
	ERRO_CID_DE_URGENCIA_NAO_ENCONTRADA("As cids informadas não justificam a criação de uma guia de urgência."),
	
	NAO_EXISTEM_GUIAS_A_FATURAR("Não existem guias para ser faturadas."),
	FATURAMENTOS_NAO_GERADOS("Não foi encontrado nenhum faturamento para essa compentência!"),
	FATURAMENTO_NAO_GERADO_PARA_O_PRESTADOR("Não foi encontrado nenhum faturamento desse prestador para essa compentência!"),
	FATURAMENTO_DUPLICADO("Caro usuário, foi detectada a geração de faturamento em duplicidade para um prestador. Contacte a equipe de suporte para a resolução do problema."),
	
	//Mensagens de erro para buscar guias
	SOLICITACAO_PRORROGACAO_NAO_PERMITIDA("A prorrogação desta guia só poderá ser solicitada a partir de {0}"),
	MOTIVO_CANCELAMENTO_NAO_INFORMADO("O MOTIVO do CANCELAMENTO deve ser preenchido.") ,
	NAO_E_POSSIVEL_CANCELAR_TODOS_OS_PROCEDIMENTOS("Não é possível cancelar todos os procedimentos da guia. Nesse caso é necessário cancelar a guia."),
	
	//Mensagens de erro para consumos
	ERRO_CRIACAO_CONSUMO("Ocorreu um erro na criação do consumo para a competência {0}!"),
	
	//Mensagens de erro para índices
	MEDIA_PROCEDIMENTOS_POR_CONSULTA_ANORMAL("A média de exames diverge da média adotada pelo Iapep Saúde. " +
			"Por este motivo a marcação do(s) exame(s) deverá ser realizada em outro Prestador"),
			
	//Mesagens de erros para índices
	INDICE_NULO("Todos os índices devem ser informados!"),
	INDICE_NAO_ENCONTRADO("Nenhum ÍNDICE relacionado a {0} foi encontrado para A COMPETÊNCIA {1}!"),
	
	// Mensagem de erro para LoteDeGuias
	SITUACAO_GUIAS_LOTE("Somente guias fechadas ou devolvidas são aceitas em um lote."),
	PRAZO_DE_ENTREGA("Lote com prazo de entrega ultrapassado. Prazo de entrega: {0} a {1}."),
	DIAS_UTEIS("A entrega de lotes é permitida somente em dias úteis."),
	EXCESSO_DE_GUIAS("O lote possui mais de {0} guias."),
	MINIMO_DE_GUIAS("O lote deve possuir pelo menos uma guia."),
	TIPO_DE_GUIAS("As guias do lote devem ser do mesmo tipo."),
	GUIAS_PRESTADOR("As guias devem pertencer ao mesmo prestador."),
	GUIA_ULTRAPASSADA("A guia {0} ultrapassou o prazo de entrega. Essa guia deveria ser entregue até o dia {1}."),
	TIPO_DE_GUIA_NAO_INFORMADO("O tipo de guia deve ser informado."),
	TIPO_DE_GUIA_INVALIDO("A guia deve ser de urgência, de internação ou de exame."),
	DATAS_INCOMPATIVEIS("O lote não pode ser entregue antes de ter sido criado."), 
	LOTE_NAO_ENCONTRADO("Nenhum lote encontrado com o identificador: {0}."), 
	GUIA_NAO_POSSUI_LOTE("A guia {0} não pertence a nenhum lote."), 
	LOTE_VAZIO("O lote não possui guias."),
	MOTIVO_DEVOLUCAO_JA_CADASTRADO("Motivo de devolução já cadastrado."),
	
	GUIA_INAPTA_PARA_ALTERACAO_PROCEDIMENTOS("Não é permitido alterar guias de {0} com a situação {1}."),
	
	//TERAPIA SERIADA
	LIMITE_TRATAMENTO_SERIADO_ESTOURADO("Caro usuário, o segurado já atingiu o limite de solicitações para este procedimento"),
	LIMITE_TIPO_PERIODO_ULTRAPASSADO("Caro usuário, o beneficiário atingiu o limite de sessões permitidas para o tratamento de {0}"),
	
	//Mensagem de erro para nova hierarquia de guia de exame
	GUIA_EXAME_NAO_POSSUI_DIARIA("Guia de exame não possui diária."),
	
	
	//Fechamento de guia
	FECHAMENTO_EXIGE_ALTA("Caro usuário, para realizar o fechamento total desta guia é necessário registrar a alta hospitalar."),
	DESCRICAO_VALOR_OUTROS_DEVE_SER_INFORMADA("Quando o campo OUTROS VALORES for informado o campo DESCRIÇÃO OUTROS também deve ser informado."), 
	GUIA_ORIGEM_NAO_POSSUI_DIARIA("A guia de origem não possui a diária solicitada: {0}."), 
	GUIA_ORIGEM_NAO_POSSUI_DIARIAS("A guia de origem não possui nenhuma diária."),
	FECHAMENTO_PARCIAL_APOS_PRAZO("A data final ara uma guia parcial deve ser na máximo após {0} dias."),
	FECHAMENTO_DE_GUIAS_COM_PROCEDIMENTOS_SOLICITADOS("A guia não pode ser fechado pois os seguintes procedimentos estão na situação \"Solicitado(a)\": {0}. A guia só poderá ser fechada após a autorização, ou não autorização, destes procedimentos."),
	
	//PAGAMENTO
	SOMENTE_MATRICULA_BOLETO_PODE_SER_REATIVADA("Apenas matrículas que pagam em boleto podem ser (re)ativadas."),
	NAO_HA_BOLETO_A_SER_GERADO("Não há boleto a ser gerado!"),
	BOLETO_ABERTO_PARA_SEGURADO("O beneficiário {0} já possui um boleto aberto e não vencido para esta competência, por favor, reimprima-o."),
	SEGURADO_POSSUI_BOLETO_PAGO("O beneficiário {0} já possui um boleto pago para esta competência. Não é possível regerá-lo."),
	
	
	//ORDENADOR
	VALOR_TETO_MAIOR_QUE_LIMITE_INFORMADO("O Valor R$ {0} do faturamento {1} supera o Limite informado de R$ {2}"),
	
	
	//Boleto
	IMAGEM_DE_COMUNICACAO_ALTURA_ERRADA("A imagem de comunicacao deve possuir {0} pixels de altura."),
	IMAGEM_DE_COMUNICACAO_LARGURA_ERRADA("A imagem de comunicacao deve possuir {0} pixels de largura."),
	IMAGEM_DE_ENVELOPE_ALTURA_ERRADA("A imagem do envelope deve possuir {0} pixels de altura."),
	IMAGEM_DE_ENVELOPE_LARGURA_ERRADA("A imagem do envelope deve possuir {0} pixels de largura."),
	QUESTIONARIO_ALERTA_CID("O beneficiário possue DLPs ligadas ao(s) CID(s) contido(s) nesta guia, favor consulte o cadastro do beneficiário para verificação"),
	QUESTIONARIO_ALERTA_SUB_GRUPO("O beneficiário possui DLPs ligadas ao(s) procedimento(s) contido(s) nesta guia, favor consulte o cadastro do beneficiário para verificação. Código: {0}"),
	
	// Registro de honorarios de procedimentos contidos em guias Faturado(a) ou Pago(a)
	PROCEDIMENTO_COM_HONORARIO_PAGO("Não é possível registrar honorário para o procedimento {0} para o grau cirurgião. Os honorários de cirurgião referentes a este procedimento já foram pagos dentro da guia de origem."),
	
	NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__CBHPM("Caro usuário, você tentou alterar o procedimento de código {0} para o procedimento de código {1}. Não é permitido alterar procedimentos contidos em guias nas situações 'Faturado(a)' ou 'Pago(a)'."),
	NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__PORCENTAGEM("Caro usuário você tentou alterar a porcentagem do procedimento de código {0}. Não é permitido alterar a porcentagem de procedimentos contidos em guias nas situações 'Faturado(a)' ou 'Pago(a)'."),
	NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__GLOSA("Caro usuário você tentou glosar o procedimento de código {0}. Não é permitido glosar procedimentos contidos em guias nas situações 'Faturado(a)' ou 'Pago(a)'."),
	NAO_E_POSSIVEL_ALTERAR_PROCEDIMENTO_DE_GUIA_PAGA__DATA_REALIZACAO("Caro usuário você tentou alterar a data de realização do procedimento de código {0}. Não é permitido alterar a data de procedimentos contidos em guias nas situações 'Faturado(a)' ou 'Pago(a)'."),
	
	//Mensagens que têm relação com o fluxo de auditar guia, especificamente atendimento de Gasoterapia
	GASOTERAPIA_GLOSADA_SEM_MOTIVO_DE_GLOSA("O motivo da Alteração/Glosa para gasoterapia {0} deve ser preenchido."),
	TAXA_GLOSADA_SEM_MOTIVO_DE_GLOSA("O motivo da Alteração/Glosa para taxa {0} deve ser preenchido."),
	
	//Cadastro de Item Especialidade
	NAO_MARCOU_MODALIDADE("Não foi possível cadastrar a especialidade pois nenhuma modalidade foi marcada. Marque pelo menos uma modalidade."),
	
	NENHUMA_MENSAGEM_ENCONTRADA("Nenhuma mensagem foi encontrada."),
	SELECIONAR_MENSAGEM_LER("Selecione apenas uma mensagem para ser lida."),
	MENSAGEM_NAO_SELECIONADA("Selecione pelo menos uma mensagem para ser lida."),
	INFORMAR_PELO_MENOS_UM_DOS_PARAMETROS("Informe pelo menos um dos parâmetros."),
	
	PERIODICIDADE_TRATAMENTO_ODONTO("Novos procedimentos só podem ser confirmados a partir do dia {0}."),
	PERIODICIDADE_TRATAMENTO_ODONTO_PARCIAL("Só podem ser realizados {0} procedimentos. Os demais só podem ser confirmados a partir do dia {1}."),
	
	//Painel de controle
	
	INTERVALOS_IGUAIS("Intervalo inválido! Início e fim iguais!"),
	INTERVALOS_CONFLITANTES("Intervalo inserido \"{0}\" conflita com intervalo \"{1}\" já cadastrado!"),
	HORA_INVALIDA("Horário \"{0}\" inválido!"),
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
