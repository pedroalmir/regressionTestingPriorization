package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import br.com.infowaypi.ecarebc.utils.ConfigFactory;
/**
 * Usado nos cálculos de datas inicial e final do Periodo.
 * @see Periodo
 * @author Idelvane / Jonhnny
 * @changes Danilo Portela
 */
public enum TempoStaticWindown {
	
	MENSAL_FATURAMENTO(0){
		
		public Date getDataInicialEmCompetencias(Date dataBase){
			return super.getDataInicialPeriodo(dataBase, true);
		}

		public Date getDataFinalEmCompetencias(Date dataBase){ 
			return super.getDataFinalPeriodo(dataBase, true);
		}
		
		public Date getDataInicialEmMeses(Date dataBase){
			return null;
		}

		public Date getDataFinalEmMeses(Date dataBase){ 
			return null;
		}
		
	},
	MES(0){
		public Date getDataInicialEmCompetencias(Date dataBase){
			return super.getDataInicialPeriodo(dataBase, true);
		}

		public Date getDataFinalEmCompetencias(Date dataBase){ 
			return super.getDataFinalPeriodo(dataBase, true);
		}
		
		public Date getDataInicialEmMeses(Date dataBase){
			return super.getDataInicialPeriodo(dataBase, false);
		}

		public Date getDataFinalEmMeses(Date dataBase){ 
			return super.getDataFinalPeriodo(dataBase, false);
		}
	},
	TRIMESTRE(2) {
		public Date getDataInicialEmCompetencias(Date dataBase){
			return super.getDataInicialPeriodo(dataBase, true);
		}

		public Date getDataFinalEmCompetencias(Date dataBase){ 
			return super.getDataFinalPeriodo(dataBase, true);
		}
		
		public Date getDataInicialEmMeses(Date dataBase) {
			return super.getDataInicialPeriodo(dataBase, false);
		}

		public Date getDataFinalEmMeses(Date dataBase) {
			return super.getDataFinalPeriodo(dataBase, false);
		}
	},
	SEMESTRE(5) {
		public Date getDataInicialEmCompetencias(Date dataBase){
			return super.getDataInicialPeriodo(dataBase, true);
		}

		public Date getDataFinalEmCompetencias(Date dataBase){ 
			return super.getDataFinalPeriodo(dataBase, true);
		}
		
		public Date getDataInicialEmMeses(Date dataBase) {
			return super.getDataInicialPeriodo(dataBase, false);
		}

		public Date getDataFinalEmMeses(Date dataBase) {
			return super.getDataFinalPeriodo(dataBase, false);
		}
	},	
	ANO(11) {
		public Date getDataInicialEmCompetencias(Date dataBase){
			return super.getDataInicialPeriodo(dataBase, true);
		}

		public Date getDataFinalEmCompetencias(Date dataBase){ 
			return super.getDataFinalPeriodo(dataBase, true);
		}
		
		public Date getDataInicialEmMeses(Date dataBase) {
			return super.getDataInicialPeriodo(dataBase, false);
		}
		
		public Date getDataFinalEmMeses(Date dataBase) {
			return super.getDataFinalPeriodo(dataBase, false);
		}
	};
	
	protected Integer inicioCompetencia;
	protected Integer fimCompetencia;
	protected Integer valor;
	
	
	TempoStaticWindown(Integer valor) {
		ConfigFactory config = ConfigFactory.getInstance();
		inicioCompetencia = config.getValorInteger("inicio.competencia");
		fimCompetencia = config.getValorInteger("fim.competencia");
		
		this.valor = valor;
	}
	
	
	/**
	 * Retorna a data inicial para o periodo 
	 */
	private Date getDataInicialPeriodo(Date dataBase, Boolean isCompetencia){ 
		GregorianCalendar inicioPeriodo = new GregorianCalendar();
		inicioPeriodo.setTime(dataBase);
		
		//Construindo a data inicial do periodo em competencias
		if(isCompetencia){
			Integer diaDoMes = inicioPeriodo.get(Calendar.DAY_OF_MONTH);
			if(diaDoMes <= fimCompetencia)
				inicioPeriodo.add(Calendar.MONTH, -1);
			
			inicioPeriodo.set(Calendar.DAY_OF_MONTH, inicioCompetencia);
		}
		
		//Construindo data inicial do período em meses
		else{
			inicioPeriodo.set(Calendar.DAY_OF_MONTH, inicioPeriodo.getActualMinimum(Calendar.DAY_OF_MONTH) );
		}
		
		return inicioPeriodo.getTime();
	}
	
	/**
	 * Retorna a data final para o periodo 
	 */
	private Date getDataFinalPeriodo(Date dataBase, Boolean isCompetencia){ 
		GregorianCalendar finalPeriodo = new GregorianCalendar();
		finalPeriodo.setTime(dataBase);
		
		//Construindo a data final do periodo em competencias
		if(isCompetencia){
			Integer diaDoMes = finalPeriodo.get(Calendar.DAY_OF_MONTH);
			if(diaDoMes >= inicioCompetencia)
				finalPeriodo.add(Calendar.MONTH, 1);
			
			finalPeriodo.set(Calendar.DAY_OF_MONTH, fimCompetencia);
		}
		
		//Construindo data final do período em meses
		else{
			finalPeriodo.set(Calendar.DAY_OF_MONTH, finalPeriodo.getActualMaximum(Calendar.DAY_OF_MONTH) );
		}
		
		finalPeriodo.add(Calendar.MONTH, valor);
		return finalPeriodo.getTime();
	}
	
	public String getDescricao() {
		return this.name();
	}

	public Integer getInicioCompetencia() {
		return inicioCompetencia;
	}

	public void setInicioCompetencia(Integer inicioCompetencia) {
		this.inicioCompetencia = inicioCompetencia;
	}

	public Integer getFimCompetencia() {
		return fimCompetencia;
	}

	public void setFimCompetencia(Integer fimCompetencia) {
		this.fimCompetencia = fimCompetencia;
	}
	
	public Integer getValor()   { 
		return this.valor;
	}	
	
	public Date getDataInicialEmMeses(Date dataBase){
		return null;
	}
	public Date getDataFinalEmMeses(Date dataBase){
		return null;
	}
	public Date getDataInicialEmCompetencias(Date dataBase){
		return null;
	}
	public Date getDataFinalEmCompetencias(Date dataBase){
		return null;
	}
	
}
