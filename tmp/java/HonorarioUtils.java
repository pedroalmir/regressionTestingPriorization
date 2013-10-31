package br.com.infowaypi.ecarebc.atendimentos.honorario;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;

public class HonorarioUtils {

	public static Profissional getProfissional(Prestador prestador) {
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("pessoaFisica.cpf", prestador.getPessoaJuridica().getCnpj()));
		return sa.uniqueResult(Profissional.class);
	}
	
}
