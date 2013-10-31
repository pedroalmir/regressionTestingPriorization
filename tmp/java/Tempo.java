package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Date;

/**
 * Utilizada para dar mais flexibilidade nas operações com Enum de periodo.
 * @author Idelvane
 * 
 */
public interface Tempo {
	
	public Date getDataInicialEmMeses(Date dataBase) ;
	public Date getDataFinalEmMeses(Date dataBase) ;
	
	public Date getDataInicialEmCompetencias(Date dataBase) ;
	public Date getDataFinalEmCompetencias(Date dataBase) ;
	
	public String getDescricao();
	public Integer getValor() ;
}
