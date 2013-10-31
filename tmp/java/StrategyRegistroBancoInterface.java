package br.com.infowaypi.ecarebc.financeiro.arquivos.strategy.tipoArquivo;


import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;
import br.com.infowaypi.msr.user.UsuarioInterface;

public interface StrategyRegistroBancoInterface {
	
	public final static char TIPO_A = 'A';  // HEADER
	public final static char TIPO_B = 'B';  // CADASTRAMENTO DE DÉBITO AUTOMÁTICO
	public final static char TIPO_F = 'F';  // RETORNO DO DÉBITO/CRÉDITO AUTOMÁTICO
	public final static char TIPO_Z = 'Z';  // TRAILLER
	
	public final static char TIPO_T = 'T';  // 
	public final static char TIPO_J = 'J';  // CONFIRMAÇÃO DE RECEBIMENTO DE ARQUIVOS
	
	public static final String MOVIMENTO_BOLETO_PAGO = "06";
	
	public static final String DEBITO_EFETUADO = "Débito Efetuado";
	public static final String BB_DNE_01 = "01 - Insuficiência de fundos";
	public static final String BB_DNE_02 = "02 - Conta corrente nao cadastrada";
	public static final String BB_DNE_04 = "04 - Outras restrições";
	public static final String BB_DNE_10 = "10 - Agência em regime de encerramento";
	public static final String BB_DNE_12 = "12 - Valor inválido";
	public static final String BB_DNE_13 = "13 - Data de lançamento inválida";
	public static final String BB_DNE_14 = "14 - Agência inválida";
	public static final String BB_DNE_15 = "15 - DAC da conta corrente inválido";
	public static final String BB_DNE_18 = "18 - Data do débito anterior à do processamento";
	public static final String BB_DNE_30 = "30 - Sem contrato de débito automático";
	public static final String BB_DNE_96 = "96 - Manutenção do cadastro";
	public static final String BB_DNE_97 = "97 - Cancelamento - Não encontrado";
	public static final String BB_DNE_98 = "98 - Cancelamento - Não efetuado, fora de tempo hábil";
	public static final String BB_DNE_99 = "99 - Cancelamento - Cancelado conforme solicitação";
	
	public static final String CEF_DNE_01 = "01 - Número remessa inválido";
	public static final String CEF_DNE_02 = "02 - Arquivo sem HEADER";
	public static final String CEF_DNE_03 = "03 - Tipo registro inválido";
	public static final String CEF_DNE_04 = "04 - Código banco inválido";
	public static final String CEF_DNE_05 = "05 - Insuficiência de fundos";
	public static final String CEF_DNE_07 = "07 - Código do convênio inválido";
	public static final String CEF_DNE_08 = "08 - Código da remessa inválido";
	public static final String CEF_DNE_09 = "09 - Outras restrições";
	public static final String CEF_DNE_11 = "11 - Agência inválida";
	public static final String CEF_DNE_12 = "12 - Número da conta inválido";
	public static final String CEF_DNE_15 = "15 - Tipo movimento inválido";
	public static final String CEF_DNE_19 = "19 - Data de pagamento inválido";
	public static final String CEF_DNE_20 = "20 - Tipo de moeda inválido";
	public static final String CEF_DNE_21 = "21 - Quantidade de moeda inválida";
	public static final String CEF_DNE_22 = "22 - Valor de pagamento inválido";
	public static final String CEF_DNE_34 = "34 - Data de vencimento inválida";
	public static final String CEF_DNE_39 = "39 - Remessa sem TRAILLER";
	public static final String CEF_DNE_40 = "40 - Total registros do TRAILLER inválido";
	public static final String CEF_DNE_41 = "41 - Valor total registros do TRAILLER inválido";
	public static final String CEF_DNE_46 = "46 - Data movimento inválida";
	public static final String CEF_DNE_47 = "47 - Identificação cliente empresa inválido";
	public static final String CEF_DNE_50 = "50 - Convênio não cadastrado";
	public static final String CEF_DNE_51 = "51 - Parâmetro transmissão não cadastrado";
	public static final String CEF_DNE_52 = "52 - Compromisso não cadastrado";
	public static final String CEF_DNE_53 = "53 - Agência inativa";
	public static final String CEF_DNE_57 = "57 - Agência inválida";
	public static final String CEF_DNE_60 = "60 - Conta a debitar inexistente no cadastro de optantes";
	public static final String CEF_DNE_62 = "62 - Número do convênio inválido";
	public static final String CEF_DNE_63 = "63 - Tipo de compromisso inválido";
	public static final String CEF_DNE_64 = "64 - Número de compromisso inválido";
	public static final String CEF_DNE_65 = "65 - Mais de 1 TRAILLER na remessa";
	public static final String CEF_DNE_66 = "66 - Remessa com erro";
	public static final String CEF_DNE_67 = "67 - Data opção inválida";
	public static final String CEF_DNE_76 = "76 - Quantidade de parcelas inválida";
	public static final String CEF_DNE_78 = "78 - Cadastro de optantes inexistente";
	public static final String CEF_DNE_81 = "81 - Conta não cadastrada";
	public static final String CEF_DNE_82 = "82 - Conta não cadastrada";
	public static final String CEF_DNE_85 = "85 - Data cancelamento expirada";
	public static final String CEF_DNE_86 = "86 - Agendamento não encontrado";
	public static final String CEF_DNE_87 = "87 - Valor do débito maior que o valor limite";
	public static final String CEF_DNE_89 = "89 - Data atual do compromisso não ativa";
	public static final String CEF_DNE_91 = "91 - Registro já existente na base";
	public static final String CEF_DNE_95 = "95 - Data do débito inferior a 3 dias úteis";
	public static final String CEF_DNE_96 = "96 - Manutenção de cadastro";
	public static final String CEF_DNE_99 = "99 - Cancelamento conforme solicitado";

	public void executar(String linha, ArquivoDeRetorno arquivo, UsuarioInterface usuario) throws Exception;
}