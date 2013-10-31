package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Date;

import br.com.infowaypi.ecarebc.utils.ConfigFactory;
/**
 * 
 * @author Idelvane
 * @changes Danilo Portela
 */
public enum PeriodoSW implements Tempo{
	DIARIO             (1, "diario"), 
	SEMANAL            (2, "semanal"), 
	QUINZENAL          (3, "quinzenal"), 
	
	MENSAL             (4, "mensal"){
		@Override
		public Date getDataFinalEmMeses(Date dataBase) {
			return TempoSlideWindown.MES.getDataFinalEmMeses(dataBase);
		}
		@Override
		public Date getDataInicialEmMeses(Date dataBase) {
			return TempoSlideWindown.MES.getDataInicialEmMeses(dataBase);
		}
		
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoSlideWindown.MES.getDataFinalEmCompetencias(dataBase);
		}
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoSlideWindown.MES.getDataInicialEmCompetencias(dataBase);
		}
		
	},
	BIMESTRAL          (5, "bimestral"),
	
	TRIMESTRAL         (6, "trimestral"){
		@Override
		public Date getDataFinalEmMeses(Date dataBase) {
			return TempoSlideWindown.TRIMESTRE.getDataFinalEmMeses(dataBase);
		}
		@Override
		public Date getDataInicialEmMeses(Date dataBase) {
			return TempoSlideWindown.TRIMESTRE.getDataInicialEmMeses(dataBase);
		}
		
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoSlideWindown.TRIMESTRE.getDataFinalEmCompetencias(dataBase);
		}
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoSlideWindown.TRIMESTRE.getDataInicialEmCompetencias(dataBase);
		}
		
		@Override
		public String getDescricao() {
			return " nos últimos 03 meses.";
		}
	},
	SEMESTRAL          (7, "semestral"){
		@Override
		public Date getDataFinalEmMeses(Date dataBase) {
			return TempoSlideWindown.SEMESTRE.getDataFinalEmMeses(dataBase);
		} 
		@Override
		public Date getDataInicialEmMeses(Date dataBase) {
			return TempoSlideWindown.SEMESTRE.getDataInicialEmMeses(dataBase);
		}
		
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoSlideWindown.SEMESTRE.getDataFinalEmCompetencias(dataBase);
		}
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoSlideWindown.SEMESTRE.getDataInicialEmCompetencias(dataBase);
		}
		@Override
		public String getDescricao() {
			return " nos últimos 06 meses.";
		}
	},
	
	ANUAL              (8, "anual"){
		@Override
		public Date getDataFinalEmMeses(Date dataBase) {
			return TempoSlideWindown.ANO.getDataFinalEmMeses(dataBase);
		}
		@Override
		public Date getDataInicialEmMeses(Date dataBase) {
			return TempoSlideWindown.ANO.getDataInicialEmMeses(dataBase);
		}
		
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoSlideWindown.ANO.getDataFinalEmCompetencias(dataBase);
		}
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoSlideWindown.ANO.getDataInicialEmCompetencias(dataBase);
		}
		
		@Override
		public String getDescricao() {
			return " nos últimos 12 meses.";
		}
	},
	MENSAL_FATURAMENTO (9, "mensal"){
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoSlideWindown.MENSAL_FATURAMENTO.getDataFinalEmCompetencias(dataBase);
		} 
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoSlideWindown.MENSAL_FATURAMENTO.getDataInicialEmCompetencias(dataBase);
		}
	};
	
	private final Integer valor;
	private final String chave;
	private final Integer limiteConsultas;
	private final Integer limiteExames;
	private final Integer limiteConsultasOdontologicas;
	private final Integer limiteExamesOdontologicos;
	
	PeriodoSW(Integer valor,String chave){
		this.valor = valor;
		this.chave = chave;
		
		ConfigFactory config = ConfigFactory.getInstance();
		limiteConsultas = config.getValorInteger(chave  +".consultas");
		limiteExames = config.getValorInteger(chave + ".exames");
		limiteConsultasOdontologicas = config.getValorInteger(chave + ".consultasOdonto");
		limiteExamesOdontologicos = config.getValorInteger(chave + ".tratamentosOdonto");
	}
	
    public Integer getValor()   { return this.valor; }

	public Integer getLimiteConsultas() {
		return limiteConsultas;
	}

	public Integer getLimiteExames() {
		return limiteExames;
	}
	
	public Integer getLimiteConsultasOdontologicas() {
		return limiteConsultasOdontologicas;
	}

	public Integer getLimiteExamesOdontologicos() {
		return limiteExamesOdontologicos;
	}

	public Date getDataFinalEmMeses(Date dataBase) {
		throw new UnsupportedOperationException("A operação não pode ser realizada. Utilize as enums MENSAL, MENSAL_FATURAMENTO, ANUAL, TRIMESTRAL E SEMESTRAL.");
	}

	public Date getDataInicialEmMeses(Date dataBase) {
		throw new UnsupportedOperationException("A operação não pode ser realizada. Utilize as enums MENSAL, MENSAL_FATURAMENTO, ANUAL, TRIMESTRAL E SEMESTRAL.");
	}

	public Date getDataFinalEmCompetencias(Date dataBase) {
		throw new UnsupportedOperationException("A operação não pode ser realizada. Utilize as enums MENSAL, MENSAL_FATURAMENTO, ANUAL, TRIMESTRAL E SEMESTRAL.");
	}

	public Date getDataInicialEmCompetencias(Date dataBase) {
		throw new UnsupportedOperationException("A operação não pode ser realizada. Utilize as enums MENSAL, MENSAL_FATURAMENTO, ANUAL, TRIMESTRAL E SEMESTRAL.");
	}
	
	public static PeriodoSW getPeriodoAtValor(Integer valor) {
		for (PeriodoSW p : PeriodoSW.values()) {
			if(p.getValor().equals(valor))
				return p;
		}
		return null;
	}

	public String getDescricao() {
		return this.name();
	}

	public String getChave() {
		return chave;
	}
}