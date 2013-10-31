package br.com.infowaypi.ecarebc.associados;

import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;

public class ManagerPrestador {
	
	public static TabelaCBHPM getConsultaEspecialidade(Prestador prestador, Especialidade especialidade, boolean urgencia){
		for(PrestadorEspecialidadeTabelaCBHPM consultaEspecialidade : prestador.getPrestadoresEspecialidadesTabelaCBHPMAtivos()){
			if(consultaEspecialidade.isEletivo() != false || consultaEspecialidade.isUrgencia() != false){
				if(consultaEspecialidade.getEspecialidade().equals(especialidade)){
					if(consultaEspecialidade.isUrgencia() == urgencia){
						return consultaEspecialidade.getTabelaCBHPM();
					} else if (consultaEspecialidade.isEletivo() == !urgencia){
						return consultaEspecialidade.getTabelaCBHPM();
					}
				}
			}
		}
		return null;
	}
}
