package br.com.infowaypi.ecare.atendimentos.promocaosaude;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.enums.SexoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.GreaterEquals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.LowerEquals;

public class PrevencaoConsultaProctologia extends CommandPrevencaoSaudeConsulta {

	private static final String ESPECIALIDADE_PROCTOLOGIA = "Proctologia";

	@Override
	public boolean geraCoparticipacao(GuiaConsulta guia) {

		if(!guia.getEspecialidade().getDescricao().equalsIgnoreCase(ESPECIALIDADE_PROCTOLOGIA))
			return true;
		AbstractSegurado segurado = guia.getSegurado();
		
		Calendar cal = new GregorianCalendar();
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("segurado", segurado));
		sa.addParameter(new In("situacao.descricao", situacoes));
		sa.addParameter(new Equals("especialidade.descricao", ESPECIALIDADE_PROCTOLOGIA));
		sa.addParameter(new LowerEquals("dataAtendimento", new Date()));

		if (idadeValida(segurado.getIdade(), 40, 1000)
			/*	&& segurado.getSexo() == SexoEnum.MASCULINO.value().intValue()*/) {
			cal.add(Calendar.YEAR, -1);
			sa
					.addParameter(new GreaterEquals("dataAtendimento", cal
							.getTime()));
			if (numeroDeGuiasSemCoparticipacao(sa.list(GuiaConsulta.class)) >= 1)
				return true;
			return false;
		}
		return true;
	}

}
