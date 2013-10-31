package br.com.infowaypi.ecare.services.recurso.relatorio;

import java.util.List;

import br.com.infowaypi.ecare.services.recurso.GuiaRecursoGlosa;
import br.com.infowaypi.ecare.services.recurso.ManagerBuscaGRG;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Classe que representa o acompanhamento das Guias de Recurso de Glosa(GRG)
 * @author Luciano Rocha
 * @since 23/11/2012
 */
public class RelatorioAcompanhamentoRecursoGlosa {
	
	public ResumoAcompanhamentoRecursoGlosa gerarRelatorio(String autorizacao, String dataInicial, String dataFinal, Prestador prestador, String situacao, UsuarioInterface usuario) throws Exception {
			
		List<GuiaRecursoGlosa> guias = ManagerBuscaGRG.buscaGuiasRecursoGlosas(autorizacao, dataInicial, dataFinal, prestador, situacao, usuario);
		
		validateGuiasSituacao(guias, situacao);
		
		ResumoAcompanhamentoRecursoGlosa resumo;
		resumo = new ResumoAcompanhamentoRecursoGlosa(guias);
		
		return resumo;
	}
	
	/**
	 * Verifica se os recursos filtrados possuem algum item de recurso na situação passada como parâmetro na busca.
	 * @author Luciano Rocha
	 * @param recursos
	 * @param situacao
	 * @throws ValidateException
	 */
	private void validateGuiasSituacao(List<GuiaRecursoGlosa> recursos, String situacao) throws ValidateException {
		if (recursos == null || recursos.size() < 1) {
			throw new ValidateException("Nenhum recurso foi encontrado na situação " + situacao + " para os parâmetros passados.");
		}
	}
}
