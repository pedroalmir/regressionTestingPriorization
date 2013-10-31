package br.com.infowaypi.ecarebc.atendimentos;
/**
 * @author ThiagoVoiD
 **/
import java.util.List;

import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;

public class ResumoConsultasPromocionais {
	
	private List<PromocaoConsulta> consultas;
	private Integer totalEletivaLiberada;
	private Integer totalEletivaUtilizada;
	private Integer totalEletivaVencida;
	private Integer totalUrgenciaLiberada;
	private Integer totalUrgenciaUtilizada;
	private Integer totalUrgenciaVencida;
	private Integer totalEletivas;
	private Integer totalUrgencias;
	
	public ResumoConsultasPromocionais(List<PromocaoConsulta> consultas) {
		this.consultas = consultas;
	}

	public Integer getNumeroTotalDeConsultasPromocionais(){
		return this.consultas.size();
	}
	
	public Integer getNumeroConsultasLiberadas(){
		int quantidadeDeConsultasLiberadas = 0;
		
		for (PromocaoConsulta consulta : consultas) {
			if (consulta.isLiberado())
				quantidadeDeConsultasLiberadas++; 
		}
		
		return quantidadeDeConsultasLiberadas;
	}
	
	public Integer getNumeroConsultasUtilizadas(){
		int quantidadeDeConsultasUtilizadas = 0;
		
		for (PromocaoConsulta consulta : consultas) {
			if (consulta.isUtilizado())
				quantidadeDeConsultasUtilizadas++;
		}
		
		return quantidadeDeConsultasUtilizadas;
	}
	
	public Integer getNumeroConsultasVencidas(){
		int quantidadeDeConsultasVencidas = 0;
		
		for (PromocaoConsulta consulta : consultas) {
			if (consulta.isVencido())
				quantidadeDeConsultasVencidas++;
		}
		
		return quantidadeDeConsultasVencidas;
	}
	
	public List<PromocaoConsulta> getConsultas() {
		return consultas;
	}

	public void setConsultas(List<PromocaoConsulta> consultas) {
		this.consultas = consultas;
	}

	public Integer getTotalEletivaLiberada() {
		return totalEletivaLiberada;
	}

	public Integer getTotalEletivaUtilizada() {
		return totalEletivaUtilizada;
	}

	public Integer getTotalEletivaVencida() {
		return totalEletivaVencida;
	}

	public Integer getTotalUrgenciaLiberada() {
		return totalUrgenciaLiberada;
	}

	public Integer getTotalUrgenciaUtilizada() {
		return totalUrgenciaUtilizada;
	}

	public Integer getTotalUrgenciaVencida() {
		return totalUrgenciaVencida;
	}
	
	public Integer getTotalEletivas() {
		return totalEletivas;
	}

	public Integer getTotalUrgencias() {
		return totalUrgencias;
	}

	public void computarResumo(){
		totalEletivaLiberada = 0;
		totalEletivaUtilizada = 0;
		totalEletivaVencida = 0;
		totalUrgenciaLiberada = 0;
		totalUrgenciaUtilizada = 0;
		totalUrgenciaVencida = 0;
		totalEletivas = 0;
		totalUrgencias = 0;
		
		for (PromocaoConsulta consulta : this.consultas) {
			
			if (consulta.isEletiva()) {
				totalEletivas++;
				if (consulta.isLiberado()) {
					totalEletivaLiberada++;
				}
				if (consulta.isUtilizado()) {
					totalEletivaUtilizada++;
				}
				if (consulta.isVencido()) {
					totalEletivaVencida++;
				}
			}
			if (consulta.isUrgencia()) {
				totalUrgencias++;
				if (consulta.isLiberado()) {
					totalUrgenciaLiberada++;
				}
				if (consulta.isUtilizado()) {
					totalUrgenciaUtilizada++;
				}
				if (consulta.isVencido()) {
					totalUrgenciaVencida++;
				}
			}
		}
	}


	
}
