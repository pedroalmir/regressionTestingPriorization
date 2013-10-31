package br.com.infowaypi.ecarebc.utils;

import java.util.Calendar;
import java.util.Date;

import br.com.infowaypi.msr.utils.Feriado;
import br.com.infowaypi.msr.utils.Utils;

public final class DateUtils {

	private static final int PRIMEIRO_DIA_MES 	= 1;
	private static final int ULTIMO_DIA_MES_ANO = 31;

	private static Integer HORA_INICIO = 8;
	private static Integer HORA_FIM = 18;
	
	private DateUtils() throws InstantiationException{
		throw new InstantiationException();
	}

	public static Date getDataFinalAno(Date data){
		Calendar calendar = manipulaData(data, ULTIMO_DIA_MES_ANO, Calendar.DECEMBER);

		return calendar.getTime();
	}

	public static Date getDataInicialAno(Date data){
		Calendar calendar = manipulaData(data, PRIMEIRO_DIA_MES, Calendar.JANUARY);

		return calendar.getTime();
	}

	private static Calendar manipulaData(Date data, int diaMes, int mes) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.DAY_OF_MONTH, diaMes);
		calendar.set(Calendar.MONTH, mes);
		return calendar;
	}
	
	public static int getDiferencaHorasUteis(Date dataInicio, Date dataFim){
		
		if(Utils.compareData(dataInicio, dataFim) == 0){
			dataInicio = normalizarDataInicio(dataInicio);
			dataFim = normalizarDataFim(dataFim);
			
			Calendar c1 = Calendar.getInstance();
			c1.setTime(dataInicio);
			
			Calendar c2 = Calendar.getInstance();
			c2.setTime(dataFim);
			
			return Utils.diferencaEmHoras(c1, c2);
		}
		
		int quantidade = getQuantidadeDiasUteis(dataInicio, dataFim);
		quantidade = quantidade * (HORA_FIM - HORA_INICIO);
		
		int diferencaEmHoras = getHorasPassadaInicio(dataInicio, HORA_FIM);
		quantidade += diferencaEmHoras;
		
		diferencaEmHoras = getHorasPassadaFim(dataFim, HORA_INICIO);
		quantidade += diferencaEmHoras;
		
		return quantidade;
	}

	private static int getHorasPassadaFim(Date data, Integer faixa) {
		
		data = normalizarDataFim(data);
		
		if(!Feriado.isDiaUtil(data)){
			return 0;
		}
		
		Calendar fim = Calendar.getInstance();
		fim.setTime(data);
		fim.set(Calendar.HOUR_OF_DAY, faixa);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		
		int diferencaEmHoras = Utils.diferencaEmHoras(fim, calendar);
		
		if(diferencaEmHoras < 0){
			return 0;
		}
		
		return diferencaEmHoras;
	}

	private static Date normalizarDataFim(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.HOUR_OF_DAY, HORA_FIM);
		
		if(Utils.compareDataHora(calendar.getTime(), data) < 0){
			return calendar.getTime();
		}
		
		return data;
	}

	private static int getHorasPassadaInicio(Date data, Integer faixa) {
		
		data = normalizarDataInicio(data);
		
		if(!Feriado.isDiaUtil(data)){
			return 0;
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.HOUR_OF_DAY, faixa);
		
		Calendar hoje = Calendar.getInstance();
		hoje.setTime(data);
		
		int diferencaEmHoras = Utils.diferencaEmHoras(hoje, calendar);
		
		if(diferencaEmHoras < 0){
			return 0;
		}
		
		return diferencaEmHoras;
	}

	private static Date normalizarDataInicio(Date data) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(data);
		calendar.set(Calendar.HOUR_OF_DAY, HORA_INICIO);
		
		if(Utils.compareDataHora(data, calendar.getTime()) < 0){
			return calendar.getTime();
		}
		
		return data;
	}

	public static int getQuantidadeDiasUteis(Date dataInicio, Date dataFim) {
		
		if(Utils.compareData(dataInicio, dataFim) >= 0){
			return 0;
		}
		
		int qDias = 0;
		
		Calendar c = Calendar.getInstance();
		c.setTime(dataInicio);
		
		for ( ; ; ) {
			c.add(Calendar.DAY_OF_MONTH, 1);
			Date time = c.getTime();
			if(Utils.compareData(time, dataFim) >= 0){
				break;
			} else {
				if(Feriado.isDiaUtil(time)){
					qDias++;
				}
			}
		}
		
		return qDias;
	}
}	
