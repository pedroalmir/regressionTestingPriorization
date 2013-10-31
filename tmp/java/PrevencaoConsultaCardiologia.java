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
 * 
 * Este command verifica se o beneficiario já realizou uma consulta de cardiologia e retorna uma resultado que indica 
 * se vai ser gerada ou não co-participacao para a guia que está sendo confirmada.
 *  
 * Beneficiários entre 35 e 45 anos tem direito a 1 consulta sem coparticipacao por ano
 * Beneficiários entre 46 e 60 anos tem direito a 1 consulta sem coparticipacao por semestre
 * Beneficiários entre acima de 60 anos tem direito a 1 consulta sem coparticipacao por bimestre
 * 
 */
public class PrevencaoConsultaCardiologia extends CommandPrevencaoSaudeConsulta{

	public static final String ESPECIALIDADE_CARDIOLOGIA = "Cardiologia";

	@Override
	public boolean geraCoparticipacao(GuiaConsulta guia) {
		
		if(!guia.getEspecialidade().getDescricao().equalsIgnoreCase(ESPECIALIDADE_CARDIOLOGIA))
			return true;
		AbstractSegurado segurado = guia.getSegurado();

		if(segurado.getIdade() < 35)
			return true;
		
		Calendar cal = new GregorianCalendar();
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("segurado", segurado));
		sa.addParameter(new In("situacao.descricao",situacoes));
		sa.addParameter(new Equals("especialidade.descricao",ESPECIALIDADE_CARDIOLOGIA));
		sa.addParameter(new LowerEquals("dataAtendimento", new Date()));
		
		if(idadeValida(segurado.getIdade(), 35, 45)){
			cal.add(Calendar.YEAR, -1);
			sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
			if(numeroDeGuiasSemCoparticipacao( sa.list(GuiaConsulta.class))>=1)
				return true;
			return false;
		}
		
		if(idadeValida(segurado.getIdade(), 46, 60)){
			cal.add(Calendar.MONTH, -6);
			sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
			if(numeroDeGuiasSemCoparticipacao( sa.list(GuiaConsulta.class))>=1)
				return true;
			return false;
		}
		
		if(idadeValida(segurado.getIdade(), 61, 1000)){
			cal.add(Calendar.MONTH, -2);
			sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
			if(numeroDeGuiasSemCoparticipacao(sa.list(GuiaConsulta.class))>=1)
				return true;
			return false;
		}
		
		return true;
	}

}
