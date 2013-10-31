package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public enum Bimestre {
    PRIMEIRO (0, 1),
    SEGUNDO  (2, 3),
    TERCEIRO (4, 5),
    QUARTO   (6, 7),
    QUINTO   (8, 9),
    SEXTO    (10, 11);

    private final int mesInicial;
    private final int mesFinal;
    
    Bimestre(int mesInicial, int mesFinal) {
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
    
    private static Bimestre getMes(int mes){
    	Bimestre b = null;
		switch (mes) {
			case Calendar.JANUARY:
			case Calendar.FEBRUARY:
				b = Bimestre.PRIMEIRO;
				break;
			case Calendar.MARCH:
			case Calendar.APRIL:
				b = Bimestre.SEGUNDO;
				break;
			case Calendar.MAY:
			case Calendar.JUNE:
				b = Bimestre.TERCEIRO;
				break;
			case Calendar.JULY:
			case Calendar.AUGUST:
				b = Bimestre.QUARTO;
				break;
			case Calendar.SEPTEMBER:
			case Calendar.OCTOBER:
				b = Bimestre.QUINTO;
				break;
			case Calendar.NOVEMBER:
			case Calendar.DECEMBER:
				b = Bimestre.SEXTO;
				break;
		}
		return b;
    }
}
