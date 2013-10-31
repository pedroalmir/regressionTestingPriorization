package br.com.infowaypi.ecare.resumos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.com.infowaypi.ecare.financeiro.faturamento.FaturamentoSR;
import br.com.infowaypi.ecare.procedimentos.ProcedimentoCirurgicoSR;
import br.com.infowaypi.ecare.segurados.AbstractMatricula;
import br.com.infowaypi.ecare.segurados.DependenteInterface;
import br.com.infowaypi.ecare.segurados.Titular;
import br.com.infowaypi.ecare.services.Faixa;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.TipoSeguradoEnum;
import br.com.infowaypi.ecarebc.financeiro.consignacao.Consignacao;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
/**
 * 
 * @author Josino
 * Resumo usado para 
 */
public class ResumoReceitaEDespesa {
	
	Set<ResumoFaixa> resumosFaixas;
	BigDecimal receitaTotal;
	BigDecimal despesaTotal;
	Date competencia;
	
	public ResumoReceitaEDespesa(List<GuiaSimples> guias,String tipoSegurado){
		receitaTotal = BigDecimal.ZERO;
		despesaTotal = BigDecimal.ZERO;
		computarGuias(guias,tipoSegurado);
	}
	
	private void computarGuias(List<GuiaSimples> guias,String tipoBeneficiario){
		Map<Faixa, ResumoFaixa> mapResumos = getMapResumoFaixa();
		ResumoFaixa resumoFaixa;
		for (GuiaSimples guia : guias) {
			despesaTotal = despesaTotal.add(guia.getValorTotal());
			if(!isBeneficiarioValido(tipoBeneficiario, guia.getSegurado()))
				continue;
			resumoFaixa = mapResumos.get(Faixa.getFaixaPorIdade(guia.getSegurado().getPessoaFisica().getIdade()));
			
			resumoFaixa.setQuantidadeDeSegurados(resumoFaixa.getQuantidadeDeSegurados()+1);
			resumoFaixa.setDespesa(resumoFaixa.getDespesa().add(guia.getValorTotal()));
			
		}
		resumosFaixas = new TreeSet<ResumoFaixa>(mapResumos.values());
	}
	
	public void computarFaturamentoAnestesista(List<FaturamentoSR> faturamentosAnestesicos,String tipoBeneficiario){
		Map<Faixa, ResumoFaixa> mapResumos = getMapResumoFaixa();
		Faixa faixa;
		ResumoFaixa resumoFaixa;
		BigDecimal valorTotalAnestesistas = BigDecimal.ZERO;
		System.out.println("faturamentosAnestesicos.size(): "+ faturamentosAnestesicos.size());
		for (FaturamentoSR faturamentoSR : faturamentosAnestesicos) {
			for (ProcedimentoInterface procedimento : faturamentoSR.getProcedimentosAnestesicos()) {
				valorTotalAnestesistas = valorTotalAnestesistas.add(procedimento.getValorAnestesista());
				if(!isBeneficiarioValido(tipoBeneficiario, procedimento.getGuia().getSegurado()))
					continue;
				resumoFaixa = mapResumos.get(Faixa.getFaixaPorIdade(procedimento.getGuia().getSegurado().getPessoaFisica().getIdade()));
				resumoFaixa.setDespesa(resumoFaixa.getDespesa().add(procedimento.getValorAnestesista()));
			}
		}
		despesaTotal = despesaTotal.add(valorTotalAnestesistas);
		System.out.println("VALOR ANESTESISTAS: "+valorTotalAnestesistas);
		resumosFaixas = new TreeSet<ResumoFaixa>(mapResumos.values());
	}
	
	private boolean isBeneficiarioValido(String tipoBeneficiario, AbstractSegurado segurado) {
		
		if(TipoSeguradoEnum.TODOS.descricao().equals(tipoBeneficiario))
			return true;
		
		if(tipoBeneficiario.equals(segurado.getTipoDeSegurado()))
			return true;
		
		return false;
	}
	
	private void computarResumo(Set<Titular> titulares, Date competencia){

		Map<Faixa, ResumoFaixa> mapResumos = getMapResumoFaixa();
		Consignacao consignacao;
		BigDecimal salarioBase;
		ResumoFaixa resumoFaixa;
		for (Titular titular : titulares) {
			salarioBase = BigDecimal.ZERO;
			for (AbstractMatricula matricula : titular.getMatriculas()) {
				salarioBase = salarioBase.add(matricula.getSalario());
			}
			resumoFaixa = mapResumos.get(Faixa.getFaixaPorIdade(titular.getIdade()));
			resumoFaixa.setReceita(resumoFaixa.getReceita().add(titular.getValorFinanciamento(salarioBase)[1]));
			
			for (DependenteInterface dependente : titular.getDependentes()) {
				resumoFaixa = mapResumos.get(Faixa.getFaixaPorIdade(dependente.getIdade()));
				resumoFaixa.setReceita(resumoFaixa.getReceita().add(dependente.getValorFinanciamentoDependente(salarioBase, new Date())[1]));
			}
		}
	}


	private Map<Faixa, ResumoFaixa> getMapResumoFaixa() {
		Map<Faixa, ResumoFaixa> mapResumos = new HashMap<Faixa, ResumoFaixa>();
		if(this.resumosFaixas == null || this.resumosFaixas.isEmpty()){
			ResumoFaixa resumoFaixa;
			for (Faixa faixa : Faixa.values()) {
				resumoFaixa = new ResumoFaixa(faixa);
				mapResumos.put(faixa, resumoFaixa);
			}
		}else{
			for (ResumoFaixa resumoFaixa : this.resumosFaixas) {
				mapResumos.put(resumoFaixa.getFaixa(), resumoFaixa);
			}
		}
		return mapResumos;
	}
	
	public void calcularSalario(Titular titular){
	}
	
	public Set<ResumoFaixa> getResumosFaixas() {
		return resumosFaixas;
	}
	
	public void setResumosFaixas(Set<ResumoFaixa> resumosFaixas) {
		this.resumosFaixas = resumosFaixas;
	}
	
	public BigDecimal getReceitaTotalSeguradosEncontrados(){
		BigDecimal total = BigDecimal.ZERO;
		for (ResumoFaixa resumo : resumosFaixas) {
			total = total.add(resumo.getReceita());
		}
		return total;
	}

	public BigDecimal getDespesaTotalSeguradosEncontrados(){
		BigDecimal total = BigDecimal.ZERO;
		for (ResumoFaixa resumo : resumosFaixas) {
			total = total.add(resumo.getDespesa());
		}
		return total;
	}
	
	public BigDecimal getReceitaTotal() {
		return receitaTotal;
	}
	
	public void setReceitaTotal(BigDecimal receitaTotal) {
		this.receitaTotal = receitaTotal;
	}
	
	public BigDecimal getDespesaTotal() {
		return despesaTotal;
	}
	
	public void setDespesaTotal(BigDecimal despesaTotal) {
		this.despesaTotal = despesaTotal;
	}
}