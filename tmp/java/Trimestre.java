package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public enum Trimestre {
    PRIMEIRO (0, 2),
    SEGUNDO  (3, 5),
    TERCEIRO (6, 8),
    QUARTO   (9, 11);

    private final int mesInicial;
    private final int mesFinal;
    
    Trimestre(int mesInicial, int mesFinal) {
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
    
    private static Trimestre getMes(int mes){
    	Trimestre b = null;
		switch (mes) {
			case Calendar.JANUARY:
			case Calendar.FEBRUARY:
			case Calendar.MARCH:
				b = Trimestre.PRIMEIRO;
				break;
			case Calendar.APRIL:
			case Calendar.MAY:
			case Calendar.JUNE:
				b = Trimestre.SEGUNDO;
				break;
			case Calendar.JULY:
			case Calendar.AUGUST:
			case Calendar.SEPTEMBER:
				b = Trimestre.TERCEIRO;
				break;
			case Calendar.OCTOBER:
			case Calendar.NOVEMBER:
			case Calendar.DECEMBER:
				b = Trimestre.QUARTO;
				break;
		}
		return b;
    }
}
