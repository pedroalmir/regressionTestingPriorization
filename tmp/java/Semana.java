package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public enum Semana {
    PRIMEIRA (),
    SEGUNDA  (),
    TERCEIRA (),
    QUARTA   ();

    public static Date getDataInicial(Date dataBase){
		GregorianCalendar inicioSemana = new GregorianCalendar();
		inicioSemana.setTime(dataBase);
		int diferencaDias = inicioSemana.get(Calendar.DAY_OF_WEEK);
		inicioSemana.add(Calendar.DAY_OF_MONTH, -(diferencaDias - Calendar.SUNDAY));
		return inicioSemana.getTime();
    }
    
    public static Date getDataFinal(Date dataBase){ 
		GregorianCalendar finalSemana = new GregorianCalendar();
		finalSemana.setTime(dataBase);
		int diferencaDias = Calendar.SATURDAY - finalSemana.get(Calendar.DAY_OF_WEEK);
		finalSemana.add(Calendar.DAY_OF_MONTH, diferencaDias);
		return finalSemana.getTime();
    }
}
