package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.msr.pessoa.PessoaJuridicaInterface;

public class PrestadorFaturamentoTunning {

	private Long idPrestador;
	private PessoaJuridicaInterface pessoaJuridica;
	private Set<AbstractFaturamento> faturamentos;
	private boolean pessoaFisica;

	public PrestadorFaturamentoTunning() {
		super();
	}

	public Long getIdPrestador() {
		return idPrestador;
	}

	public void setIdPrestador(Long idPrestador) {
		this.idPrestador = idPrestador;
	}

	public PessoaJuridicaInterface getPessoaJuridica() {
		return pessoaJuridica;
	}

	public void setPessoaJuridica(PessoaJuridicaInterface pessoaJuridica) {
		this.pessoaJuridica = pessoaJuridica;
	}

	public Set<AbstractFaturamento> getFaturamentos() {
		return faturamentos;
	}

	public void setFaturamentos(Set<AbstractFaturamento> faturamentos) {
		this.faturamentos = faturamentos;
	}

	public boolean isPessoaFisica() {
		return pessoaFisica;
	}

	public void setPessoaFisica(boolean pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}

	@Override
	public boolean equals(Object objeto) {
		if (this == objeto) {
			return true;
		}
		if (!(objeto instanceof PrestadorFaturamentoTunning)) {
			return false;
		}
		PrestadorFaturamentoTunning outro = (PrestadorFaturamentoTunning) objeto;
		return new EqualsBuilder().append(this.getIdPrestador(), outro.getIdPrestador()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.getIdPrestador()).toHashCode();
	}
}
