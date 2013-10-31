package br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo;


import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;
import br.com.infowaypi.msr.user.UsuarioInterface;

public interface StrategyRegistroBancoInterface {
	
	public final static char TIPO_A = 'A';  // HEADER
	public final static char TIPO_B = 'B';  // CADASTRAMENTO DE D�BITO AUTOM�TICO
	public final static char TIPO_F = 'F';  // RETORNO DO D�BITO/CR�DITO AUTOM�TICO
	public final static char TIPO_Z = 'Z';  // TRAILLER
	
	public final static char TIPO_T = 'T';  // 
	public final static char TIPO_J = 'J';  // CONFIRMA��O DE RECEBIMENTO DE ARQUIVOS
	
	public static final String MOVIMENTO_BOLETO_PAGO = "06";
	
	public static final String DEBITO_EFETUADO = "D�bito Efetuado";
	public static final String BB_DNE_01 = "01 - Insufici�ncia de fundos";
	public static final String BB_DNE_02 = "02 - Conta corrente nao cadastrada";
	public static final String BB_DNE_04 = "04 - Outras restri��es";
	public static final String BB_DNE_10 = "10 - Ag�ncia em regime de encerramento";
	public static final String BB_DNE_12 = "12 - Valor inv�lido";
	public static final String BB_DNE_13 = "13 - Data de lan�amento inv�lida";
	public static final String BB_DNE_14 = "14 - Ag�ncia inv�lida";
	public static final String BB_DNE_15 = "15 - DAC da conta corrente inv�lido";
	public static final String BB_DNE_18 = "18 - Data do d�bito anterior � do processamento";
	public static final String BB_DNE_30 = "30 - Sem contrato de d�bito autom�tico";
	public static final String BB_DNE_96 = "96 - Manuten��o do cadastro";
	public static final String BB_DNE_97 = "97 - Cancelamento - N�o encontrado";
	public static final String BB_DNE_98 = "98 - Cancelamento - N�o efetuado, fora de tempo h�bil";
	public static final String BB_DNE_99 = "99 - Cancelamento - Cancelado conforme solicita��o";
	
	public static final String CEF_DNE_01 = "01 - N�mero remessa inv�lido";
	public static final String CEF_DNE_02 = "02 - Arquivo sem HEADER";
	public static final String CEF_DNE_03 = "03 - Tipo registro inv�lido";
	public static final String CEF_DNE_04 = "04 - C�digo banco inv�lido";
	public static final String CEF_DNE_05 = "05 - Insufici�ncia de fundos";
	public static final String CEF_DNE_07 = "07 - C�digo do conv�nio inv�lido";
	public static final String CEF_DNE_08 = "08 - C�digo da remessa inv�lido";
	public static final String CEF_DNE_09 = "09 - Outras restri��es";
	public static final String CEF_DNE_11 = "11 - Ag�ncia inv�lida";
	public static final String CEF_DNE_12 = "12 - N�mero da conta inv�lido";
	public static final String CEF_DNE_15 = "15 - Tipo movimento inv�lido";
	public static final String CEF_DNE_19 = "19 - Data de pagamento inv�lido";
	public static final String CEF_DNE_20 = "20 - Tipo de moeda inv�lido";
	public static final String CEF_DNE_21 = "21 - Quantidade de moeda inv�lida";
	public static final String CEF_DNE_22 = "22 - Valor de pagamento inv�lido";
	public static final String CEF_DNE_34 = "34 - Data de vencimento inv�lida";
	public static final String CEF_DNE_39 = "39 - Remessa sem TRAILLER";
	public static final String CEF_DNE_40 = "40 - Total registros do TRAILLER inv�lido";
	public static final String CEF_DNE_41 = "41 - Valor total registros do TRAILLER inv�lido";
	public static final String CEF_DNE_46 = "46 - Data movimento inv�lida";
	public static final String CEF_DNE_47 = "47 - Identifica��o cliente empresa inv�lido";
	public static final String CEF_DNE_50 = "50 - Conv�nio n�o cadastrado";
	public static final String CEF_DNE_51 = "51 - Par�metro transmiss�o n�o cadastrado";
	public static final String CEF_DNE_52 = "52 - Compromisso n�o cadastrado";
	public static final String CEF_DNE_53 = "53 - Ag�ncia inativa";
	public static final String CEF_DNE_57 = "57 - Ag�ncia inv�lida";
	public static final String CEF_DNE_60 = "60 - Conta a debitar inexistente no cadastro de optantes";
	public static final String CEF_DNE_62 = "62 - N�mero do conv�nio inv�lido";
	public static final String CEF_DNE_63 = "63 - Tipo de compromisso inv�lido";
	public static final String CEF_DNE_64 = "64 - N�mero de compromisso inv�lido";
	public static final String CEF_DNE_65 = "65 - Mais de 1 TRAILLER na remessa";
	public static final String CEF_DNE_66 = "66 - Remessa com erro";
	public static final String CEF_DNE_67 = "67 - Data op��o inv�lida";
	public static final String CEF_DNE_76 = "76 - Quantidade de parcelas inv�lida";
	public static final String CEF_DNE_78 = "78 - Cadastro de optantes inexistente";
	public static final String CEF_DNE_81 = "81 - Conta n�o cadastrada";
	public static final String CEF_DNE_82 = "82 - Conta n�o cadastrada";
	public static final String CEF_DNE_85 = "85 - Data cancelamento expirada";
	public static final String CEF_DNE_86 = "86 - Agendamento n�o encontrado";
	public static final String CEF_DNE_87 = "87 - Valor do d�bito maior que o valor limite";
	public static final String CEF_DNE_89 = "89 - Data atual do compromisso n�o ativa";
	public static final String CEF_DNE_91 = "91 - Registro j� existente na base";
	public static final String CEF_DNE_95 = "95 - Data do d�bito inferior a 3 dias �teis";
	public static final String CEF_DNE_96 = "96 - Manuten��o de cadastro";
	public static final String CEF_DNE_99 = "99 - Cancelamento conforme solicitado";

	public void executar(String linha, ArquivoDeRetorno arquivo, UsuarioInterface usuario) throws Exception;
}