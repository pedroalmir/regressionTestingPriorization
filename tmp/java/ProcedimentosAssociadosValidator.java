package br.com.infowaypi.ecarebc.procedimentos.validators;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.procedimentos.Procedimento;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe que verifica se os procedimentos solicitados na guia podem ser realizados pelo prestador da sessão.
 * 
 * <br>
 * Exemplo de uso:
 * 
 * <pre>
 * 
 *    
 * </pre>
 * 
 * <br>
 * Limitações:
 * <ul>
 * 	<li> Valida apenas os procedimetos que o hospital está habilitado a realizar,
 * deixando de lado se o profissional atende ou não a especialidade do procedimento.</li>
 * </ul>
 * 
 * @author patricia
 * @version
 * @see br.com.infowaypi.ecarebc.procedimentos.Procedimento
 */
public class ProcedimentosAssociadosValidator extends AbstractProcedimentoValidator<Procedimento, GuiaSimples> {

	@Override
	protected boolean templateValidator(Procedimento proc, GuiaSimples guia) throws Exception {
		boolean isProcedimentoAssociadoAoPrestador = true;
		
		if(guia.getPrestador()!=null && guia.getPrestador().isVerificaProcedimentosAssociados()){
			isProcedimentoAssociadoAoPrestador = guia.getPrestador().getProcedimentos().contains(proc.getProcedimentoDaTabelaCBHPM());
		}

		String mensagem = " o procedimento \""+proc.getProcedimentoDaTabelaCBHPM().getCodigoEDescricao()+"\"";
		Assert.isTrue(isProcedimentoAssociadoAoPrestador, MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE.getMessage(mensagem));
		
		return true;
	}

}
