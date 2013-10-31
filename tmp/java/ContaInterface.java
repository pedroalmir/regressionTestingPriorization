package br.com.infowaypi.ecarebc.financeiro.conta;

import java.io.Serializable;

import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeEnvio;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;

public interface ContaInterface extends Serializable, PagamentoInterface{

	public static final String CONTA_ABERTA = "Aberto(a)";
	public static final String CONTA_VENCIDA = "Vencido(a)";
	public static final String CONTA_PAGA = "Pago(a)";
	public static final String CONTA_INADIMPLENTE = "Inadimplente";
	public static final String CONTA_CANCELADA = "Cancelado(a)";
	
	public static final int TIPO_A_PAGAR = 1;
	public static final int TIPO_A_RECEBER = 2;
	
	//constantes boleto
	public static final String IDENTIFICADOR_DO_PRODUTO        = "8";
	public static final String IDENTIFICADOR_DO_SEGMENTO       = "6";
	public static final String IDENTIFICADOR_DO_VALOR_REAL     = "7";
	//varificar cnpj UNIPLAM
	public static final String IDENTIFICADOR_EMPRESA     	   = "05351257";
	
	
//	public static final String DIGITO_FIXO_CAMPO_LIVRE         = "1";
//	public static final String CODIGO_BANCO_CAIXA_ECONOMICA    = "104";
//	public static final String DESCRICAO_CEDENTE               = "PROTEÇÃO - ASSISTÊNCIA MÉDICO-HOSPITALAR LTDA";
//	public static final String CODIGO_CLIENTE_CEDENTE_CAIXA    = "021804";
//	public static final String CODIGO_MOEDA                    = "9";
//	public static final String CNPJ_CEDENTE                    = "05.351.257/0001-01";
//	public static final String DADOS_BANCARIOS_CEDENTE         = "1606/021804-1";
//	public static final String SEGUNDO_DIGITO_FIXO_CAMPO_LIVRE = "9";
	
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.ContaInterface#getIdConta()
	 */
	public abstract Long getIdConta();

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.ContaInterface#setIdConta(java.lang.Long)
	 */
	public abstract void setIdConta(Long idConta);

	public ArquivoDeEnvio getArquivoEnvio();

	public void setArquivoEnvio(ArquivoDeEnvio arquivoEnvio);

	public ArquivoDeRetorno getArquivoRetorno();

	public void setArquivoRetorno(ArquivoDeRetorno arquivoRetorno);
	
	public Long getCodigoLegado();
	
	public void setCodigoLegado(Long codigoLegado);

	public Integer getTipoPagamento();
	
	public void setTipoPagamento(Integer tipoPagamento);
	
	public Boleto getBoleto();
	
	public ComponenteColecaoContas getColecaoContas();
	
	public void setColecaoContas(ComponenteColecaoContas colecaoContas);
	
	public boolean isVinculado();

	public void setVinculado(boolean vinculado);
	
	public String getCompetenciaFormatada();
	
	public String getIdentificadorTitular();
	
	public void setIdentificadorTitular(String identificadorTitular);
	
	@Deprecated
	public String getNossoNumero();
	
	public Long getNossoNumeroLong();
	
	public void setNossoNumeroLong(Long nossoNumeroLong);
	
}