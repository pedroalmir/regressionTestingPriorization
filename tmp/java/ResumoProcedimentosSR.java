package br.com.infowaypi.ecare.resumos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.infowaypi.ecarebc.financeiro.faturamento.ResumoProcedimento;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.msr.utils.MoneyCalculation;

public class ResumoProcedimentosSR {
	

	private List<ResumoProcedimento> resumos;
	private int quantidadeTotalProcedimentos;
	private Float valorTotalProcedimentos;
	
	private List<ResumoProcedimento> resumoExame;
	private List<ResumoProcedimento> resumoCirurgico;
	private List<ResumoProcedimento> resumoOdontologico;

	public ResumoProcedimentosSR(List<Procedimento> procedimentos) {
		resumos = new ArrayList<ResumoProcedimento>();
		valorTotalProcedimentos = 0f;
		quantidadeTotalProcedimentos = 0;
		
		resumoExame = new ArrayList<ResumoProcedimento>();
		resumoCirurgico = new ArrayList<ResumoProcedimento>();
		resumoOdontologico = new ArrayList<ResumoProcedimento>();
		
		computarResumoTotal(procedimentos);
	}

	private void computarResumoTotal(List<Procedimento> procedimentos) {
		String codigoCBHPM = "";
		long quantidadeProcedimentos = 0;
		Map<String,ResumoProcedimento> mapResumosProcedimento = new HashMap<String, ResumoProcedimento>();
		for (ProcedimentoInterface procedimento : procedimentos) {
			quantidadeProcedimentos += procedimento.getQuantidade();
			
			codigoCBHPM = procedimento.getProcedimentoDaTabelaCBHPM().getCodigo();
			ResumoProcedimento resumo;
			if(mapResumosProcedimento.keySet().contains(codigoCBHPM)){
				resumo = mapResumosProcedimento.get(codigoCBHPM);
				resumo.incrementarQuantidade(procedimento.getQuantidade());
				resumo.incrementarValor(procedimento.getValorTotal().floatValue());
			}else{
				resumo = new ResumoProcedimento(procedimento);
				mapResumosProcedimento.put(codigoCBHPM, resumo);
			}
		}
		resumos.addAll(mapResumosProcedimento.values());
		totalizar();
		
	}

	@SuppressWarnings("unchecked")
	private void totalizar() {
		Collections.sort(this.getResumos(), new Comparator(){
			public int compare(Object a, Object b){
				ResumoProcedimento resumo1 = (ResumoProcedimento)a;
				ResumoProcedimento resumo2 = (ResumoProcedimento)b;
				return resumo1.getProcedimento().getDescricao().compareTo(resumo2.getProcedimento().getDescricao());
			}
		});
		
		for (ResumoProcedimento resumo : this.getResumos()) {
			this.quantidadeTotalProcedimentos += resumo.getQuantidade();
			this.valorTotalProcedimentos = MoneyCalculation.getSoma(new BigDecimal(this.valorTotalProcedimentos), resumo.getValorTotal()).floatValue();
		}
	}
	
	public List<ResumoProcedimento> getResumos() {
		Collections.sort(resumos, new Comparator<ResumoProcedimento>(){

			public int compare(ResumoProcedimento resumo1, ResumoProcedimento resumo2) {
				return resumo1.getQuantidade().compareTo(resumo2.getQuantidade()) * -1;
			}
			
		});
		boolean isExiste = false;
		
		for (ResumoProcedimento resumo : resumos) {
			if((resumo.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_NORMAL)
				|| (resumo.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_OUTROS)){
				for (ResumoProcedimento exame : resumoExame) {
					if(exame.getProcedimento().getCodigo().equals(resumo.getProcedimento().getCodigo())){
						isExiste = true;
					}
						
				}
				if(isExiste == false)	
					this.getResumoExame().add(resumo);
			}
			else if(resumo.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_CIRURGICO){
				for (ResumoProcedimento cirurgico : resumoCirurgico) {
					if(cirurgico.getProcedimento().getCodigo().equals(resumo.getProcedimento().getCodigo())){
						isExiste = true;
					}
					
				}
				if(isExiste == false)	
					this.getResumoCirurgico().add(resumo);
				
					
			}
			else if((resumo.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_ODONTO)
				|| (resumo.getTipoProcedimento() == ProcedimentoInterface.PROCEDIMENTO_ODONTO_RESTAURACAO)){
				for (ResumoProcedimento odontologico : resumoOdontologico) {
					if(odontologico.getProcedimento().getCodigo().equals(resumo.getProcedimento().getCodigo())){
						isExiste = true;
					}
						
				}
				if(isExiste == false)	
					this.getResumoOdontologico().add(resumo);
				
					
			}
		}
		
		return resumos;
	}

	public List<ResumoProcedimento> getResumoExame() {
		return resumoExame;
	}

	public void setResumoExame(List<ResumoProcedimento> resumoExame) {
		this.resumoExame = resumoExame;
	}

	public List<ResumoProcedimento> getResumoCirurgico() {
		return resumoCirurgico;
	}

	public void setResumoCirurgico(List<ResumoProcedimento> resumoCirurgico) {
		this.resumoCirurgico = resumoCirurgico;
	}

	public List<ResumoProcedimento> getResumoOdontologico() {
		return resumoOdontologico;
	}

	public void setResumoOdontologico(List<ResumoProcedimento> resumoOdontologico) {
		this.resumoOdontologico = resumoOdontologico;
	}

	public int getQuantidadeTotalProcedimentos() {
		return quantidadeTotalProcedimentos;
	}

	public void setQuantidadeTotalProcedimentos(int quantidadeTotalProcedimentos) {
		this.quantidadeTotalProcedimentos = quantidadeTotalProcedimentos;
	}

	public Float getValorTotalProcedimentos() {
		return valorTotalProcedimentos;
	}

	public void setValorTotalProcedimentos(Float valorTotalProcedimentos) {
		this.valorTotalProcedimentos = valorTotalProcedimentos;
	}

	public void setResumos(List<ResumoProcedimento> resumos) {
		this.resumos = resumos;
	}
	
	public float getValorMedioPorProcedimento() {
		return this.valorTotalProcedimentos/ this.quantidadeTotalProcedimentos;
	}
	
}
