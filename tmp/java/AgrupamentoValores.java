/**
 * 
 */
package br.com.infowaypi.ecare.financeiro.consignacao;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Josino Rodrigues/ Marcus bOolean
 *
 */
public class AgrupamentoValores {
	
	private String codigoEmpresa;
	private String codigoMatricula;
	private String cpf;
	private BigDecimal salario;
	private BigDecimal valorCoParticipacao;
	private BigDecimal valorDevolvido;
	private BigDecimal valorDeRestituicao;
	
	
	public BigDecimal getValorCoParticipacao() {
		return valorCoParticipacao;
	}

	public BigDecimal getValorDevolvido() {
		return valorDevolvido;
	}

	public BigDecimal getValorDeRestituicao() {
		return valorDeRestituicao;
	}

	public AgrupamentoValores() {
		
	}

	public final String getCodigoEmpresa() {
		return codigoEmpresa;
	}

	public final void setCodigoEmpresa(String codigoEmpresa) {
		this.codigoEmpresa = codigoEmpresa;
	}

	public final BigDecimal getSalario() {
		return salario;
	}

	public final void setSalario(String salario) {
		this.salario = new BigDecimal(Integer.parseInt(salario)/100F).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public final String getCodigoMatricula() {
		return StringUtils.stripStart(codigoMatricula, "0").trim();
	}

	public final void setCodigoMatricula(String codigoMatricula) {
		this.codigoMatricula = codigoMatricula;
	}
	
	public final void setValorCoParticipacao(String valorCoParticipacao) {
		this.valorCoParticipacao = new BigDecimal(Float.parseFloat(valorCoParticipacao)).setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public final void setValorDevolvido(String valorDevolvido) {
		this.valorDevolvido = new BigDecimal(Float.parseFloat(valorDevolvido)).setScale(2, BigDecimal.ROUND_HALF_UP);
		System.out.println("");
	}
	
	public final void setValorDeRestituicao(String valorDeRestituicao) {
		this.valorDeRestituicao = new BigDecimal(Float.parseFloat(valorDeRestituicao)).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public String getCpf() {
		return Utils.applyMask(this.cpf, "###.###.###-##");
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	

}
