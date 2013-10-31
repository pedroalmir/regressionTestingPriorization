package br.com.infowaypi.ecare.atendimentos.promocaosaude;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.exceptions.PrevencaoSaudeException;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SexoEnum;
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
 * Este command verifica se a beneficiaria(apenas SEXO FEMININO) já realizou uma consulta para ginecologista e retorna uma resultado 
 * que indica se vai ser gerada ou não co-participacao para a guia que está sendo confirmada.
 *  
 * Beneficiárias acima de 35 anos tem direito a 1 consulta sem coparticipacao por semestre
 * 
 */
public class PrevencaoConsultaGinecologia extends CommandPrevencaoSaudeConsulta{

	public static final String ESPECIALIDADE_GINECOLOGIA = "Ginecologia";

	@Override
	public boolean geraCoparticipacao(GuiaConsulta guia) {
		
		if(!guia.getEspecialidade().getDescricao().equalsIgnoreCase(ESPECIALIDADE_GINECOLOGIA))
			return true;
		AbstractSegurado segurado = guia.getSegurado();
		
		if(SexoEnum.FEMININO.value() != segurado.getSexo())
			throw new PrevencaoSaudeException(MensagemErroEnumSR.ERRO_SEXO_INVALIDO_PREVENCAO.getMessage());
		
		Calendar cal = new GregorianCalendar();
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("segurado", segurado));
		sa.addParameter(new In("situacao.descricao",situacoes));
		sa.addParameter(new Equals("especialidade.descricao",ESPECIALIDADE_GINECOLOGIA));
		sa.addParameter(new LowerEquals("dataAtendimento", new Date()));
					
		if(idadeValida(segurado.getIdade(), 36, 1000)){
			cal.add(Calendar.MONTH, -6);
			sa.addParameter(new GreaterEquals("dataAtendimento", cal.getTime()));
			int consultasSemCoparticipacao = numeroDeGuiasSemCoparticipacao(sa.list(GuiaConsulta.class));
			if(consultasSemCoparticipacao>=1)
				return true;
			return false;
			
		}
		
		return true;
	}

}
