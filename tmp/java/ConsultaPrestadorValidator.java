package br.com.infowaypi.ecarebc.atendimentos.validators;


import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaConsulta;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Assert;

/**
 * Classe para validação de Prestador em guias de consulta
 * @author root
 * @changes Danilo Nogueira Portela/ Idelvane
 */
@SuppressWarnings("unchecked")
public class ConsultaPrestadorValidator extends AbstractGuiaValidator<GuiaConsulta> {
 
	public boolean templateValidator(GuiaConsulta guia) throws ValidateException {
		Prestador prestador = guia.getPrestador();
		Boolean isPrestadorNaoNulo = prestador != null;
		Boolean isConsultaOdonto = guia.isConsultaOdonto() || guia.isConsultaOdontoUrgencia();
		
		if(isConsultaOdonto)
			Assert.isTrue(isPrestadorNaoNulo && prestador.isFazOdontologico(), MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE.
					getMessage("CONSULTAS ODONTOLÓGICAS"));
		
		else
			Assert.isTrue(isPrestadorNaoNulo && prestador.isFazConsulta(), MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE.
					getMessage("CONSULTAS"));
		
//		if (isPrestadorNaoNulo && !prestador.isFazConsulta() && !prestador.isFazOdontologico())
//			throw new ValidateException(MensagemErroEnum.PRESTADOR_NAO_HABILITADO_PARA_REALIZAR_ATIVIDADE.getMessage("CONSULTAS"));
		
		return true;
	}

}
