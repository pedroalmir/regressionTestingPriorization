package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public enum Ano {
    CORRENTE  ();
    
    private final Date dataInicial; 
    private final Date dataFinal; 
    
    Ano(){
    	GregorianCalendar calendario = new GregorianCalendar();
    	calendario.set(Calendar.MONTH, Calendar.JANUARY);
    	calendario.set(Calendar.DAY_OF_MONTH, 1);
    	dataInicial = calendario.getTime();
    	calendario.set(Calendar.MONTH, Calendar.DECEMBER);
    	calendario.set(Calendar.DAY_OF_MONTH, 31);
    	dataFinal = calendario.getTime();
    }
    
	public Date getDataFinal() {
		return dataFinal;
	}

	public Date getDataInicial() {
		return dataInicial;
	}
}
