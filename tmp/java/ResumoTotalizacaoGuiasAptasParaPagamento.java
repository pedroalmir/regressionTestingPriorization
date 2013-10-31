package br.com.infowaypi.ecarebc.atendimentos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.utils.GuiaUtils;

public class ResumoTotalizacaoGuiasAptasParaPagamento {

	private String descricao;
	private BigDecimal totalGuiasConfirmadas = BigDecimal.ZERO;
	private BigDecimal totalGuiasAuditadas = BigDecimal.ZERO;
	private BigDecimal totalGuiasRecebidas = BigDecimal.ZERO;
	private BigDecimal totalGeral = BigDecimal.ZERO;
	private int quantidadeGuiasAuditadas = 0;
	private int quantidadeGuiasRecebidas = 0;
	private String porcentagemValorAuditado;
	private String porcentagemNumeroGuias;

	public ResumoTotalizacaoGuiasAptasParaPagamento(String descricao, BigDecimal totalGuiasConfirmadas, BigDecimal totalGuiasAuditadas,
			BigDecimal totalGuiasRecebidas, BigDecimal totalGeral) {
		this.descricao = descricao;
		this.totalGuiasConfirmadas = totalGuiasConfirmadas;
		this.totalGuiasAuditadas = totalGuiasAuditadas;
		this.totalGuiasRecebidas = totalGuiasRecebidas;
		this.totalGeral = totalGeral;
	}

	public ResumoTotalizacaoGuiasAptasParaPagamento(String descricao, BigDecimal totalGuiasAuditadas, BigDecimal totalGuiasRecebidas,
			int quantidadeGuiasAuditadas, int quantidadeGuiasRecebidas, String porcentagemValorAuditado,
			String porcentagemNumeroGuias) {
		this.descricao = descricao;
		this.totalGuiasAuditadas = totalGuiasAuditadas;
		this.totalGuiasRecebidas = totalGuiasRecebidas;
		this.quantidadeGuiasAuditadas = quantidadeGuiasAuditadas;
		this.quantidadeGuiasRecebidas = quantidadeGuiasRecebidas;
		this.porcentagemValorAuditado = porcentagemValorAuditado;
		this.porcentagemNumeroGuias = porcentagemNumeroGuias;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public BigDecimal getTotalGuiasConfirmadas() {
		return totalGuiasConfirmadas;
	}
	public void setTotalGuiasConfirmadas(BigDecimal totalGuiasConfirmadas) {
		this.totalGuiasConfirmadas = totalGuiasConfirmadas;
	}
	public BigDecimal getTotalGuiasAuditadas() {
		return totalGuiasAuditadas;
	}
	public void setTotalGuiasAuditadas(BigDecimal totalGuiasAuditadas) {
		this.totalGuiasAuditadas = totalGuiasAuditadas;
	}
	public BigDecimal getTotalGuiasRecebidas() {
		return totalGuiasRecebidas;
	}
	public void setTotalGuiasRecebidas(BigDecimal totalGuiasRecebidas) {
		this.totalGuiasRecebidas = totalGuiasRecebidas;
	}
	public BigDecimal getTotalGeral() {
		return totalGeral;
	}
	public void setTotalGeral(BigDecimal totalGeral) {
		this.totalGeral = totalGeral;
	}

	public int getQuantidadeGuiasAuditadas() {
		return quantidadeGuiasAuditadas;
	}

	public void setQuantidadeGuiasAuditadas(int quantidadeGuiasAuditadas) {
		this.quantidadeGuiasAuditadas = quantidadeGuiasAuditadas;
	}

	public int getQuantidadeGuiasRecebidas() {
		return quantidadeGuiasRecebidas;
	}

	public void setQuantidadeGuiasRecebidas(int quantidadeGuiasRecebidas) {
		this.quantidadeGuiasRecebidas = quantidadeGuiasRecebidas;
	}

	public String getPorcentagemValorAuditado() {
		return porcentagemValorAuditado;
	}

	public void setPorcentagemValorAuditado(String porcentagemValorAuditado) {
		this.porcentagemValorAuditado = porcentagemValorAuditado;
	}

	public String getPorcentagemNumeroGuias() {
		return porcentagemNumeroGuias;
	}

	public void setPorcentagemNumeroGuias(String porcentagemNumeroGuias) {
		this.porcentagemNumeroGuias = porcentagemNumeroGuias;
	}
	
}
