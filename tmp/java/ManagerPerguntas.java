package br.com.infowaypi.ecare.questionarioqualificado;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.OrderBy;

public class ManagerPerguntas {
	
	/**
	 * Busca todas as perguntas existentes no banco e gera respostas sem assertiva.
	 */
	@SuppressWarnings("unchecked")
	public static List<Resposta> getRespostasQuestionario(){
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new OrderBy("idPergunta"));
		List<Pergunta> perguntas = sa.list(Pergunta.class);
		List<Resposta> respostas = new ArrayList<Resposta>(); 

		for (Pergunta pergunta : perguntas) {
			Resposta resposta = new Resposta();
			resposta.setPergunta(pergunta);
			respostas.add(resposta);
		}
		return respostas;
	}
}
