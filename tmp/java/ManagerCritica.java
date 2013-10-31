package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.Critica;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;

public class ManagerCritica {

	public static void processaApresentaveis(GuiaSimples<?> guia, Integer ... tipoDeCritica){
		List<Integer> tiposDeCritica = Arrays.asList(tipoDeCritica);
		Set<Critica> criticasParaApresentacaoPrevia = new HashSet<Critica>();

		for (Critica critica: guia.getCriticas()) {
			if (!critica.isAvaliada() && tiposDeCritica.contains(critica.getTipoCritica())){
				criticasParaApresentacaoPrevia.add(critica);
			}
		}
		
		guia.setCriticasParaApresentacaoPrevia(criticasParaApresentacaoPrevia);
	}
	
	public static void processaSituacao(GuiaSimples<?> guia){
		for (Critica critica : guia.getCriticasParaApresentacaoPrevia()) {
			if(!critica.getOrigem().solicitado()){
				critica.setAvaliada(true);
				critica.setAutorizada(critica.getOrigem().autorizado());
			}
		}
	}

}
