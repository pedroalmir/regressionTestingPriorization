package br.com.infowaypi.ecare.services;

import br.com.infowaypi.ecarebc.associados.Prestador;

public class ResumoPrestadorInternacoes {

	private String fantasiaPrestador;
	
	private Integer qtdeInternacoes;

	public ResumoPrestadorInternacoes(Prestador prestador, Integer qtdeInternacoes) {
		this.fantasiaPrestador = prestador.getPessoaJuridica().getFantasia();
		
		this.qtdeInternacoes = qtdeInternacoes;
	}

	public String getFantasiaPrestador() {
		return fantasiaPrestador.toUpperCase();
	}

	public void setFantasiaPrestador(String fantasiaPrestador) {
		this.fantasiaPrestador = fantasiaPrestador;
	}

	public Integer getQtdeInternacoes() {
		return qtdeInternacoes;
	}

	public void setQtdeInternacoes(Integer qtdeInternacoes) {
		this.qtdeInternacoes = qtdeInternacoes;
	}
	
}
