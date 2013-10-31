package br.com.infowaypi.ecare.services.financeiro.faturamento.ordenador;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.financeiro.faturamento.ordenador.Ordenador;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

@SuppressWarnings("unchecked")
public class RelatorioVisualizarImprimirOrdenadorService {

	public ResumoOrdenadores buscarOrdenadores(Integer identificador, Date competencia) throws ValidateException{
		boolean informouId = identificador != null;
		boolean informouCompetencia = competencia != null;
		SearchAgent sa = new SearchAgent();

		if (!informouId && !informouCompetencia){
			throw new ValidateException("Informe pelo menos um parâmetro");
		}
		
		if (informouId) {
			sa.addParameter(new Equals("identificador", identificador));
		} else 	if (informouCompetencia) {
			sa.addParameter(new Equals("competencia", competencia));
		}
		
		List ordenadores = sa.list(Ordenador.class);
		
		if (ordenadores.isEmpty()) {
			throw new ValidateException("Não foi possível encontrar nenhum ordenador com as informações fornecidas");
		}
		
		ResumoOrdenadores resumoOrdenadores = new ResumoOrdenadores(ordenadores);
		return resumoOrdenadores;
	}
	
	public Ordenador selecionarOrdenador(Ordenador ordenador) throws Exception {
		Assert.isNotNull(ordenador, "Selecione o ordenador a ser visualizado/impresso");
		return ordenador;
	}
}
