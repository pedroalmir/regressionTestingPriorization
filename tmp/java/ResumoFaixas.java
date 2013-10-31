package br.com.infowaypi.ecare.services;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.enums.SexoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.utils.MoneyCalculation;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Resumo de pagamentos.
 * @author Mário Sérgio Coelho Marroquim/ Idelvane
 */
public class ResumoFaixas {

	private List<ResumoFaixa> faixas;
	private int totalVidasAtivas;
	private BigDecimal valorTotalReceitas;
	private BigDecimal valorTotalDespesas;
	private int quantidadeConsultas;
	private int quantidadeExames;
	private int quantidadeProcedimentos;
	private BigDecimal consumoGeralDeConsultas;
	private BigDecimal consumoGeralDeExames;
	private java.util.Date competencia; 
	
	
	public int getQuantidadeProcedimentos() {
		return quantidadeProcedimentos;
	}

	public void setQuantidadeProcedimentos(int quantidadeProcedimentos) {
		this.quantidadeProcedimentos = quantidadeProcedimentos;
	}

	public ResumoFaixas() {
		valorTotalDespesas = new BigDecimal(0f);
		valorTotalReceitas = new BigDecimal(0f);
		consumoGeralDeConsultas = new BigDecimal(0f);
		consumoGeralDeExames = new BigDecimal(0f);
		faixas = new ArrayList<ResumoFaixa>();
		addFaixa(0, 18,    "00 a 18");
		addFaixa(19, 23,   "19 a 23");
		addFaixa(24, 28,   "24 a 28");
		addFaixa(29, 33,   "29 a 33");
		addFaixa(34, 38,   "34 a 38");
		addFaixa(39, 43,   "39 a 43");
		addFaixa(44, 48,   "44 a 48");
		addFaixa(49, 53,   "49 a 53");
		addFaixa(54, 59,   "54 a 59");
		addFaixa(60, 1000, " >= 60 ");
	}

	private void addFaixa(Integer de, Integer ate, String descricao) {
		ResumoFaixa faixa = new ResumoFaixa(de, ate, descricao);
		faixas.add(faixa);
	}

	public void computarValor(Date date, AbstractSegurado segurado) {
		java.util.Date dataNascimento = date;
		int idade = Utils.getIdade(dataNascimento);
		for (ResumoFaixa faixa : faixas) {
			if(faixa.possui(idade)){
				faixa.setVidasAtivas(faixa.getVidasAtivas() + 1);
				if(segurado.getPessoaFisica().getSexo().equals(SexoEnum.MASCULINO.value()))
					faixa.setVidasMasculinas(faixa.getVidasMasculinas() + 1);
				else faixa.setVidasFemininas(faixa.getVidasFemininas() + 1);
				break;
			}
		}
	}
	
	public void computarValor(Date date, BigDecimal valorTotalUsado) {
		java.util.Date dataNascimento = date;
		int idade = Utils.getIdade(dataNascimento);
		for (ResumoFaixa faixa : faixas) {
			if(faixa.possui(idade)){
				faixa.setNumeroDeVidasQueUsaramPlano(faixa.getNumeroDeVidasQueUsaramPlano() + 1);
				faixa.setCustoTotal(MoneyCalculation.rounded(faixa.getCustoTotal().add(valorTotalUsado)));
				break;
			}
		}
	}

	public void computarValorReceita(Date date, BigDecimal financiamento) {
		java.util.Date dataNascimento = date;
		int idade = Utils.getIdade(dataNascimento);
		
		BigDecimal valor = BigDecimal.ZERO;
		if(financiamento != null)
			valor = valor.add(financiamento);
		
		for (ResumoFaixa faixa : faixas) {
			if(faixa.possui(idade)){
				faixa.setReceitas(MoneyCalculation.rounded(faixa.getReceitas().add(valor)));
				break;
			}
		}
	}

	public void calcularMedias() {
		for (ResumoFaixa faixa : faixas)
			faixa.calcularMedias();
	}
	
	@Override
	public String toString() {
		StringBuffer string = new StringBuffer();
		for (ResumoFaixa faixa : faixas) {
			string.append(faixa.toString());
			string.append(System.getProperty("line.separator"));
		}
		return string.toString();
	}

	public List<ResumoFaixa> getFaixas() {
		return faixas;
	}
	
	public void setFaixas(List<ResumoFaixa> faixas) {
		this.faixas = faixas;
	}

	public String getSinistralidadePorQuantidade() {
		return this.getSinistralidade(1);
	}

	public String getSinistralidadePorValor() {
		return this.getSinistralidade(2);
	}
	
	private String getSinistralidade(Integer tipo) {
		StringBuffer string = new StringBuffer();
		for (ResumoFaixa faixa : faixas) {
			string.append(tipo.equals(1) ? faixa.sinistralidadeQuantitativa() : faixa.sinistralidadeMonetaria());
			string.append(System.getProperty("line.separator"));
		}
		return string.toString();
	}
	
	public int getTotalVidasAtivas() {
		return totalVidasAtivas;
	}

	public void setTotalVidasAtivas(int totalVidasAtivas) {
		this.totalVidasAtivas = totalVidasAtivas;
	}

	public BigDecimal getValorTotalDespesas() {
		return valorTotalDespesas;
	}

	public void setValorTotalDespesas(BigDecimal valorTotalDespesas) {
		this.valorTotalDespesas = valorTotalDespesas;
	}

	public BigDecimal getValorTotalReceitas() {
		return valorTotalReceitas;
	}

	public void setValorTotalReceitas(BigDecimal valorTotalReceitas) {
		this.valorTotalReceitas = valorTotalReceitas;
	}

	public int getQuantidadeConsultas() {
		return quantidadeConsultas;
	}

	public void setQuantidadeConsultas(int quantidadeConsultas) {
		this.quantidadeConsultas = quantidadeConsultas;
	}

	public int getQuantidadeExames() {
		return quantidadeExames;
	}

	public void setQuantidadeExames(int quantidadeExames) {
		this.quantidadeExames = quantidadeExames;
	}

	public BigDecimal getIndiceConsultas(){
		BigDecimal indiceConsultas = new BigDecimal(0f);
		if(this.quantidadeConsultas >0){
			indiceConsultas = new BigDecimal(((float)this.quantidadeConsultas * 12)/ (float)totalVidasAtivas);
		}
		return MoneyCalculation.rounded(indiceConsultas); 
	}
	public BigDecimal getIndiceExames(){
		BigDecimal indiceExames = new BigDecimal(0f);
		if(this.quantidadeExames >0){
			indiceExames = new BigDecimal(((float)this.quantidadeExames * 12)/ (float)totalVidasAtivas);
		}
		return MoneyCalculation.rounded(indiceExames); 
	}
	public BigDecimal getIndiceProcedimentos(){
		BigDecimal indiceProcedimentos = new BigDecimal(0f);
		if(this.quantidadeProcedimentos >0){
			indiceProcedimentos = new BigDecimal(((float)this.quantidadeProcedimentos * 12)/ (float)totalVidasAtivas);
		}
		return MoneyCalculation.rounded(indiceProcedimentos); 
	}
	
	public BigDecimal getSaldo(){
		return MoneyCalculation.rounded(this.valorTotalReceitas.subtract(this.valorTotalDespesas));
	}
	public BigDecimal getValorPerCaptaConsulta() throws SQLException{
		BigDecimal valor = new BigDecimal(0f);
		BigDecimal valorRetorno = new BigDecimal(0f);
		if(!this.consumoGeralDeConsultas.equals(BigDecimal.ZERO)){
			valor = MoneyCalculation.rounded(consumoGeralDeConsultas.divide(new BigDecimal((float)totalVidasAtivas), 2, BigDecimal.ROUND_HALF_UP));
			if(!valor.equals(BigDecimal.ZERO))
				valorRetorno = MoneyCalculation.rounded(MoneyCalculation.multiplica(valor , 12f));
		}
		return MoneyCalculation.rounded(valorRetorno);
	}
	
	public BigDecimal getValorPerCaptaExames(){
		BigDecimal valor = new BigDecimal(0f);
		BigDecimal valorRetorno = new BigDecimal(0f);
		if(!this.consumoGeralDeExames.equals(BigDecimal.ZERO)){
			valor = MoneyCalculation.rounded(consumoGeralDeExames.divide(new BigDecimal((float)totalVidasAtivas), 2, BigDecimal.ROUND_HALF_UP));
			if(!valor.equals(BigDecimal.ZERO))
				valorRetorno = MoneyCalculation.rounded(MoneyCalculation.multiplica(valor , 12f));
		}
		return MoneyCalculation.rounded(valorRetorno);
	}

	public BigDecimal getConsumoGeralDeConsultas() {
		return consumoGeralDeConsultas;
	}

	public void setConsumoGeralDeConsultas(BigDecimal consumoGeralDeConsultas) {
		this.consumoGeralDeConsultas = consumoGeralDeConsultas;
	}

	public BigDecimal getConsumoGeralDeExames() {
		return consumoGeralDeExames;
	}

	public void setConsumoGeralDeExames(BigDecimal consumoGeralDeExames) {
		this.consumoGeralDeExames = consumoGeralDeExames;
	}

	public String getCompetencia() {
		return Utils.format(competencia, "MM/yyyy");
	}
	
	public BigDecimal getSinistralidadeGeral(){
		BigDecimal sinistralidade = this.valorTotalDespesas.multiply(new BigDecimal(100f));
		sinistralidade = sinistralidade.divide(this.valorTotalReceitas, 2, BigDecimal.ROUND_HALF_UP);
		
		return sinistralidade;
	}
	public void setCompetencia(java.util.Date competencia) {
		this.competencia = competencia;
	} 
}