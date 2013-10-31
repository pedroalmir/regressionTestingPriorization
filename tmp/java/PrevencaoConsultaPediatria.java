package br.com.infowaypi.ecare.atendimentos.promocaosaude;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.LowerEquals;

/**
 * 
 * @author Josino Rodrigues
 */
public class PrevencaoConsultaPediatria extends CommandPrevencaoSaudeConsulta{

	public static final String ESPECIALIDADE_PEDIATRIA = "Pediatria";

	@Override
	public boolean geraCoparticipacao(GuiaConsulta guia) {
		
		if(!guia.getEspecialidade().getDescricao().equalsIgnoreCase(ESPECIALIDADE_PEDIATRIA))
			return true;
		AbstractSegurado segurado = guia.getSegurado();
		
		Calendar cal = new GregorianCalendar();
		Date dataAtual = cal.getTime();
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("segurado", segurado));
		sa.addParameter(new In("situacao.descricao",situacoes));
		sa.addParameter(new Equals("especialidade.descricao",ESPECIALIDADE_PEDIATRIA));
		sa.addParameter(new LowerEquals("dataAtendimento", dataAtual));
		
		if(idadeValida(segurado.getIdade(), 0, 2)){
			cal.add(Calendar.MONTH, -1);
			sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
			if(numeroDeGuiasSemCoparticipacao( sa.list(GuiaConsulta.class))>=1)
				return true;
			return false;
		}
		
		if(idadeValida(segurado.getIdade(), 3, 10)){
			cal.add(Calendar.MONTH, -6);
			sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
			if(numeroDeGuiasSemCoparticipacao( sa.list(GuiaConsulta.class))>=2)
				return true;
			return false;
		}
		
		if(idadeValida(segurado.getIdade(), 11, 18)){
			cal.add(Calendar.MONTH, -6);
			sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
			if(numeroDeGuiasSemCoparticipacao( sa.list(GuiaConsulta.class))>=1)
				return true;
			return false;
		}
			
		return true;
	}
}
