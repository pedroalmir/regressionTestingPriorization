package br.com.infowaypi.ecarebc.utils;

import java.util.Calendar;
import java.util.Date;


public class CompetenciaUtils {
	
	/**
	 * Retorna a data de início da competência informada.
	 * Exemplo:
	 * 		Competência - 01/06/2008.
	 * 		Data de Início - 21/05/2008.
	 * @param competencia
	 * @return 
	 */
	
	public static Date getInicioCompetencia(Date competencia){
		Integer inicioCompetencia = getDiaInicioCompetencia();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(competencia);
		/*if_not[COMPETENCIA_MESMO_MES]*/
		calendar.add(Calendar.MONTH, -1);
		/* end[COMPETENCIA_MESMO_MES]*/
		calendar.set(Calendar.DAY_OF_MONTH, inicioCompetencia);
		calendar.setLenient(false);
		return calendar.getTime();
	}

	/**
	 * Retorna a data de término da competência informada
	 * Exemplo:
	 * 		Competência - 01/06/2008
	 *  	Data de Início - 20/06/2008
	 * @param competencia
	 * @return 
	 */
	public static Date getFimCompetencia(Date competencia){
		Integer fimCompetencia = getDiaFimCompetencia();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(competencia);
		/*if[COMPETENCIA_MESMO_MES]
		int ultimoDia = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		if(fimCompetencia>ultimoDia){
			fimCompetencia = ultimoDia;
		}
		end[COMPETENCIA_MESMO_MES]*/
		calendar.set(Calendar.DAY_OF_MONTH, fimCompetencia);
		calendar.setLenient(false);
		return calendar.getTime();
	}
	
	/**
	 * Retorna a competência de uma determinada data
	 * Exemplo 01:
	 * 		Data informada - 20/06/2008
	 * 		Competência - 01/06/2008
	 * Exemplo 02:
	 * 		Data informada - 30/06/2008
	 * 		Competência - 01/07/2008
	 * @param data
	 * @return
	 */
	public static Date getCompetencia(Date data){
		
		Calendar calendar = Calendar.getInstance();
		calendar.setLenient(false);
		calendar.setTime(data);
		if(calendar.get(Calendar.DAY_OF_MONTH) <= getDiaFimCompetencia()){
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			return calendar.getTime();
		}else{
			calendar.add(Calendar.MONTH, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			return calendar.getTime();
		}

	}

	private static Integer getDiaInicioCompetencia() {
		ConfigFactory config = ConfigFactory.getInstance();
		Integer inicioCompetencia = config.getValorInteger("inicio.competencia");
		return inicioCompetencia;
	}
	
	private static Integer getDiaFimCompetencia() {
		ConfigFactory config = ConfigFactory.getInstance();
		Integer fimCompetencia = config.getValorInteger("fim.competencia");
		return fimCompetencia;
	}
	
}
