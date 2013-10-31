package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public enum Mes {
	TODOS	  (-1, "Todos"),
    JANEIRO   (0, "Janeiro"),
    FEVEREIRO (1, "Fevereiro"),
    MARCO     (2, "Março"),
    ABRIL     (3, "Abril"),
    MAIO      (4, "Maio"),
    JUNHO     (5, "Junho"),
    JULHO     (6, "Julho"),
    AGOSTO    (7, "Agosto"),
    SETEMBRO  (8, "Setembro"),
    OUTUBRO   (9, "Outubro"),
    NOVEMBRO  (10, "Novembro"),
    DEZEMBRO  (11, "Dezembro");
    
    private final Date dataInicial; 
    private final Date dataFinal;
    private String descricao; 
    private Integer valor;
    
    public Integer getValor() {
		return valor;
	}

	public void setValor(Integer valor) {
		this.valor = valor;
	}

	Mes(Integer mes, String descricao){
		this.valor = mes;
		this.descricao = descricao;
    	GregorianCalendar calendario = new GregorianCalendar();
    	calendario.set(Calendar.MONTH, mes);
    	calendario.set(Calendar.DAY_OF_MONTH, 1);
    	dataInicial = calendario.getTime();
    	calendario.set(Calendar.DAY_OF_MONTH, calendario.getActualMaximum(Calendar.DAY_OF_MONTH));
    	dataFinal = calendario.getTime();
    }
    
	public Date getDataFinal() {
		return dataFinal;
	}

	public Date getDataInicial() {
		return dataInicial;
	}
	
	public static Mes getMesAtValor(Integer valor){
		for (Mes mes : Mes.values()) {
			if(mes.getValor().equals(valor))
				return mes;
		}
		return null;
	}
	
	public static Date getDataInicial(Date dataBase){
		GregorianCalendar inicioMes = new GregorianCalendar();
		inicioMes.setTime(dataBase);
		inicioMes.set(Calendar.DAY_OF_MONTH, 1);
		return inicioMes.getTime();
	}

	public static Date getDataFinal(Date dataBase){ 
		GregorianCalendar finalMes = new GregorianCalendar();
		finalMes.setTime(dataBase);
		finalMes.set(Calendar.DAY_OF_MONTH, finalMes.getActualMaximum(Calendar.DAY_OF_MONTH) );
		return finalMes.getTime();
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
