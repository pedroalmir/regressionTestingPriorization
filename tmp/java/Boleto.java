package br.com.infowaypi.ecarebc.financeiro.conta;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import br.com.infowaypi.jbank.boletos.BoletoBean;
import br.com.infowaypi.msr.utils.Utils;


public class Boleto extends BoletoBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String competencia;
	private Float juros;
	private Float desconto;
	private Float outrasDeducoes; 

	public Boleto(){
		super();
	}
	
	public Boleto(ContaInterface conta){
		super();
		this.setNossoNumero(String.valueOf(conta.getIdConta()));
		this.setValorBoleto(conta.getValorCobrado().toString());
		this.setDataVencimento(isNull(conta.getDataVencimento()) ?" ":Utils.format(conta.getDataVencimento(),"dd/MM/yyyy"));
		this.competencia = isNull(conta.getCompetencia()) ?" ":Utils.format(conta.getCompetencia(),"MM/yyyy");
	}
	
	
	public String getValorDoTituloFormatado() {
    	String valorCobradoFormatado = (new DecimalFormat("#,###,##0.00")).format(this.getValorBoleto());
    	valorCobradoFormatado = StringUtils.replace(valorCobradoFormatado , ",", ".");
		return valorCobradoFormatado;
	}
	
	public String getCampoDigitavel(){
		return GeradorCodigoBarras.getCodigoBarras(
				ContaInterface.IDENTIFICADOR_DO_PRODUTO, 
				ContaInterface.IDENTIFICADOR_DO_SEGMENTO, 
				ContaInterface.IDENTIFICADOR_DO_VALOR_REAL,
				new BigDecimal(this.getValorBoleto()), 
				ContaInterface.IDENTIFICADOR_EMPRESA,
				this.getNossoNumero());
	}	

	public String getCodigoDeBarras(){
		return GeradorCodigoBarras.getCodigoBarras(
				ContaInterface.IDENTIFICADOR_DO_PRODUTO,
				ContaInterface.IDENTIFICADOR_DO_SEGMENTO,
				ContaInterface.IDENTIFICADOR_DO_VALOR_REAL,
				new BigDecimal(this.getValorBoleto()),
				ContaInterface.IDENTIFICADOR_EMPRESA,
				this.getNossoNumero());			
	}	
	
	public String getCodigoDeBarrasFormatado(){
		return GeradorCodigoBarras.getCodigoBarrasFormatado(
				ContaInterface.IDENTIFICADOR_DO_PRODUTO,
				ContaInterface.IDENTIFICADOR_DO_SEGMENTO,
				ContaInterface.IDENTIFICADOR_DO_VALOR_REAL,
				new BigDecimal(this.getValorBoleto()),
				ContaInterface.IDENTIFICADOR_EMPRESA,
				this.getNossoNumero());
	}
	
	public String getNossoNumeroFormatado() {
		return StringUtils.leftPad(this.getNossoNumero(),21,"0");
	}

	public String getCompetencia() {
		return competencia;
	}

	public void setCompetencia(String competencia) {
		this.competencia = competencia;
	}

	public String getStringVazia() {
		return " ";
	}

	public Float getDesconto() {
		return desconto;
	}

	public void setDesconto(Float desconto) {
		this.desconto = desconto;
	}

	public Float getJuros() {
		return juros;
	}

	public void setJuros(Float juros) {
		this.juros = juros;
	}

	public Float getOutrasDeducoes() {
		return outrasDeducoes;
	}

	public void setOutrasDeducoes(Float outrasDeducoes) {
		this.outrasDeducoes = outrasDeducoes;
	}
		
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Boleto)) {
			return false;
		}
		Boleto boleto = (Boleto) object;
		return new EqualsBuilder()
			.append(this.getCodigoDeBarras(), boleto.getCodigoDeBarras())
			.isEquals();
	}
	
	private boolean isNull(Object object){
		if (object == null)
			return true;
		return false;
	}
	
//	public static void main(String[] args) {
//		Conta conta = new Conta();
//		conta.setIdConta(new Long(1));
//		conta.setDataVencimento(new Date());
//		conta.setDataPagamento(new Date());
//		conta.setCompetencia(new Date());
//	}
}
