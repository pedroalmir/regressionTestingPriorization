/**
 * 
 */
package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.ArrayList;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsultaOdonto;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * @author Idelvane
 * @changes Danilo Nogueira Portela,Jefferson Gonçalves de Oliveira, Leonardo Sampaio
 */
@SuppressWarnings("unchecked")
public class ConsultaOrigemValidator extends AbstractGuiaValidator<GuiaSimples>{

	@Override
	protected boolean templateValidator(GuiaSimples guia) throws ValidateException {
		
		GuiaSimples<Procedimento> guiaOrigem = guia.getGuiaOrigem();

		if(guiaOrigem != null && (guiaOrigem.isInternacao() || guiaOrigem.isUrgencia()))
			return true;
		
		
		boolean isValidaGuiaConsultaOrigem = isValidaGuiaConsultaOrigem(guia);
		
		if(isValidaGuiaConsultaOrigem){
			List<GuiaSimples> consultasOrigem = new ArrayList<GuiaSimples>();
			
			//não considerar profissional da guia TratamentoOdonto
			consultasOrigem.addAll(guia.buscarGuiaOrigem(GuiaConsultaOdonto.class, false, 90)); //FIXME remover constante
			consultasOrigem.addAll(guia.buscarGuiaOrigem(GuiaConsulta.class, false, GuiaSimples.PERIODO_VALIDADE_CONSULTA_PARA_SOLICITACAO_EXAME));
			consultasOrigem.add(guia.getGuiaOrigem());
				
			Assert.isNotEmpty(consultasOrigem, MensagemErroEnum.GUIA_SEM_CONSULTA_ORIGEM.getMessage(String.valueOf(60)));
		}
		
		return true;
	}
	
	private boolean isValidaGuiaConsultaOrigem(GuiaSimples guia){
		if (guia.isExameOdonto())
			return true;
		else
			return guia.getSituacao().getUsuario().isPrestador();
	}
}
