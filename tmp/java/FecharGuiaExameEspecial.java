/**
 * 
 */
package br.com.infowaypi.ecare.services.exame;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.validators.fechamento.FechamentoDataLimiteValidator;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.service.exames.FecharGuiaExameEspecialService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Service para fechamento de guias de exame com inserção de materiais e medicamentos
 * @author Marcus bOolean
 * @changes Danilo Nogueira Portela
 */
public class FecharGuiaExameEspecial extends FecharGuiaExameEspecialService{
	
	public static final int LIMITE_DIAS_FECHAMENTO = 60;
	
	public GuiaExame buscarGuiasFechamento(String autorizacao, Prestador prestador) throws Exception{
		if (Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		GuiaExame<Procedimento> guia = super.buscarGuias(autorizacao, prestador,false, GuiaExame.class);

		boolean isGuiaAberta = guia.isSituacaoAtual(SituacaoEnum.ABERTO.descricao());
		boolean isGuiaFechada = guia.isSituacaoAtual(SituacaoEnum.FECHADO.descricao());
		
		if(!isGuiaAberta && !isGuiaFechada){ 
			throw new ValidateException(MensagemErroEnum.GUIA_NAO_CONFIRMADA_PARA_FECHAMENTO.getMessage());
		}
		Assert.isNotNull(guia, MensagemErroEnum.GUIA_NAO_ENCONTRADA.getMessage());
		guia.tocarObjetos();
		
		FechamentoDataLimiteValidator.execute(guia);
		
		Boolean permiteFechamento = false;
		for (Procedimento procedimento : guia.getProcedimentos()) {
			if(procedimento.getProcedimentoDaTabelaCBHPM().isPermiteMaterialComplementar() || procedimento.getProcedimentoDaTabelaCBHPM().isPermiteMedicamentoComplementar()){
				permiteFechamento = true;
			}	
		}
		if(!permiteFechamento){
			throw new RuntimeException("Esta guia não possui nenhum procedimento que permita seu fechamento.");
		}
		
		return guia;
	}
	
}
