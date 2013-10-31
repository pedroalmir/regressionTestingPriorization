package br.com.infoway.ecare.services.tarefasCorrecao.relatorioMedAlliance;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Linha {
	private String cartao;
	private String nome;
	private BigDecimal total = BigDecimal.ZERO;

	private ResumoGuias consultas = new ResumoGuias();
	private ResumoGuias exames = new ResumoGuias();
	private ResumoGuias urgencias = new ResumoGuias();
	private ResumoGuias internacoes = new ResumoGuias();

	public Linha(String cartao, String nome) {
		this.cartao = cartao;
		this.nome = nome;
	}

	public void addValor(String tipo, BigInteger quant, BigDecimal valor) {
		if (tipo.equals("GCS")) {
			consultas.add(quant, valor);
		}
		if (tipo.equals("GEX")) {
			exames.add(quant, valor);
		}
		if (tipo.equals("CUR") || tipo.equals("AUR")) {
			urgencias.add(quant, valor);
		}
		if (tipo.equals("IEL") || tipo.equals("IUR")) {
			internacoes.add(quant, valor);
		}
		total = total.add(valor);
	}

	public BigDecimal getValorConsultas() {
		return this.consultas.getValor();
	}

	public BigDecimal getValorExames() {
		return this.exames.getValor();
	}

	public BigDecimal getValorUrgencias() {
		return this.urgencias.getValor();
	}

	public BigDecimal getValorInternacoes() {
		return this.internacoes.getValor();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(cartao + ";");
		builder.append(nome + ";");
		builder.append(consultas + ";");
		builder.append(exames + ";");
		builder.append(urgencias + ";");
		builder.append(internacoes + ";");

		return builder.toString();
	}

	public String getCartao() {
		return cartao;
	}

	public String getNome() {
		return nome;
	}

	public BigDecimal getTotal() {
		return total;
	}
}
