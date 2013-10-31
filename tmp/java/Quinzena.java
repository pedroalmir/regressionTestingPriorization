package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public enum Quinzena {
    PRIMEIRA (),
    SEGUNDA  ();

    public static Date getDataInicial(Date dataBase){
		GregorianCalendar inicioQuinzena = new GregorianCalendar();
		inicioQuinzena.setTime(dataBase);
		if(inicioQuinzena.get(Calendar.DAY_OF_MONTH) < 16)
			inicioQuinzena.set(Calendar.DAY_OF_MONTH, 1);
		else
			inicioQuinzena.set(Calendar.DAY_OF_MONTH, 16);
		return inicioQuinzena.getTime();
    }
    
    public static Date getDataFinal(Date dataBase){ 
		GregorianCalendar finalQuinzena = new GregorianCalendar();
		finalQuinzena.setTime(dataBase);
		if(finalQuinzena.get(Calendar.DAY_OF_MONTH) < 16)
			finalQuinzena.set(Calendar.DAY_OF_MONTH, 15);
		else{
			finalQuinzena.set(Calendar.DAY_OF_MONTH, finalQuinzena.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		return finalQuinzena.getTime();
    }
}
