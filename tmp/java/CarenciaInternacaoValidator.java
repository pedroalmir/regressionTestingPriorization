package br.com.infowaypi.ecarebc.atendimentos.validators;


import java.util.Date;
import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaCompleta;
import br.com.infowaypi.ecarebc.atendimentos.acordos.itensAcordos.ItemDiaria;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.enums.SituacaoEnum;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe para valida��o de car�ncia em guias do sistema
 * @author Danilo Nogueira Portela
 * @chages Dannylvan
 * 
 */
public class CarenciaInternacaoValidator extends AbstractGuiaValidator<GuiaCompleta> {
 
	public boolean templateValidator(GuiaCompleta guia)throws ValidateException{
		AbstractSegurado segurado = guia.getSegurado();
		
		
		if(guia.isInternacao() && (!segurado.isCumpriuCarencia(180))){
			
			Date dataFimCarencia = Utils.incrementaDias(segurado.getInicioDaCarencia(), 180);
			
			String msgErro = MensagemErroEnum.BENEFICIARIO_NAO_CUMPRIU_CARENCIA_INTERNACAO.
					getMessage("INTERNA��ES", Utils.format(dataFimCarencia));
			
			if(guia.isSituacaoAtual(SituacaoEnum.SOLICITADO_PRORROGACAO.descricao())){
				throw new ValidateException(msgErro + " Portanto, n�o � poss�vel prorrogar este atendimento.");
			}
			
			if (guia.getPrestador().isPrestadorSemSalaObservacao()){
				for (ItemDiaria diaria : (Set<ItemDiaria>) guia.getItensDiariaSolicitados()){
					Assert.isEquals(diaria.getValor().getQuantidade(), new Integer(1), msgErro + " S�  � poss�vel solicitar uma di�ria para fins de observa��o.");
				}
			} else {
				throw new ValidateException(msgErro);
			}
		}
		
		return true;
	}
	
	
}
