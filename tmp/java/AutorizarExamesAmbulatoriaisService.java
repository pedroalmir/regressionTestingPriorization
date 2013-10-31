package br.com.infowaypi.ecare.services.auditor;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecare.enums.MensagemErroEnumSR;
import br.com.infowaypi.ecare.exceptions.AutorizacaoExameException;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.ResumoGuias;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.user.Usuario;
import br.com.infowaypi.msr.utils.Utils;

public class AutorizarExamesAmbulatoriaisService extends Service{

	
	/**
	 * Busca guias de exame que estão solicitada. Deve-se informar pelo menos um parametro para esse método.
	 * @param autorizacao
	 * @param prestador
	 * @return retorna um resumo contendo as guias encontradas
	 */
	public ResumoGuias buscarGuiasParaAutorizacao(String autorizacao) {

		boolean isAutorizacaoInformada = !Utils.isStringVazia(autorizacao);
		
		SearchAgent sa = new SearchAgent();
		sa.addParameter(new Equals("situacao.descricao",SituacaoEnum.SOLICITADO.descricao()));
		
		if(isAutorizacaoInformada)
			sa.addParameter(new Equals("autorizacao",autorizacao));
		
		List<GuiaExame> guias = sa.list(GuiaExame.class);

		return new ResumoGuias(guias, SituacaoEnum.SOLICITADO.descricao(),false);
	}
  
	/**
	 * Muda a situação dos procedimentos para Autorizado(a) caso eles 
	 * @param guia
	 */
	public void autorizarProcedimentos(GuiaExame<ProcedimentoInterface> guia,Usuario usuario) {
		for (ProcedimentoInterface procedimento : guia.getProcedimentos()) {
			if(procedimento.getSelecionado() != null && procedimento.getSelecionado()){
				procedimento.mudarSituacao(usuario, SituacaoEnum.AUTORIZADO.descricao(), MotivoEnum.AUTORIZADO_PELO_AUDITOR.getMessage(), new Date());
			}else{
				if(procedimento.isSituacaoAtual(SituacaoEnum.SOLICITADO.descricao()) && Utils.isStringVazia(procedimento.getMotivo()))
					throw new AutorizacaoExameException(MensagemErroEnumSR.MOTIVO_REQUERIDO.getMessage(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo()));
				
				procedimento.mudarSituacao(usuario, SituacaoEnum.NAO_AUTORIZADO.descricao(), procedimento.getMotivo(), new Date());
			}
		}
		
		
		
	}
	
	
}
