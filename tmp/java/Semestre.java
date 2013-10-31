package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public enum Semestre {
    PRIMEIRO (0, 5),
    SEGUNDO  (6, 11);

    private final int mesInicial;
    private final int mesFinal;
    
    Semestre(int mesInicial, int mesFinal) {
        this.mesInicial = mesInicial;
        this.mesFinal = mesFinal;
    }
    
    
    public int getMesInicial()   { return mesInicial; }
    public int getMesFinal() { return mesFinal; }
    
    public static Date getDataInicial(Date dataBase){
    	GregorianCalendar calendario = new GregorianCalendar();
    	calendario.setTime(dataBase);
    	calendario.set(Calendar.MONTH, getMes(calendario.get(Calendar.MONTH)).mesInicial);
    	calendario.set(Calendar.DAY_OF_MONTH, 1);
    	return calendario.getTime(); 
    }
    
    public static Date getDataFinal(Date dataBase){ 
    	GregorianCalendar calendario = new GregorianCalendar();
    	calendario.setTime(dataBase);
    	calendario.set(Calendar.MONTH, getMes(calendario.get(Calendar.MONTH)).mesFinal);
    	calendario.set(Calendar.DAY_OF_MONTH, calendario.getActualMaximum(Calendar.DAY_OF_MONTH));
    	return calendario.getTime(); 
    }
    
    private static Semestre getMes(int mes){
    	Semestre b = null;
		switch (mes) {
			case Calendar.JANUARY:
			case Calendar.FEBRUARY:
			case Calendar.MARCH:
			case Calendar.APRIL:
			case Calendar.MAY:
			case Calendar.JUNE:
				b = Semestre.PRIMEIRO;
				break;
			case Calendar.JULY:
			case Calendar.AUGUST:
			case Calendar.SEPTEMBER:
			case Calendar.OCTOBER:
			case Calendar.NOVEMBER:
			case Calendar.DECEMBER:
				b = Semestre.SEGUNDO;
				break;
		}
		return b;
    }
}
