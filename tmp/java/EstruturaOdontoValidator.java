package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.odonto.Arcada;
import br.com.infowaypi.ecarebc.odonto.Dente;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.odonto.Face;
import br.com.infowaypi.ecarebc.odonto.Quadrante;
import br.com.infowaypi.ecarebc.odonto.enums.DenticaoEnum;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validação básica de estruturas odontológicas
 * @author <a href="mailto:dnportela@gmail.com">Danilo Nogueira Portela</a>
 */
public class EstruturaOdontoValidator extends AbstractEstruturaOdontoValidator {

	@Override
	protected boolean templateValidator(EstruturaOdonto estrutura, ProcedimentoOdonto proc) throws Exception {
		Boolean isArcadaNula = estrutura.getArcada() == null;
		Boolean isQuadranteNulo = estrutura.getQuadrante() == null;
		Boolean isDenteNulo = estrutura.getDente() == null;
		Boolean isFaceNula = estrutura.getFace() == null;
		
		Assert.isNotNull(estrutura.getDenticao(), MensagemErroEnum.ESTRUTURA_NAO_INFORMADA.getMessage(EstruturaOdontoEnum.DENTICAO.getDescricao(), ""));
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("descricao",DenticaoEnum.TODOS.getDescricao()));
		
		if(isArcadaNula){
			Arcada arcadaTodas = (Arcada)sa.list(Arcada.class).get(0);
			estrutura.setArcada(arcadaTodas);
		}
		
		if(isQuadranteNulo){
			Quadrante quadranteTodos = (Quadrante) sa.list(Quadrante.class).get(0);
			estrutura.setQuadrante(quadranteTodos);
		}
		
		if(isDenteNulo){
			Dente denteTodos = (Dente) sa.list(Dente.class).get(0);
			estrutura.setDente(denteTodos);
		}
		
		if(isFaceNula){
			Face faceTodos = (Face) sa.list(Face.class).get(0);
			estrutura.setFace(faceTodos);
		}
		
		return true;
	}

}
