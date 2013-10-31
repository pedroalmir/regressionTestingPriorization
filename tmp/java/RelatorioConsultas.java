package br.com.infowaypi.ecare.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.atendimentos.ResumoConsultasPromocionais;
import br.com.infowaypi.ecarebc.enums.MensagemErroEnum;
import br.com.infowaypi.ecarebc.promocao.PromocaoConsulta;
import br.com.infowaypi.ecarebc.service.Service;
import br.com.infowaypi.molecular.SearchAgent;
import br.com.infowaypi.molecular.parameter.Between;
import br.com.infowaypi.molecular.parameter.Equals;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.utils.Utils;

public class RelatorioConsultas {
	
	/**
	 * @author ThiagoVoiD 
	 * Retorna um <code>ResumoConsultasPromocionais</code> para a seção.
	 * @param dataInicial
	 * @param dataFinal
	 * @param tipo
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ResumoConsultasPromocionais buscarGuias(String dataInicial, String dataFinal, Boolean exibirConsultasPromocionais, Integer tipo) throws Exception{
		
		Date dataInicialFormatada = Utils.parse(dataInicial);
		Date dataFinalFormatada = Utils.parse(dataFinal);
		
		Calendar dataInicalCalendar = Calendar.getInstance();
		Calendar dataFinalCalendar = Calendar.getInstance();
		
		dataInicalCalendar.setTime(dataInicialFormatada);
		dataFinalCalendar.setTime(dataFinalFormatada);
		
		if ((Utils.diferencaEmDias(dataInicalCalendar, dataFinalCalendar)) > Service.LIMITE_DE_DIAS){
			throw new ValidateException(MensagemErroEnum.LIMITE_DIAS_ULTRAPASSADO.getMessage());
		}
		
		SearchAgent sa = new SearchAgent();
		
		sa.addParameter(new Between("dataCriacao", dataInicialFormatada,dataFinalFormatada));
		
		if(tipo.equals(PromocaoConsulta.TIPO_ELETIVA)){
			sa.addParameter(new Equals("tipo", PromocaoConsulta.TIPO_ELETIVA));
		}
		
		if(tipo.equals(PromocaoConsulta.TIPO_URGENCIA)) {
			sa.addParameter(new Equals("tipo", PromocaoConsulta.TIPO_URGENCIA));
		}
		
		List<PromocaoConsulta> consulta = sa.list(PromocaoConsulta.class);
		
		ResumoConsultasPromocionais resumo = new ResumoConsultasPromocionais(consulta);		
		
		return resumo;
	}

}
