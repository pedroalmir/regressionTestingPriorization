package br.com.infowaypi.ecarebc.constantes;


import java.math.BigDecimal;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.Visibilidade;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.msr.financeiro.InformacaoFinanceiraInterface;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;
 
/**
 * Esta classe contém uma série de constantes para serem usadas em
 * todo o sistema. 
 * @author Mário Sérgio Coelho Marroquim
 */
public interface Constantes {

	
	/*(non-Javadoc)
	 * Constantes para as formas de pagamento.
	 */
	public static final Integer CONTA_CORRENTE = InformacaoFinanceiraInterface.CONTA_CORRENTE;
	public static final Integer FOLHA = InformacaoFinanceiraInterface.FOLHA;
	public static final Integer BOLETO = InformacaoFinanceiraInterface.BOLETO;
	public static final Integer TODOS = 0;
	
	/* (non-Javadoc)
	 * Constantes para as situações do sistema.
	 */
	public static final String SITUACAO_PROBLEMAS_NO_CADASTRO = "Cadastro com problemas";
	public static final String SITUACAO_ATIVO = "Ativo(a)";
	public static final String SITUACAO_INATIVO = "Inativo(a)";
	public static final String SITUACAO_SUSPENSO = "Suspenso(a)";
	public static final String SITUACAO_AGENDADA = "Agendado(a)";
	public static final String SITUACAO_CANCELADO = "Cancelado(a)";
	public static final String SITUACAO_CONFIRMADO = "Confirmado(a)";	
	public static final String SITUACAO_PAGO = "Pago(a)";
	public static final String SITUACAO_INADIMPLENTE = "Inadimplente";
	public static final String SITUACAO_REALIZADO = "Realizado(a)";
	public static final String SITUACAO_SOLICITADO = "Solicitado(a)";
	public static final String SITUACAO_SOLICITADO_INTERNACAO = "Solicitado(a) Internação";
	public static final String SITUACAO_SOLICITADO_PRORROGACAO = "Solicitado(a) Prorrogação";
	public static final String SITUACAO_FATURADA = "Faturado(a)";
	public static final String SITUACAO_GLOSADA = "Glosado(a)";	
	public static final String SITUACAO_ABERTO = "Aberto(a)";
	public static final String SITUACAO_ENVIADO = "Enviado(a)";
	public static final String SITUACAO_RECURSADO = "Recursado(a)";
	public static final String SITUACAO_DEFERIDO = "Deferido(a)";
	public static final String SITUACAO_INDEFERIDO = "Indeferido(a)";
	/*
	 * (non-Javadoc)
	 * constantes para os cartões
	 */
	public static final String SITUACAO_GERADO = "Gerado(a)";
	public static final String SITUACAO_ENVIADO_PARA_IMPRESSAO = "Enviado(a) para impressão";
	public static final String SITUACAO_IMPRESSO = "Impresso(a)";
	public static final String SITUACAO_ENTREGUE = "Entregue";
	
	/* (non-Javadoc)
	 * Constantes para os motivos de cancelamento de um titular
	 */
	public static final String MOTIVO_INADIMPLENCIA = "Cobrança vencida";	
	
	/* (non-Javadoc)
	 * Constantes para as situações do segurado
	 */
	public static final Integer SITUACAO_NO_IAPEP_NORMAL = 1;
	public static final Integer SITUACAO_NO_IAPEP_APOSENTADO = 2;
	public static final Integer SITUACAO_NO_IAPEP_OBITO = 3;
	
	/* (non-Javadoc)
	 * Constantes para os tipos de segurado.
	 */
	//TODO Revisar estes valores na hora da migração de dados.
	public static final Integer SITUACAO_PREVIDENCIARIA_TITULAR_ATIVO = 1;
	public static final Integer SITUACAO_PREVIDENCIARIA_TITULAR_INATIVO = 2;
	public static final Integer SITUACAO_PREVIDENCIARIA_INSTITUIDOR_DE_PENSAO = 3;
	
	public static final Integer SITUACAO_PREVIDENCIARIA_PENSIONISTA_PREVIDENCIARIO = 4;
	public static final Integer SITUACAO_DEPENDENTE = 9;
	
	public static final Integer SITUACAO_PREVIDENCIARIA_ATIVO = 1;
	public static final Integer SITUACAO_PREVIDENCIARIA_INATIVO = 2;
	public static final Integer SITUACAO_PREVIDENCIARIA_PENSIONISTA = 3;
	public static final Integer SITUACAO_PREVIDENCIARIA_PENSIONISTA_RATEADO = 4;

	/* (non-Javadoc)
	 * Constantes para as situações funcionais do titular em um órgão do sistema.
	 */
	public static final Integer SITUACAO_FUNCIONAL_OUTROS = 1;
	public static final Integer SITUACAO_FUNCIONAL_EM_EXERCICIO = 2;
	public static final Integer SITUACAO_FUNCIONAL_CEDIDO_PARA_OUTRO_ORGAO = 3;
	public static final Integer SITUACAO_FUNCIONAL_CEDIDO_PARA_OUTRO_NIVEL = 4;
	public static final Integer SITUACAO_FUNCIONAL_EXERCENDO_MANDATO = 5;
	public static final Integer SITUACAO_FUNCIONAL_EM_LICENCA_AFASTAMENTO = 6;
	public static final Integer SITUACAO_FUNCIONAL_EM_DISPONIBILIDADE = 7;
	public static final Integer SITUACAO_FUNCIONAL_CEDIDO_PARA_OUTRAS_ENTIDADES = 8;

	/* (non-Javadoc)
	 * Constantes para os vínculos de um titular ao governo, em um órgão.
	 */
	public static final Integer VINCULO_OUTROS = 1;
	public static final Integer VINCULO_CARGO_EFETIVO = 2;
	public static final Integer VINCULO_CARGO_TEMPORARIO = 3;
	public static final Integer VINCULO_CARGO_COMISSIONADO_EXCLUSIVO = 4;
	public static final Integer VINCULO_CARGO_MILITAR = 5;
	public static final Integer VINCULO_CARGO_EMPREGO_PUBLICO = 6;	
	public static final Integer VINCULO_CARGO_MANDATO_ELETIVO_EXCLUSIVO = 7;
	
	/* (non-Javadoc)
	 * Constantes para códigos de poder de um titular.
	 */
	public static final Integer PODER_EXECUTIVO = 1;
	public static final Integer PODER_LEGISLATIVO = 2;
	public static final Integer PODER_JUDICIARIO = 3;
	public static final Integer PODER_MINISTERIO_PUBLICO = 4;
	
	/* (non-Javadoc)
	 * Constantes para o sexo das pessoas, procedimentos e especialidades.
	 */
	public static final Integer SEXO_MASCULINO = PessoaFisicaInterface.SEXO_MASCULINO;
	public static final Integer SEXO_FEMININO = PessoaFisicaInterface.SEXO_FEMININO;
	public static final Integer SEXO_AMBOS = PessoaFisicaInterface.SEXO_AMBOS;

	/* (non-Javadoc)
	 * Constantes para os tipos de Dependência.
	 */
	public static final Integer TIPO_DEPENDENCIA_TITULAR = 0;
	public static final Integer TIPO_DEPENDENCIA_OUTROS = 1;
//	MM => marido/mulher
	public static final Integer TIPO_DEPENDENCIA_ESPOSO = 2;
//	CO => companheiro
	public static final Integer TIPO_DEPENDENCIA_COMPANHEIRO = 3;
//	FI => filho normal menor 21
	public static final Integer TIPO_DEPENDENCIA_FILHO_MENOR_21_ANOS = 4;
//	DP => filho inválido
	public static final Integer TIPO_DEPENDENCIA_FILHO_INVALIDO = 5;
//	FE => filho estudante
	public static final Integer TIPO_DEPENDENCIA_FILHO_ESTUDANTE = 15;
//	PM => pai/mãe
	public static final Integer TIPO_DEPENDENCIA_PAI_MAE_COM_DEPENDENCIA_ECONOMICA = 6;
	public static final Integer TIPO_DEPENDENCIA_IRMAO_MENOR_21_ANOS_COM_DEPENDENCIA_ECONOMICA = 7;
//	II => irmão invalido
	public static final Integer TIPO_DEPENDENCIA_IRMAO_INVALIDO_COM_DEPENDENCIA_ECONOMICA = 8;
//	EN => enteado menor que 21
	public static final Integer TIPO_DEPENDENCIA_ENTEADO_MENOR_21_ANOS_COM_DEPENDENCIA_ECONOMICA = 9;
//	EI => enteado invalido
	public static final Integer TIPO_DEPENDENCIA_ENTEADO_INVALIDO_COM_DEPENDENCIA_ECONOMICA = 10;
//	MT => menor tutelado
	public static final Integer TIPO_DEPENDENCIA_MENOR_TUTELADO_MENOR_21_ANOS_COM_DEPENDENCIA_ECONOMICA = 11;
//	MI => menor tutelado invalido
	public static final Integer TIPO_DEPENDENCIA_MENOR_TUTELADO_INVALIDO_COM_DEPENDENCIA_ECONOMICA = 12;
	public static final Integer TIPO_DEPENDENCIA_MENOR_SOB_GUARDA_MENOR_21_ANOS_COM_DEPENDENCIA_ECONOMICA = 13;
//	MG => menor sob guarda
	public static final Integer TIPO_DEPENDENCIA_MENOR_SOB_GUARDA_INVALIDO_COM_DEPENDENCIA_ECONOMICA = 14;
	
	public static final String DESCRICAO_TIPO_DEPENDENCIA_TITULAR = "Titular";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_OUTROS = "Outros";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_ESPOSO = "MM - Esposo(a)";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_COMPANHEIRO = "CO - Companheiro(a)";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_FILHO_MENOR_21_ANOS = "FI - Filho menor que 21 anos";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_FILHO_INVALIDO = "DP - Filho Inválido";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_FILHO_ESTUDANTE = "FE - Filho Estudante";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_PAI_MAE_COM_DEPENDENCIA_ECONOMICA = "PM - Pai/Mãe com dependência econômica";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_IRMAO_MENOR_21_ANOS_COM_DEPENDENCIA_ECONOMICA = "Irmão menor que 21 anos com dependência econômica";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_IRMAO_INVALIDO_COM_DEPENDENCIA_ECONOMICA = "II - Irmão inválido com dependência econômica";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_ENTEADO_MENOR_21_ANOS_COM_DEPENDENCIA_ECONOMICA = "EN - Enteado menor que 21 anos com dependência econômica";	
	public static final String DESCRICAO_TIPO_DEPENDENCIA_ENTEADO_INVALIDO_COM_DEPENDENCIA_ECONOMICA = "EI - Enteado inválido";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_MENOR_TUTELADO_MENOR_21_ANOS_COM_DEPENDENCIA_ECONOMICA = "MT - Menor tutelado com menos de 21 anos com dependência econômica";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_MENOR_TUTELADO_INVALIDO_COM_DEPENDENCIA_ECONOMICA = "MI - Menor tutelado inválido com dependência econômica";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_MENOR_SOB_GUARDA_MENOR_21_ANOS_COM_DEPENDENCIA_ECONOMICA = "Menor sob guarda com menos de 21 anos com dependência econômica";
	public static final String DESCRICAO_TIPO_DEPENDENCIA_MENOR_SOB_GUARDA_INVALIDO_COM_DEPENDENCIA_ECONOMICA = "MG - Menor sob guarda inválido";
	
	/* 
	 * Constantes para os tipos de Guia existentes.
	 */
	public static final int TIPO_GUIA_EXAME = 1;
	public static final int TIPO_GUIA_CONSULTA = 2;
	public static final int TIPO_GUIA_TRATAMENTO_ODONTO = 3;
	public static final int TIPO_GUIA_CONSULTA_ODONTO = 4;
	public static final int TIPO_GUIA_CONSULTA_URGENCIA = 5;
	public static final int TIPO_GUIA_INTERNACAO_ELETIVA = 6;
	public static final int TIPO_GUIA_ATENDIMENTO_SUBSEQUENTE = 7;
	public static final int TIPO_GUIA_INTERNACAO_URGENCIA= 8;
	public static final int TIPO_GUIA_TRATATAMENTO_SERIADO = 9;
	public static final int TIPO_GUIA_TODAS= 10;
	public static final int TIPO_GUIA_CONSULTA_ODONTO_URGENCIA = 11;
	public static final int TIPO_GUIA_CIRURGIA_ODONTO = 12;
	public static final int TIPO_GUIA_TODAS_INTERNACAO = 13;
	public static final int TIPO_GUIA_HONORARIO_MEDICO = 14;
	public static final int TIPO_GUIA_ACOMPANHAMENTO_ANESTESICO = 15;
	public static final int TIPO_GUIA_RECURSO_GLOSA = 16;
	
	/* (non-Javadoc)
	 * Constantes para a marcação de consultas e exames.
	 */
	public static final Integer VALIDADE_GUIA = 2;
	
	/* (non-Javadoc)
	 * Outras constantes úteis para a aplicação.
	 */
	public static final Integer IDADE_MINIMA_PARA_EXIGENCIA_DE_DOCUMENTO = 18;

	/* (non-Javadoc)
	 * Constantes para categorias de prestadores.
	 */
	public static final int CATEGORIA_TODOS = 999;
	public static final int CATEGORIA_CLINICAS_HOSPITAIS_CAPITAL = 1;
	public static final int CATEGORIA_CLINICAS_HOSPITAIS_LABORATORIOS_INTERIOR = 2;
	public static final int CATEGORIA_MEDICOS_INTERIOR = 3;
	public static final int CATEGORIA_ODONTOLOGIA_CAPITAL = 4;
	public static final int CATEGORIA_ODONTOLOGIA_INTERIOR = 5;
	public static final int CATEGORIA_RADIOLOGIA = 6;
	public static final int CATEGORIA_SUPERVISORES = 8;
	public static final int CATEGORIA_CONSULTAS_MEDICAS_CAPITAL = 9;
	public static final int CATEGORIA_CLINICAS_LABORATORIOS_EXAMES_CAPITAL = 10;
	public static final int CATEGORIA_MEDICOS_CAPITAL_PROCEDIMENTO_HOSPITALAR = 11;
	public static final int CATEGORIA_PERITOS = 12;
	public static final int CATEGORIA_PENSIONISTAS = 13;
	public static final int CATEGORIA_BIOQUIMICOS = 14;

	/* (non-Javadoc)
	 * Constantes para tipos de prestadores.
	 */
	public static final int TIPO_PRESTADOR_HOSPITAL = Prestador.TIPO_PRESTADOR_HOSPITAL;
	public static final int TIPO_PRESTADOR_LABORATORIO = Prestador.TIPO_PRESTADOR_LABORATORIO;
	public static final int TIPO_PRESTADOR_MEDICOS = Prestador.TIPO_PRESTADOR_MEDICOS;
	public static final int TIPO_PRESTADOR_DENTISTAS = Prestador.TIPO_PRESTADOR_DENTISTAS;
	public static final int TIPO_PRESTADOR_OUTROS = Prestador.TIPO_PRESTADOR_OUTROS;
	public static final int TIPO_PRESTADOR_CLINICAS_DE_EXAMES = Prestador.TIPO_PRESTADOR_CLINICAS_DE_EXAMES;
	public static final int TIPO_PRESTADOR_CLINICAS_DE_ODONTOLOGIA = Prestador.TIPO_PRESTADOR_CLINICAS_DE_ODONTOLOGIA;
	public static final int TIPO_PRESTADOR_CLINICAS_AMBULATORIAIS = Prestador.TIPO_PRESTADOR_CLINICAS_AMBULATORIAIS;
	public static final int TIPO_PRESTADOR_ANESTESISTA = Prestador.TIPO_PRESTADOR_ANESTESISTA;
	public static final int TIPO_PRESTADOR_TODOS = Prestador.TIPO_PRESTADOR_TODOS;
		
	
	/* (non-Javadoc)
	 * Constantes para tipos de pessoas.
	 */
	public static final Integer PESSOA_TODOS = 0;
	public static final Integer PESSOA_FISICA = 1;
	public static final Integer PESSOA_JURIDICA = 2;
	
	
	
	/*(non-Javadoc)
	 * Constantes para a questão do faturamento.
	 */
	public static final long FAIXA_1 = 1;
	public static final long FAIXA_2 = 2;
	public static final long FAIXA_3 = 3;
	public static final long FAIXA_4 = 4;
	public static final long FAIXA_5 = 5;
	
	
	/* (non-Javadoc)
	 * Constantes para bancos.
	 */
	public static final char BANCO_BRASIL = '1';
	public static final char BANCO_CEF = '2';
	public static final char BANCO_BRADESCO = '3';
	public static final char BANCO_BEP = '4';
	
	public static final char FATURAMENTO_ABERTO = 'A';
	public static final char FATURAMENTO_FECHADO = 'F';
	public static final char FATURAMENTO_PAGO = 'P';
	public static final char FATURAMENTO_CANCELADO = 'C';
	public static final char FATURAMENTO_EM_DEBITO = 'D';
	
	public static final int IMPOSTO_DE_RENDA = 0;
	public static final int ISS = 1;
	public static final int INSS = 2;
	public static final int PENSAO = 3;
	
	public static final Integer TIPO_ELEMENTO_ARCADA = 1;
	public static final Integer TIPO_ELEMENTO_DENTE = 2;
	
	public static final Integer TIPO_PADRAO = 1;
	public static final Integer TIPO_ODONTO = 2;

	public static final Integer VISIBILIDADE_MEDICO = Visibilidade.MEDICO.getValor();
	public static final Integer VISIBILIDADE_AMBOS = Visibilidade.AMBOS.getValor();
	public static final Integer VISIBILIDADE_ODONTOLOGICA = Visibilidade.ODONTOLOGICO.getValor();
	
	public static final Integer CARENCIA_CONSULTA = 1;
	public static final Integer CARENCIA_EXAME_NORMAL = 60;
	public static final Integer CARENCIA_EXAME_ESPECIAL = 180;
	
	//Constantes de Consignação
	public static final String GERACAO = "Geração";
	public static final String CANCELAMENTO = "Cancelamento";
	
	//Constantes de elemento
	public static final Integer DENTICAO = EstruturaOdontoEnum.DENTICAO.getValor();
	public static final Integer ARCADA = EstruturaOdontoEnum.ARCADA.getValor();
	public static final Integer QUADRANTE = EstruturaOdontoEnum.QUADRANTE.getValor();
	public static final Integer DENTE = EstruturaOdontoEnum.DENTE.getValor();
	public static final Integer FACE = EstruturaOdontoEnum.FACE.getValor();
	
	//Constantes de quantidade CBHPM
	public static final Integer QUANTIDADE_0 = 0;
	public static final Integer QUANTIDADE_1 = 1;
	public static final Integer QUANTIDADE_2 = 2;
	public static final Integer QUANTIDADE_3 = 3;
	public static final Integer QUANTIDADE_4 = 4;
	public static final Integer QUANTIDADE_5 = 5;
	public static final Integer QUANTIDADE_6 = 6;
	public static final Integer QUANTIDADE_7 = 7;
	public static final Integer QUANTIDADE_8 = 8;
	public static final Integer QUANTIDADE_9 = 9;
	public static final Integer QUANTIDADE_10 = 10;
	
	// Constantes para carências
	public static final Integer CARENCIA_1DIAS = 1;
	public static final Integer CARENCIA_15DIAS = 15;
	public static final Integer CARENCIA_180DIAS = 180;
	public static final Integer CARENCIA_300DIAS = 4;
	public static final Integer CARENCIA_24MESES = 5;
	
	//Constantes de porcentagens de procedimentos
	public static final BigDecimal PORCENTAGEM_0 = BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_UP);
	public static final BigDecimal PORCENTAGEM_30 = new BigDecimal(30).setScale(2, BigDecimal.ROUND_HALF_UP);
	public static final BigDecimal PORCENTAGEM_50 = new BigDecimal(50).setScale(2, BigDecimal.ROUND_HALF_UP);
	public static final BigDecimal PORCENTAGEM_70 = new BigDecimal(70).setScale(2, BigDecimal.ROUND_HALF_UP);
	public static final BigDecimal PORCENTAGEM_100 = new BigDecimal(100).setScale(2, BigDecimal.ROUND_HALF_UP);
	
	public static final String DESCRICAO_PORTE_ANESTESICO_1 = "P03A";
	public static final String DESCRICAO_PORTE_ANESTESICO_2 = "P03C";
	public static final String DESCRICAO_PORTE_ANESTESICO_3 = "P04C";
	public static final String DESCRICAO_PORTE_ANESTESICO_4 = "P06B";
	public static final String DESCRICAO_PORTE_ANESTESICO_5 = "P07C";
	public static final String DESCRICAO_PORTE_ANESTESICO_6 = "P09B";
	public static final String DESCRICAO_PORTE_ANESTESICO_7 = "P10C";
	public static final String DESCRICAO_PORTE_ANESTESICO_8 = "P12A";
	
	//Operadores lógicos
	public static final Integer NENHUM = null;
	public static final Integer IGUAL = 0;
	public static final Integer MAIOR = 1;
	public static final Integer MENOR = 2;
	public static final Integer MAIOR_IGUAL = 3;
	public static final Integer MENOR_IGUAL = 4;
	public static final Integer DIFERENTE = 5;
	
	//tipos faturamentos
	public static final Integer TIPO_FATURAMENTO_NENHUM = 0;
	public static final Integer TIPO_FATURAMENTO_NORMAL = 1;
	public static final Integer TIPO_FATURAMENTO_CAPITACAO = 2;
	public static final Integer TIPO_FATURAMENTO_PASSIVO = 3;

	//Ordenamentos
	public static final char SITUACAO_PROCESSADO = 'P';
	public static final char SITUACAO_FECHADO = 'F';
	
	
	/*
	 * RELATIVOS AOS VALORES QUE PODEM SER ATRIBUÍDOS AO CAMPO visibilidadeDoUsuario
	 */
	public static final Integer VISIBILIDADE_TOTAL = 1;//É O ADITOR, Q VÊ TUDO
	public static final Integer VISIBILIDADE_CENTRAL = 2;//É A CENTRAL, Q VÊ MENOS Q O AUDITOR E MAIS Q O PRESTADOR
	public static final Integer VISIBILIDADE_PRESTADOR = 3;//É O PRESTADOR, Q É O Q TEM MENOR ACESSO
	
	public static final Integer AUTORIZAR = 1;
	public static final Integer NAO_AUTORIZAR = 2;
	public static final Integer DEIXAR_PENDENTE = 3;
	
	//prestadores anestesistas
	public static final Long ID_COOPANEST = 373690L;
	
}