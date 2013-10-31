package br.com.infowaypi.ecare.resumos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;

public class ResumoSimulacaoTratamento {
	
	private Segurado segurado;
	private List<SimulacaoProcedimento> simulacaoProcedimentos;

	public ResumoSimulacaoTratamento(Segurado segurado, List<Procedimento> procedimentos){
		this.segurado = segurado;
		simulacaoProcedimentos = new ArrayList<SimulacaoProcedimento>();
		for (Procedimento procedimento : procedimentos) {
			SimulacaoProcedimento sp = new SimulacaoProcedimento(segurado, procedimento);
			simulacaoProcedimentos.add(sp);
		}

	}
	
	public List<SimulacaoProcedimento> getSimulacaoProcedimentos() {
		return simulacaoProcedimentos;
	}
	
	public void setSimulacaoProcedimentos(List<SimulacaoProcedimento> simulacaoProcedimentos) {
		this.simulacaoProcedimentos = simulacaoProcedimentos;
	}
	
	public Segurado getSegurado() {
		return segurado;
	}
	
	public void setSegurado(Segurado segurado) {
		this.segurado = segurado;
	}

	public BigDecimal getValorTotal() {
		BigDecimal valorTotal = new BigDecimal(0);
		for (SimulacaoProcedimento simulacaoProcedimento : simulacaoProcedimentos)
			valorTotal = valorTotal.add(simulacaoProcedimento.getProcedimento().getProcedimentoDaTabelaCBHPM().getValor());
		
		return valorTotal;
	}
	
	public BigDecimal getValorTotalCoParticipacao() {
		BigDecimal valorTotalCoparticipacao = new BigDecimal(0);
		for (SimulacaoProcedimento simulacaoProcedimento : simulacaoProcedimentos)
			valorTotalCoparticipacao = valorTotalCoparticipacao.add(simulacaoProcedimento.getProcedimento().getValorCoParticipacao());
		
		return valorTotalCoparticipacao;
	}
	
	public List<SimulacaoProcedimento> getProcedimentosLiberados(){
		List<SimulacaoProcedimento> procedimentosLiberados = new ArrayList<SimulacaoProcedimento>();
		for (SimulacaoProcedimento procedimento : this.simulacaoProcedimentos) {
			if(procedimento.isLiberado()){
				procedimentosLiberados.add(procedimento);
			}
		}
		return procedimentosLiberados;
	}
	
	public List<SimulacaoProcedimento> getProcedimentosNaoLiberados(){
		List<SimulacaoProcedimento> procedimentosNaoLiberados = new ArrayList<SimulacaoProcedimento>();
		for (SimulacaoProcedimento procedimento : this.simulacaoProcedimentos) {
			if(!procedimento.isLiberado()){
				procedimentosNaoLiberados.add(procedimento);
			}
		}
		return procedimentosNaoLiberados;
	}
	
	public Float getValorTotalProcedimentosLiberados(){
		Float valor = new Float(0);
		for (SimulacaoProcedimento simulacaoProcedimento : getProcedimentosLiberados()) {
			valor += simulacaoProcedimento.getProcedimento().getValorTotal().floatValue();
		}
		return valor;
	}
	
	public Float getValorTotalProcedimentosNaoLiberados(){
		Float valor = new Float(0);
		for (SimulacaoProcedimento simulacaoProcedimento : getProcedimentosNaoLiberados()) {
			valor += simulacaoProcedimento.getProcedimento().getValorTotal().floatValue();
		}
		return valor;
	}
	
	public Float getValorCoParticipacaoProcedimentosLiberados(){
		Float valor = new Float(0);
		for (SimulacaoProcedimento simulacaoProcedimento : getProcedimentosLiberados()) {
			valor += simulacaoProcedimento.getProcedimento().getValorCoParticipacao().floatValue();
		}
		return valor;
	}
	
	public Float getValorCoParticipacaoProcedimentosNaoLiberados(){
		Float valor = new Float(0);
		for (SimulacaoProcedimento simulacaoProcedimento : getProcedimentosNaoLiberados()) {
			valor += simulacaoProcedimento.getProcedimento().getValorCoParticipacao().floatValue();
		}
		return valor;
	}
	
}
