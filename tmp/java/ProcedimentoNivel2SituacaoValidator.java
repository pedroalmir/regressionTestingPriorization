package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Iterator;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * Este validator verifica as guia de interna��o quanto aos seus procedimentos.
 * O validator verifica se existem  guias de interna��o com procedimentos de n�vel 2 com a situa��o de solicitado.
 * Caso exista uma guia de interna��o nessas condi��es, o validator dispara uma exce��o.
 * @author jefferson
 * @changes Luciano Rocha 
 * Criei uma valida��o tamb�m para o Registrar Alta/Evolu��o para evitar que o prestador registre alta sem que o
 * procedimento de n�vel 2 com situa��o Solicitado(a) seja regulado. Isso evita que o prestador n�o consiga fechar 
 * a guia.
 * @since 20/01/2013
 */

public class ProcedimentoNivel2SituacaoValidator extends AbstractGuiaValidator<GuiaSimples>{

	@Override
	protected boolean templateValidator(GuiaSimples guia) throws Exception {
		
		if (guia.isInternacao()){
			Iterator iterator = guia.getProcedimentos().iterator();
			boolean registrandoAlta = guia.getSituacao(SituacaoEnum.ALTA_REGISTRADA.descricao()) == null;
			while (iterator.hasNext()) {
				Procedimento proc = (Procedimento) iterator.next();
				boolean isProcedimentoSolicitado = proc.getSituacao().getDescricao().equals(SituacaoEnum.SOLICITADO.descricao());
				boolean isProcedimentoNivel2 =  proc.getProcedimentoDaTabelaCBHPM().getNivel()==2;
				if(isProcedimentoSolicitado  && isProcedimentoNivel2){
					if (!registrandoAlta) {
						throw new ValidateException(MensagemErroEnum.ERRO_NO_PROCEDIMENTO_AO_FECHAR_GUIA.getMessage());
					}
					else {
						throw new ValidateException(MensagemErroEnum.ERRO_NO_PROCEDIMENTO_AO_REGISTRAR_ALTA.getMessage());
					}
				}
			}
		}
		
		return true;
	}
}
