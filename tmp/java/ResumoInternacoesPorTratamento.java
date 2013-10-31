package br.com.infowaypi.ecare.services;


public class ResumoInternacoesPorTratamento {
	private String prestador;
	
	private int qtdeClinicoAberto;
	private int qdeCirurgicoAberto;
	private Integer totalAberto;
	
	private int qtdeClinicoFechado;
	private int qtdeCirurgicoFechado;
	private Integer totalFechado;
	
	private int diferenca;
	
	public ResumoInternacoesPorTratamento(String prestador, int qtdeClinicoAberto, int qdeCirurgicoAberto, int qtdeClinicoFechado, int qtdeCirurgicoFechado) {
		this.prestador = prestador;
		
		this.qtdeClinicoAberto = qtdeClinicoAberto;
		this.qdeCirurgicoAberto = qdeCirurgicoAberto;
		this.totalAberto = this.qtdeClinicoAberto + this.qdeCirurgicoAberto;
		
		this.qtdeClinicoFechado = qtdeClinicoFechado;
		this.qtdeCirurgicoFechado = qtdeCirurgicoFechado;
		this.totalFechado = this.qtdeClinicoFechado + this.qtdeCirurgicoFechado;
		
		this.diferenca = this.totalAberto - this.totalFechado;
	}

	public String getPrestador() {
		return prestador.toUpperCase();
	}

	public void setPrestador(String prestador) {
		this.prestador = prestador;
	}

	public int getQtdeClinicoAberto() {
		return qtdeClinicoAberto;
	}

	public void setQtdeClinicoAberto(int qtdeClinicoAberto) {
		this.qtdeClinicoAberto = qtdeClinicoAberto;
	}

	public int getQdeCirurgicoAberto() {
		return qdeCirurgicoAberto;
	}

	public void setQdeCirurgicoAberto(int qdeCirurgicoAberto) {
		this.qdeCirurgicoAberto = qdeCirurgicoAberto;
	}

	public Integer getTotalAberto() {
		return totalAberto;
	}

	public void setTotalAberto(Integer totalAberto) {
		this.totalAberto = totalAberto;
	}

	public int getQtdeClinicoFechado() {
		return qtdeClinicoFechado;
	}

	public void setQtdeClinicoFechado(int qtdeClinicoFechado) {
		this.qtdeClinicoFechado = qtdeClinicoFechado;
	}

	public int getQtdeCirurgicoFechado() {
		return qtdeCirurgicoFechado;
	}

	public void setQtdeCirurgicoFechado(int qtdeCirurgicoFechado) {
		this.qtdeCirurgicoFechado = qtdeCirurgicoFechado;
	}

	public Integer getTotalFechado() {
		return totalFechado;
	}

	public void setTotalFechado(Integer totalFechado) {
		this.totalFechado = totalFechado;
	}

	public int getDiferenca() {
		return diferenca;
	}

	public void setDiferenca(int diferenca) {
		this.diferenca = diferenca;
	}

}
