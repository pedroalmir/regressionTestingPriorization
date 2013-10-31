package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Date;

import br.com.infowaypi.ecare.enums.TipoLiberacaoForaLimiteEnum;
import br.com.infowaypi.ecare.manager.SeguradoManager;
import br.com.infowaypi.ecarebc.atendimentos.Critica;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.Role;
import br.com.infowaypi.ecarebc.exceptions.ConsumoException;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;

/**
 * Classe para validação de limites de utilização do segurado no sistema
 * @author Danilo Nogueira Portela
 */
@SuppressWarnings("unchecked")
public class ConsumoSeguradoValidator extends AbstractGuiaValidator<GuiaSimples> {
 
	public boolean templateValidator(GuiaSimples guia) throws Exception {
		AbstractSegurado segurado = guia.getSegurado();
		Boolean isUsuarioAuditor = guia.getSituacao().getUsuario().isPossuiRole(Role.AUDITOR.getValor(),Role.DIGITADOR.getValor());
		boolean isUsuarioCentral = guia.getSituacao().getUsuario().isPossuiRole(Role.CENTRAL_DE_SERVICOS.getValor());
		
		/**
		 * Verificação para tratar o beneficiário e profissional, se estão ambos no PPS.
		 * @author Luciano Rocha
		 * @since 11/10/2012
		 */
		Integer tipoLiberacao = SeguradoManager.pegaTipoDeLiberacao(segurado, guia.getProfissional());
		boolean isPPS = tipoLiberacao == TipoLiberacaoForaLimiteEnum.LIBERACAO_MPPS.codigo();
		
		//Retirar depois
		if (guia.isTratamentoSeriado()) {
			return true;
		}
		if(guia.isRegulaConsumo()){
			if(!isUsuarioAuditor && !isUsuarioCentral && !isPPS){
				if (guia.isConsulta()){
					segurado.temLimite(guia.getDataAtendimento(), guia);
				} else {
					segurado.temLimite(new Date(), guia);
				}
			}else{
				try {
					segurado.temLimite(new Date(), guia);
				} catch (ConsumoException e) {
					guia.setMensagemLimite(e.getMessage());
					Critica critica = new Critica();
					critica.setMensagem(e.getMessage());
					critica.setGuia(guia);
					guia.getCriticas().add(critica);
					
					if(isUsuarioAuditor || isUsuarioCentral) {
						tipoLiberacao = TipoLiberacaoForaLimiteEnum.LIBERACAO_AUDITOR.codigo();
					}
					
					guia.setLiberadaForaDoLimite(tipoLiberacao);
					SeguradoManager.contabilizaLiberacoesForaDoLimite(segurado, tipoLiberacao);
				}
			}
		}

		return true;
	}
}
