package br.com.infowaypi.ecarebc.financeiro.conta;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeEnvio;
import br.com.infowaypi.ecarebc.financeiro.arquivos.ArquivoDeRetorno;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

public class Conta extends ImplPagamento implements ContaInterface{
	
	private static final long serialVersionUID = 1L;
	
	private Long idConta;
	private Long codigoLegado;
	
	private ComponenteColecaoContas colecaoContas;
	private ArquivoDeEnvio arquivoEnvio;
	private ArquivoDeRetorno arquivoRetorno;
	private Integer tipoPagamento; 
	private boolean vinculado;
	private String identificadorTitular;
	private int tipoDeConta;
	private Boleto boleto;
	private Long nossoNumeroLong;
	
	public Conta() {
		this(null);
	}
	
	public Conta(UsuarioInterface usuario) {
		super();
		this.mudarSituacao(usuario, CONTA_ABERTA, "Cadastro de conta", null);
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#getIdConta()
	 */
	public Long getIdConta() {
		return idConta;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#setIdConta(java.lang.Long)
	 */
	public void setIdConta(Long idConta) {
		this.idConta = idConta;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#processarVencimento()
	 */
	public void processarVencimento() {
		try {
			if (this.isVencida()){
				this.mudarSituacao(null, VENCIDO, "Processamento automático de conta.", null);
					ImplDAO.save(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isVencida(){
		boolean contaAberta = this.isSituacaoAtual(ABERTO);
		boolean contaVenceu = Utils.compareData(this.getDataVencimento(), new Date()) < 0;
		if (contaAberta && contaVenceu)
			return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#getArquivoEnvio()
	 */
	public ArquivoDeEnvio getArquivoEnvio() {
		return arquivoEnvio;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#setArquivoEnvio(br.com.infowaypi.ecare.financeiro.arquivos.ArquivoDeEnvio)
	 */
	public void setArquivoEnvio(ArquivoDeEnvio arquivoEnvio) {
		this.arquivoEnvio = arquivoEnvio;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#getArquivoRetorno()
	 */
	public ArquivoDeRetorno getArquivoRetorno() {
		return arquivoRetorno;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#setArquivoRetorno(br.com.infowaypi.ecare.financeiro.arquivos.ArquivoDeRetorno)
	 */
	public void setArquivoRetorno(ArquivoDeRetorno arquivoRetorno) {
		this.arquivoRetorno = arquivoRetorno;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#getCodigoLegado()
	 */
	public Long getCodigoLegado() {
		return codigoLegado;
	}
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#setCodigoLegado(java.lang.Long)
	 */
	public void setCodigoLegado(Long codigoLegado) {
		this.codigoLegado = codigoLegado;
	}
	
	public String getIdentificadorTitular() {
		return identificadorTitular;
	}
	
	public void setIdentificadorTitular(String identificadorTitular) {
		this.identificadorTitular = identificadorTitular;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#getTipoPagamento()
	 */
	public Integer getTipoPagamento() {
		return tipoPagamento;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#setTipoPagamento(java.lang.Integer)
	 */
	public void setTipoPagamento(Integer tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}
	
	public String getCompetenciaFormatada(){
		if (this.getCompetencia() != null)
			return Utils.format(this.getCompetencia(), "MM/yyyy");
		else return "";
	}
	
	public boolean isVinculado() {
		return vinculado;
	}

	public void setVinculado(boolean vinculado) {
		this.vinculado = vinculado;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#getBoleto()
	 */
	public Boleto getBoleto() {
		if (this.boleto == null)
			this.boleto = new Boleto(this);
		return boleto;
	}	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#getColecaoContas()
	 */
	public ComponenteColecaoContas getColecaoContas() {
		return colecaoContas;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#setColecaoContas(br.com.infowaypi.ecare.financeiro.conta.ComponenteColecaoContas)
	 */
	public void setColecaoContas(ComponenteColecaoContas colecaoContas) {
		this.colecaoContas = colecaoContas;
	}
	
	public int getTipoDeConta() {
		return tipoDeConta;
	}

	public void setTipoDeConta(int tipoDeConta) {
		this.tipoDeConta = tipoDeConta;
	}

	public String getNossoNumero() {
		return String.valueOf(nossoNumeroLong);
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.conta.ContaInterface#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ContaInterface)) {
			return false;
		}
		ContaInterface outro = (ContaInterface) object;
		return new EqualsBuilder()
			.append(this.getIdConta(), outro.getIdConta())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
		.append("idConta", this.idConta)
		.append("competencia", this.competencia)
		.append("dataVencimento", this.dataVencimento)
		.append("dataPagamento", this.dataPagamento)
		.append("valorCobrado", this.valorCobrado)
		.append("valorPago", this.valorPago)
		.append("colecaoContas", this.colecaoContas)
		.toString();
	}
	
	public Long getNossoNumeroLong() {
		return nossoNumeroLong;
	}

	public void setNossoNumeroLong(Long nossoNumeroLong) {
		this.nossoNumeroLong = nossoNumeroLong;
	}
	
}
