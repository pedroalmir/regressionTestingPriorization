/**
 * 
 */
package br.com.infowaypi.ecare.services;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.service.SolicitarProcedimentosService;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

/**
 * @author Marcus bOolean
 *
 */
public class SolicitarProcedimentosEspeciais extends SolicitarProcedimentosService {
	
	public GuiaCompleta buscarGuia(String autorizacao, Prestador prestador) throws Exception {
		if(Utils.isStringVazia(autorizacao))
			throw new ValidateException(MensagemErroEnum.AUSENCIA_DE_PARAMETROS.getMessage());
		GuiaCompleta guia =  super.buscarGuias(autorizacao, prestador,false, GuiaCompleta.class, SituacaoEnum.ABERTO,SituacaoEnum.PRORROGADO, SituacaoEnum.SOLICITADO_PRORROGACAO);
		guia.tocarObjetos();
		return guia;
	}
	
	public void salvarGuia(GuiaCompleta guia) throws Exception {
		super.salvarGuia(guia);
	}
}
