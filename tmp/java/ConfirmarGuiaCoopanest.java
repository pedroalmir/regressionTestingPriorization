/**
 * 
 */
package br.com.infowaypi.ecare.services.exame;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaExame;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.MotivoEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.ImplDAO;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 * Classe service responsável pela confiramçao de guias
 * da coopanest
 */
public class ConfirmarGuiaCoopanest extends Service {
	
	public static final List<String> procedimentosCoopanest = Arrays.asList("90000001","90000002","90000003");
	
	public GuiaSimples<Procedimento> buscarGuiasConfirmacao(String autorizacao) throws Exception {
		if (Utils.isStringVazia(autorizacao.trim()))
			throw new ValidateException(MensagemErroEnum.NUMERO_AUTORIZACAO_INVALIDO.getMessage());
		GuiaExame<Procedimento> guiaEncontrada = super.buscarGuias(autorizacao, GuiaExame.class, null, SituacaoEnum.AUTORIZADO,SituacaoEnum.AGENDADA);
		
		Boolean achouProcedimentoCoopanest = Boolean.FALSE;
		Boolean achouOutroProcedimento = Boolean.FALSE;
		
		if(guiaEncontrada != null) {
			guiaEncontrada.tocarObjetos();
			for (Procedimento procedimento : guiaEncontrada.getProcedimentos()) {
				if(procedimentosCoopanest.contains(procedimento.getProcedimentoDaTabelaCBHPM().getCodigo())){
					achouProcedimentoCoopanest = Boolean.TRUE;
					procedimento.setGeraCoParticipacao(Boolean.FALSE);
				}else{
					achouOutroProcedimento = Boolean.TRUE;
				}
					
			}
			
			if(achouProcedimentoCoopanest && !achouOutroProcedimento) {
				return guiaEncontrada;
			}else {
				throw new RuntimeException(MensagemErroEnum.GUIA_NAO_PODE_SER_CONFIRMADA.getMessage(guiaEncontrada.getAutorizacao()));
			}
		}else {
			throw new RuntimeException(MensagemErroEnum.GUIA_NAO_PODE_SER_CONFIRMADA.getMessage(guiaEncontrada.getAutorizacao()));
		}
		
	}
	
	
	public void salvarGuia(GuiaSimples<Procedimento> guia, UsuarioInterface usuario, Prestador prestador) throws Exception {
		guia.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(), new Date());
		
		for (ProcedimentoInterface procedimento : guia.getProcedimentosNaoCanceladosENegados()) {
			guia.mudarSituacao(usuario, SituacaoEnum.CONFIRMADO.descricao(), MotivoEnum.CONFIRMADA_NO_PRESTADOR.getMessage(), new Date());
		}
		
		guia.setPrestador(prestador);
		prestador.incrementarConsumoFinanceiro(guia);	
		ImplDAO.save(guia);
		ImplDAO.save(guia.getPrestador().getConsumoIndividual());
	}

}
