import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.financeiro.faturamento.GuiaFaturavel;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.molecular.parameter.In;
import br.com.infowaypi.molecular.parameter.NotIn;


/**
 * Classe responsável por realizar as buscas mais comuns nos Fluxos do iHealth.
 * @author Eduardo Vera Sousa
 *
 */
public class IHealthSearch {

	/**
	 * Busca uma Guia pela Autorização podendo ter a situação como parâmetro adicional.
	 * Caso o prestador seja nulo, o método retornará as guia independentemente do prestador.
	 * Caso contrário, retornará apenas a guia com a autorização e prestador.
	 * O parâmetro <b>naSituação</b> indica se a guia deve ou não estar nas situações seguintes. 
	 * Preencha esse parâmetro com <b>null</b> para retornar a guia independente de sua situação.
	 * @param tipoDeGuia
	 * @param autorizacao
	 * @param naSituacao
	 * @param situacao
	 * @return <G extends tipoDeGuia>
	 */
	public static <G extends GuiaFaturavel> G getGuiaByAutorizacao(Class tipoDeGuia, String autorizacao, Prestador prestador, Boolean naSituacao, SituacaoEnum... situacao) {

		SearchAgent sa = new SearchAgent();
		List<String> situacoes = new ArrayList<String>();
		sa.addParameter(new Equals("autorizacao", autorizacao));
		
		for (int i = 0; i < situacao.length; i++) {
			situacoes.add(situacao[i].descricao());
		}
		if (naSituacao != null) {
			if (naSituacao) {
				sa.addParameter(new In("situacao.descricao", situacoes));
			} else {
				sa.addParameter(new NotIn("situacao.descricao", situacoes));
			}			
		}
		
		return (G) sa.uniqueResult(tipoDeGuia);
	}

	/**
	 * Busca todas as guias pertencentes a um segurado podendo ter a situação como parâmetro adicional.
	 * Caso o prestador seja nulo, o método retornará as guia independentemente do prestador.
	 * Caso contrário, retornará apenas a guia com a autorização e prestador.
	 * O parâmetro <b>naSituação</b> indica se a guia deve ou não estar nas situações seguintes. 
	 * Preencha esse parâmetro com <b>null</b> para retornar a guia independente de sua situação.
	 * @param tipoDeGuia
	 * @param segurado
	 * @param prestador
	 * @param naSituacao
	 * @param situacao
	 * @return
	 */
	
	public static <G extends GuiaFaturavel> G getGuiaBySegurado(Class tipoDeGuia, Segurado segurado, Prestador prestador, Boolean naSituacao, SituacaoEnum... situacao) {

		SearchAgent sa = new SearchAgent();
		List<String> situacoes = new ArrayList<String>();
		sa.addParameter(new Equals("segurado", segurado));
		
		for (int i = 0; i < situacao.length; i++) {
			situacoes.add(situacao[i].descricao());
		}
		if (naSituacao != null) {
			if (naSituacao) {
				sa.addParameter(new In("situacao.descricao", situacoes));
			} else {
				sa.addParameter(new NotIn("situacao.descricao", situacoes));
			}			
		}
		
		return (G) sa.uniqueResult(tipoDeGuia);
	}

	
	/**
	 * Busca um segurado pelo CPF podendo ter a situação como parâmetro adicional.
	 * O parâmetro <b>naSituação</b> indica se o segurado deve ou não estar nas situações seguintes. 
	 * Preencha esse parâmetro com <b>null</b> para retornar o segurado independente de sua situação.
	 * @param cpf
	 * @param naSituacao
	 * @param situacao
	 * @return AbstractSegurados
	 */
	public static Segurado getSeguradoByCPF(String cpf, Boolean naSituacao, SituacaoEnum... situacao) {
		
		SearchAgent sa = new SearchAgent();
		List<String> situacoes = new ArrayList<String>();
		
		sa.addParameter(new Equals("pessoaFisica.cpf", cpf));
		
		for (int i = 0; i < situacao.length; i++) {
			situacoes.add(situacao[i].descricao());
		}
		if (naSituacao != null) {
			if (naSituacao) {
				sa.addParameter(new In("situacao.descricao", situacoes));
			} else {
				sa.addParameter(new NotIn("situacao.descricao", situacoes));
			}			
		}
		return sa.uniqueResult(Segurado.class);
	}
	
	/**
	 * Busca um segurado pelo número do cartão podendo ter a situação como parâmetro adicional.
	 * O parâmetro <b>naSituação</b> indica se o segurado deve ou não estar nas situações seguintes. 
	 * Preencha esse parâmetro com <b>null</b> para retornar o segurado independente de sua situação.
	 * @param cpf
	 * @param naSituacao
	 * @param situacao
	 * @return AbstractSegurados
	 */
	public static Segurado getSeguradoByNumeroDoCartao(String numeroDoCartao, Boolean naSituacao, SituacaoEnum... situacao) {
		
		SearchAgent sa = new SearchAgent();
		List<String> situacoes = new ArrayList<String>();
		
		sa.addParameter(new Equals("numeroDoCartao", numeroDoCartao));
		
		for (int i = 0; i < situacao.length; i++) {
			situacoes.add(situacao[i].descricao());
		}
		if (naSituacao != null) {
			if (naSituacao) {
				sa.addParameter(new In("situacao.descricao", situacoes));
			} else {
				sa.addParameter(new NotIn("situacao.descricao", situacoes));
			}			
		}
		return sa.uniqueResult(Segurado.class);
	}
	
}