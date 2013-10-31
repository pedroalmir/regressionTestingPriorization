package br.com.infowaypi.ecare.validacao.services;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;

/**
 * Classe de valida��o para inclus�o de procedimentos levando em considera��o a situa��o da guia.
 * @author Luciano Infoway
 * @since 19/03/2013
 */
public class InclusaoProcedimentoSituacaoGuiaValidator {
	
	public InclusaoProcedimentoSituacaoGuiaValidator(GuiaSimples guia, boolean isHemopi) {
		validateGuia(guia, isHemopi);
	}
	
	private void validateGuia(GuiaSimples guia, boolean isHemopi){
		if (!isHemopi) {
			if(guia.getSituacao().getDescricao().equals(SituacaoEnum.FECHADO.descricao())) 
				throw new RuntimeException(MensagemErroEnum.IMPOSSIVEL_ADICIONAR_EXAMES_NESTA_GUIA.getMessage("FECHADA"));
			
			if(guia.getSituacao().getDescricao().equals(SituacaoEnum.AUDITADO.descricao())) 
				throw new RuntimeException(MensagemErroEnum.IMPOSSIVEL_ADICIONAR_EXAMES_NESTA_GUIA.getMessage("AUDITADA"));
		}
		
		if(guia.getSituacao().getDescricao().equals(SituacaoEnum.NAO_AUTORIZADO.descricao())) 
			throw new RuntimeException(MensagemErroEnum.IMPOSSIVEL_ADICIONAR_EXAMES_NESTA_GUIA.getMessage("N�O AUTORIZADA"));
		
		if(guia.getSituacao().getDescricao().equals(SituacaoEnum.CANCELADO.descricao())) 
			throw new RuntimeException(MensagemErroEnum.IMPOSSIVEL_ADICIONAR_EXAMES_NESTA_GUIA.getMessage("CANCELADA"));
		
		if(guia.getSituacao().getDescricao().equals(SituacaoEnum.SOLICITADO_INTERNACAO.descricao())) 
			throw new RuntimeException(MensagemErroEnum.IMPOSSIVEL_ADICIONAR_EXAMES_NESTA_GUIA.getMessage("de interna��o SOLICITADA. Para incluir exames nesta guia ela precisa ser AUTORIZADA pelo Auditor"));
		
		if(guia.getSituacao().getDescricao().equals(SituacaoEnum.FATURADA.descricao())) 
			throw new RuntimeException(MensagemErroEnum.IMPOSSIVEL_ADICIONAR_EXAMES_NESTA_GUIA.getMessage("FATURADA"));
		
		if(guia.getSituacao().getDescricao().equals(SituacaoEnum.PAGO.descricao())) 
			throw new RuntimeException(MensagemErroEnum.IMPOSSIVEL_ADICIONAR_EXAMES_NESTA_GUIA.getMessage("PAGA"));
	}
}
