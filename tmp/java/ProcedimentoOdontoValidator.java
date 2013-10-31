package br.com.infowaypi.ecarebc.procedimentos.validators;

import java.util.Set;

import br.com.infowaypi.ecarebc.atendimentos.GuiaExameOdonto;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.odonto.Dente;
import br.com.infowaypi.ecarebc.odonto.EstruturaOdonto;
import br.com.infowaypi.ecarebc.odonto.enums.EstruturaOdontoEnum;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoOdonto;
import br.com.infowaypi.ecarebc.procedimentos.TabelaCBHPM;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validação básica de procedimentos odontológicos.
 * @author Danilo Nogueira Portela
 */
public class ProcedimentoOdontoValidator extends AbstractProcedimentoValidator<ProcedimentoOdonto, GuiaExameOdonto> {

	@Override
	protected boolean templateValidator(ProcedimentoOdonto proc, GuiaExameOdonto guia) throws Exception {
		TabelaCBHPM cbhpm = proc.getProcedimentoDaTabelaCBHPM();
		Integer elementoAplicado = cbhpm.getElementoAplicado();
		
		Boolean APLICACAO_DENTE = elementoAplicado.equals(EstruturaOdontoEnum.DENTE.getValor());
		Boolean APLICACAO_FACE = elementoAplicado.equals(EstruturaOdontoEnum.FACE.getValor());
		
		Assert.isFalse(proc.getEstruturas().isEmpty() || proc.getEstruturas() == null, MensagemErroEnum.PROCEDIMENTO_ODONTO_SEM_ESTRUTURAS.
				getMessage(cbhpm.getCodigoEDescricao()));
		
		Integer numEstruturas = proc.getEstruturas().size();
		Integer quantPermitida = cbhpm.getQuantidade();
		
		Assert.isTrue(numEstruturas <= quantPermitida, MensagemErroEnum.PROCEDIMENTO_QUANTIDADE_ESTRUTURAS_INVALIDA.
				getMessage(cbhpm.getCodigoEDescricao(), quantPermitida.toString(), EstruturaOdontoEnum.getElemento(cbhpm.getElementoAplicado())));
		
		//Validando aplicação de mesmo tipo de elemento
//		Quadrante quadrante = null;
		Dente dente = null;
		for (EstruturaOdonto e : (Set<EstruturaOdonto>)proc.getEstruturas()) {
//			if(APLICACAO_DENTE){
//				if(quadrante == null)
//					quadrante = e.getQuadrante();
//				
//				else{
//					Assert.isEquals(quadrante, e.getQuadrante(), MensagemErroEnum.PROCEDIMENTO_COM_ESTRUTURAS_INVALIDAS.getMessage(cbhpm.getCodigoEDescricao(), "QUADRANTE"));
//					quadrante = null;
//				}
//			}
				
			if(APLICACAO_FACE){
				if(dente == null)
					dente = e.getDente();
				
				else{
					Assert.isEquals(dente, e.getDente(), MensagemErroEnum.PROCEDIMENTO_COM_ESTRUTURAS_INVALIDAS.getMessage(cbhpm.getCodigoEDescricao(), "ELEMENTO"));
					dente = null;
				}
			}	
		}
		
		return false;
	}

}
