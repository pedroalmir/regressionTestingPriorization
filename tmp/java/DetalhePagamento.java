package br.com.infowaypi.ecare.financeiro;

import br.com.infowaypi.msr.financeiro.Banco;

public class DetalhePagamento implements  DetalhePagamentoInterface{

	private static final long serialVersionUID = 1L;

	private Long idDetalhePagamento;
	private Banco banco;
	private String contaCorrente;
	private String agencia;
	private String operacao;
	private Boolean checado;
	private String matricula;
	private Integer tipoPagamento;
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#getIdDetalhePagamento()
	 */
	public Long getIdDetalhePagamento() {
		return idDetalhePagamento;
	}

	protected void setIdDetalhePagamento(Long idDetalhePagamento) {
		this.idDetalhePagamento = idDetalhePagamento;
	}
	
	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#getAgencia()
	 */
	public String getAgencia() {
		return agencia;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#setAgencia(java.lang.String)
	 */
	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#getBanco()
	 */
	public Banco getBanco() {
		return banco;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#setBanco(br.com.infowaypi.msr.financeiro.Banco)
	 */
	public void setBanco(Banco banco) {
		this.banco = banco;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#getChecado()
	 */
	public Boolean getChecado() {
		return checado;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#setChecado(java.lang.Boolean)
	 */
	public void setChecado(Boolean checado) {
		this.checado = checado;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#getContaCorrente()
	 */
	public String getContaCorrente() {
		return contaCorrente;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#setContaCorrente(java.lang.String)
	 */
	public void setContaCorrente(String contaCorrente) {
		this.contaCorrente = contaCorrente;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#getMatricula()
	 */
	public String getMatricula() {
		return matricula;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#setMatricula(java.lang.String)
	 */
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#getOperacao()
	 */
	public String getOperacao() {
		return operacao;
	}

	/* (non-Javadoc)
	 * @see br.com.infowaypi.ecare.financeiro.DetalhePagamentoInterface#setOperacao(java.lang.String)
	 */
	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}
 
	public Integer getTipoPagamento() {
		return tipoPagamento;
	}
	
	public String getDescricaoTipoPagamento() {
		if (this.tipoPagamento == DetalhePagamentoInterface.BOLETO) {
			return "Boleto";
		}else if (this.tipoPagamento == DetalhePagamentoInterface.CONTA_CORRENTE) {
			return "Conta Corrente";
		}else {
			return "Folha";
		}
	}

	public void setTipoPagamento(Integer tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

}
