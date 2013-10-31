package br.com.infowaypi.ecare.resumos;

import java.util.Date;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;

public class SimulacaoProcedimento {
	
	private Segurado segurado;
	private Procedimento procedimento;

	public SimulacaoProcedimento(Segurado segurado, Procedimento procedimento){
		this.segurado = segurado;
		this.procedimento = procedimento;
	}
	
	public Procedimento getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(Procedimento procedimento) {
		this.procedimento = procedimento;
	}

	public Segurado getSegurado() {
		return segurado;
	}

	public void setSegurado(Segurado segurado) {
		this.segurado = segurado;
	}
	
	public boolean isLiberado(){
		boolean cumpriuCarenciaExamesComplexos = segurado.getCarenciaRestanteExamesEspeciaisDeAltaComplexidadeCirurgiasEInternamento() <= 0;
		boolean cumpriuCarenciaExamesSimples = segurado.getCarenciaRestanteConsultasExamesDeBaixaComplexidade() <= 0;
		boolean procedimentoEspecial = procedimento.getProcedimentoDaTabelaCBHPM().getEspecial();
		
		if(procedimentoEspecial){
			if(cumpriuCarenciaExamesComplexos){
				return true;	
			}else return false;
			
		}else{
			if(cumpriuCarenciaExamesSimples){
				return true;
			}
			else return false;
		}
	}
	
	public Date getDataParaRealizacao(){
		return new Date();
	}
	
}
