package br.com.infowaypi.ecare.relatorio;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que tem como objetivo mostrar a exibição dos dados cadastrais do segurado
 * @author Jefferson
 */

public class ExibicaoDadosCadastraisSegurado {
	
	public Segurado exibirDadosSegurado(UsuarioInterface usuario){
		SearchAgent sa  = new SearchAgent();
		sa.addParameter(new Equals("numeroDoCartao",usuario.getLogin()));
		return  (Segurado) sa.list(Segurado.class).get(0); 
	}

}
