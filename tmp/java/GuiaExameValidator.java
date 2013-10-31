package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Classe para validação de guias exame
 * @author Danilo Nogueira Portela
 */
public class GuiaExameValidator<E extends GuiaExame> extends AbstractGuiaValidator<E>{
 
	public boolean templateValidator(E guia)throws ValidateException{
		Profissional solicitante = guia.getSolicitante();
		Boolean isSolicitanteNulo = solicitante == null;
		Boolean isGeradaParaInternacao = guia.getGuiaOrigem() == null? false: guia.getGuiaOrigem().isInternacao();
		
		
		/*if_not[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]*/
		if(!isGeradaParaInternacao || guia.isExigeSolicitante()){
			if(isSolicitanteNulo)
				throw new ValidateException(MensagemErroEnum.SOLICITANTE_NAO_INFORMADO.getMessage());
			
			Boolean isSolicitanteAtivo = solicitante.getSituacao().getDescricao().equals(SituacaoEnum.ATIVO.descricao());
			if(!isSolicitanteAtivo)
				throw new ValidateException(MensagemErroEnum.SOLICITANTE_INATIVO_NO_SISTEMA.getMessage());
		}
		/*end[NAO_OBRIGATORIEDADE_DE_INFORMAR_PROFISSIONAL_NO_ATENDIMENTO]*/
		
		if(!guia.isEspecial())
			if(guia.isExame()){
				Integer maximoProcedimentos = 0;
				maximoProcedimentos = guia.isExameOdonto() ? GuiaExameOdonto.NUMERO_MAXIMO_PROCEDIMENTOS : GuiaExame.NUMERO_MAXIMO_PROCEDIMENTOS;
				if(guia.getPrestador() != null){
					maximoProcedimentos = guia.getPrestador().getIdPrestador().equals(Prestador.HEMOPI)? GuiaExame.NUMERO_MAXIMO_PROCEDIMENTOS_HEMOTERAPIA: maximoProcedimentos;
				}
				
				if(guia.getProcedimentos().size() > maximoProcedimentos)
					throw new ValidateException(MensagemErroEnum.GUIA_COM_PROCEDIMENTOS_DEMAIS.getMessage());
			}
		return true;
	}
}
