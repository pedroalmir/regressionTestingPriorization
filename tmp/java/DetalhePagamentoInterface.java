package br.com.infowaypi.ecare.financeiro;

import java.io.Serializable;

import br.com.infowaypi.msr.financeiro.Banco;

public interface DetalhePagamentoInterface extends Serializable{
	
	public static final Integer CONTA_CORRENTE = 1;
	public static final Integer FOLHA = 2;
	public static final Integer BOLETO = 3;

	public abstract Long getIdDetalhePagamento();

	public abstract String getAgencia();

	public abstract void setAgencia(String agencia);

	public abstract Banco getBanco();

	public abstract void setBanco(Banco banco);

	public abstract Boolean getChecado();

	public abstract void setChecado(Boolean checado);

	public abstract String getContaCorrente();

	public abstract void setContaCorrente(String contaCorrente);

	public abstract String getMatricula();

	public abstract void setMatricula(String matricula);

	public abstract String getOperacao();

	public abstract void setOperacao(String operacao);
	
	public Integer getTipoPagamento();
	
	public String getDescricaoTipoPagamento();

	public void setTipoPagamento(Integer tipoPagamento);

}