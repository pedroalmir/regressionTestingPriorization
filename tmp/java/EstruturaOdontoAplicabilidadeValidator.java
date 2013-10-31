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
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validação de aplicação de um procedimento a uma estrutura odontológica.
 * <b>Aplicabilidade</b> é um tipo de validação do módulo e-Care que valida a aplicação de procedimentos a cada tipo de estrutura odontológica. 
 * @author Danilo Nogueira Portela
 */
public class EstruturaOdontoAplicabilidadeValidator extends AbstractEstruturaOdontoValidator{
 
	public boolean templateValidator(EstruturaOdonto estrutura, ProcedimentoOdonto proc) throws ValidateException{
		Integer elementoAplicado = proc.getProcedimentoDaTabelaCBHPM().getElementoAplicado();
		
		if(!elementoAplicado.equals(EstruturaOdontoEnum.NENHUM.getValor())){
			Boolean APLICACAO_DENTICAO = elementoAplicado.equals(EstruturaOdontoEnum.DENTICAO.getValor());
			Boolean APLICACAO_ARCADA = elementoAplicado.equals(EstruturaOdontoEnum.ARCADA.getValor());
			Boolean APLICACAO_QUADRANTE = elementoAplicado.equals(EstruturaOdontoEnum.QUADRANTE.getValor());
			Boolean APLICACAO_DENTE = elementoAplicado.equals(EstruturaOdontoEnum.DENTE.getValor());
			Boolean APLICACAO_FACE = elementoAplicado.equals(EstruturaOdontoEnum.FACE.getValor());
			
			if(APLICACAO_DENTICAO || APLICACAO_ARCADA || APLICACAO_QUADRANTE || APLICACAO_DENTE || APLICACAO_FACE)
				Assert.isTrue(estrutura.isPossuiDenticao(), MensagemErroEnum.ESTRUTURA_NAO_INFORMADA.getMessage(
						EstruturaOdontoEnum.DENTICAO.getDescricao().toUpperCase(), proc.getProcedimentoDaTabelaCBHPM().getCodigo()));
				
			if(APLICACAO_ARCADA || APLICACAO_QUADRANTE || APLICACAO_DENTE || APLICACAO_FACE)
				Assert.isTrue(estrutura.isPossuiArcada(), MensagemErroEnum.ESTRUTURA_NAO_INFORMADA.getMessage(
						EstruturaOdontoEnum.ARCADA.getDescricao().toUpperCase(), proc.getProcedimentoDaTabelaCBHPM().getCodigo()));
			
			if(APLICACAO_QUADRANTE || APLICACAO_DENTE || APLICACAO_FACE)
				Assert.isTrue(estrutura.isPossuiQuadrante(), MensagemErroEnum.ESTRUTURA_NAO_INFORMADA.getMessage(
						EstruturaOdontoEnum.QUADRANTE.getDescricao().toUpperCase(), proc.getProcedimentoDaTabelaCBHPM().getCodigo()));
			
			if(APLICACAO_DENTE || APLICACAO_FACE)
				Assert.isTrue(estrutura.isPossuiDente(), MensagemErroEnum.ESTRUTURA_NAO_INFORMADA.getMessage(
						EstruturaOdontoEnum.DENTE.getDescricao().toUpperCase(), proc.getProcedimentoDaTabelaCBHPM().getCodigo()));
			
			if(APLICACAO_FACE )
				Assert.isTrue(estrutura.isPossuiFace(), MensagemErroEnum.ESTRUTURA_NAO_INFORMADA.getMessage(
						EstruturaOdontoEnum.FACE.getDescricao().toUpperCase(), proc.getProcedimentoDaTabelaCBHPM().getCodigo()));
		
			SearchAgent sa = new SearchAgent();
			sa.addParameter(new Equals("descricao",DenticaoEnum.TODOS.getDescricao()));
			
			Arcada arcadaTodas = (Arcada) sa.list(Arcada.class).get(0);
			Quadrante quadranteTodos = (Quadrante) sa.list(Quadrante.class).get(0);
			Dente denteTodos = (Dente) sa.list(Dente.class).get(0);
			Face faceTodos = (Face) sa.list(Face.class).get(0);
			
			if(APLICACAO_DENTICAO){
				estrutura.setArcada(arcadaTodas);
				estrutura.setQuadrante(quadranteTodos);
				estrutura.setDente(denteTodos);
				estrutura.setFace(faceTodos);
			}
			if(APLICACAO_ARCADA){
				estrutura.setQuadrante(quadranteTodos);
				estrutura.setDente(denteTodos);
				estrutura.setFace(faceTodos);
			}
			
			if(APLICACAO_QUADRANTE){
				estrutura.setDente(denteTodos);
				estrutura.setFace(faceTodos);
			}
			
			if(APLICACAO_DENTE)
				estrutura.setFace(faceTodos);
			
		}
		return true;
	}
	
}
