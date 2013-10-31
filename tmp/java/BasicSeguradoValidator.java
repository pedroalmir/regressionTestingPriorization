package br.com.infowaypi.ecarebc.atendimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.situations.SituacaoInterface;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validações básicas de Segurados em guias do sistema.
 * @author root
 * @changes Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class BasicSeguradoValidator extends AbstractGuiaValidator<GuiaSimples> {
 
	public boolean templateValidator(GuiaSimples guia) throws Exception {
		AbstractSegurado segurado = guia.getSegurado();
		
		Boolean isSeguradoNulo = segurado == null;
		Assert.isFalse(isSeguradoNulo, MensagemErroEnum.SEGURADO_NAO_INFORMADO.getMessage());
		
		Boolean isSeguradoAtivo = segurado.getSituacao().getDescricao().equals(SituacaoEnum.ATIVO.descricao());
		boolean isSeguradoSuspenso = segurado.isSituacaoAtual(SituacaoEnum.SUSPENSO.descricao());
		
		/* if[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]
		isSeguradoAtivo = segurado.getContratoAtual().getSituacao().getDescricao().equals(SituacaoEnum.ATIVO.descricao());
		Assert.isTrue(isSeguradoAtivo, MensagemErroEnum.SEGURADO_INATIVO_NO_SISTEMA.getMessage());
		else[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO] */
		
		if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia() || guia.isExameExterno()){
			Assert.isTrue(isSeguradoAtivo || isSeguradoSuspenso, MensagemErroEnum.SEGURADO_INATIVO_NO_SISTEMA.getMessage());
		}else{
			Assert.isTrue(isSeguradoAtivo, MensagemErroEnum.SEGURADO_INATIVO_NO_SISTEMA.getMessage());
		}
		/* end[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]*/
		
		/* if_not[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO] */ 
		
		Boolean isBeneficiario = segurado.isBeneficiario();
		Assert.isTrue(isBeneficiario, MensagemErroEnum.SEGURADO_NAO_BENEFICIARIO.getMessage());
		
		Boolean isDependente = segurado.isSeguradoDependente();
		if(isDependente){
			SituacaoInterface situacaoDoTitular = segurado.getTitular().getSituacao();
			Boolean isTitularAtivo = situacaoDoTitular.getDescricao().equals(SituacaoEnum.ATIVO.descricao());
			boolean isTitularSuspenso = situacaoDoTitular.getDescricao().equals(SituacaoEnum.SUSPENSO.descricao());
			
			if(guia.isConsultaUrgencia() || guia.isAtendimentoUrgencia()){
				Assert.isTrue(isTitularAtivo || isTitularSuspenso, MensagemErroEnum.SEGURADO_COM_TITULAR_INATIVO_NO_SISTEMA.getMessage());
			}else{
				Assert.isTrue(isTitularAtivo, MensagemErroEnum.SEGURADO_COM_TITULAR_INATIVO_NO_SISTEMA.getMessage());
			}
			
		}
		
		/* end[ATENDIMENTO_BASEADO_NA_SITUACAO_CONTRATO]*/
		
		return true;
	}
	
}
