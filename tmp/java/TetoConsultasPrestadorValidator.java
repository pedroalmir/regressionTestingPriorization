package br.com.infowaypi.ecarebc.atendimentos.validators;

import java.util.Calendar;
import java.util.Date;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.consumo.periodos.Mes;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;


@SuppressWarnings("rawtypes")
public class TetoConsultasPrestadorValidator extends AbstractGuiaValidator<GuiaSimples>{
	@Override
	protected boolean templateValidator(GuiaSimples guia)throws ValidateException {
		Prestador prestador 	= guia.getPrestador();
		Date dataDeAtendimento 	= guia.getDataAtendimento(); 
		
		if (prestador != null && guia.isConsulta()){
			boolean verificaTetos = prestador.isVerificarTetos();
			boolean tetoEstourou  = prestador.isTetoConsultasEstourado(guia.getValorTotal(), dataDeAtendimento);
			
			if (verificaTetos && tetoEstourou){
				int mesAtual = Calendar.getInstance().get(Calendar.MONTH);
				
				Mes mes = Mes.getMesAtValor(mesAtual);
				throw new ValidateException(MensagemErroEnum.TETO_PRESTADOR_MENSAL_CONSULTAS_ESTOUROU.getMessage(mes.getDescricao()));
			}
		}		
		
		return true;
	}
}
