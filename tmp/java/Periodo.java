package br.com.infowaypi.ecarebc.consumo.periodos;

import java.util.Date;

import br.com.infowaypi.ecarebc.utils.ConfigFactory;

/**
 * 
 * @author root
 * A partir de um arquivo de properties carrega os valores. Coloque as constantes no arquivo, mesmo as que 
 * tenham valor zero. O Arquivo de properties deve ter o nome de e-care.properties (default)
 * Ex.:
 * 
 *	semanal.consultas=0
 *  quinzenal.consultas=0
 *	mensal.consultas = 2
 *	bimestral.consultas = 0
 *	trimestral.consultas = 5
 *	semestral.consultas = 7
 *	anual.consultas = 10
 *
 * @changes Danilo Nogueira Portela (Inserção de consumos odontológicos)
 */

public enum Periodo implements Tempo{
	DIARIO             (1, "diario"), 
	SEMANAL            (2, "semanal"), 
	QUINZENAL          (3, "quinzenal"), 
	
	MENSAL             (4, "mensal"){
		@Override
		public Date getDataFinalEmMeses(Date dataBase) {
			return TempoStaticWindown.MES.getDataFinalEmMeses(dataBase);
		}
		@Override
		public Date getDataInicialEmMeses(Date dataBase) {
			return TempoStaticWindown.MES.getDataInicialEmMeses(dataBase);
		}
		
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoStaticWindown.MES.getDataFinalEmCompetencias(dataBase);
		}
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoStaticWindown.MES.getDataInicialEmCompetencias(dataBase);
		}
		
	},
	BIMESTRAL          (5, "bimestral"),
	
	TRIMESTRAL         (6, "trimestral"){
		@Override
		public Date getDataFinalEmMeses(Date dataBase) {
			return TempoStaticWindown.TRIMESTRE.getDataFinalEmMeses(dataBase);
		}
		@Override
		public Date getDataInicialEmMeses(Date dataBase) {
			return TempoStaticWindown.TRIMESTRE.getDataInicialEmMeses(dataBase);
		}
		
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoStaticWindown.TRIMESTRE.getDataFinalEmCompetencias(dataBase);
		}
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoStaticWindown.TRIMESTRE.getDataInicialEmCompetencias(dataBase);
		}
		
		@Override
		public String getDescricao() {
			return " nos últimos 03 meses.";
		}
	},
	SEMESTRAL          (7, "semestral"){
		@Override
		public Date getDataFinalEmMeses(Date dataBase) {
			return TempoStaticWindown.SEMESTRE.getDataFinalEmMeses(dataBase);
		} 
		@Override
		public Date getDataInicialEmMeses(Date dataBase) {
			return TempoStaticWindown.SEMESTRE.getDataInicialEmMeses(dataBase);
		}
		
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoStaticWindown.SEMESTRE.getDataFinalEmCompetencias(dataBase);
		}
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoStaticWindown.SEMESTRE.getDataInicialEmCompetencias(dataBase);
		}
		@Override
		public String getDescricao() {
			return " nos últimos 06 meses.";
		}
	},
	
	ANUAL              (8, "anual"){
		@Override
		public Date getDataFinalEmMeses(Date dataBase) {
			return TempoStaticWindown.ANO.getDataFinalEmMeses(dataBase);
		}
		@Override
		public Date getDataInicialEmMeses(Date dataBase) {
			return TempoStaticWindown.ANO.getDataInicialEmMeses(dataBase);
		}
		
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoStaticWindown.ANO.getDataFinalEmCompetencias(dataBase);
		}
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoStaticWindown.ANO.getDataInicialEmCompetencias(dataBase);
		}
		
		@Override
		public String getDescricao() {
			return " nos últimos 12 meses.";
		}
	},
	MENSAL_FATURAMENTO (9, "mensal"){
		@Override
		public Date getDataFinalEmCompetencias(Date dataBase) {
			return TempoStaticWindown.MENSAL_FATURAMENTO.getDataFinalEmCompetencias(dataBase);
		} 
		@Override
		public Date getDataInicialEmCompetencias(Date dataBase) {
			return TempoStaticWindown.MENSAL_FATURAMENTO.getDataInicialEmCompetencias(dataBase);
		}
	};
	
	private final int valor;
	private final String chave;
	private final int limiteConsultas;
	private final int limiteExames;
	private final int limiteConsultasOdonto;
	private final int limiteTratamentosOdonto;

	Periodo(int valor,String chave){
		this.valor = valor;
		this.chave = chave;
		ConfigFactory config = ConfigFactory.getInstance();
		this.limiteConsultas = config.getValorInteger(chave+".consultas");
		this.limiteExames = config.getValorInteger(chave+".exames");
		this.limiteConsultasOdonto = config.getValorInteger(chave+".consultasOdonto");
		this.limiteTratamentosOdonto = config.getValorInteger(chave+".tratamentosOdonto");
	}
	
    public Integer getValor() { return this.valor; }

	public int getLimiteConsultas() {
		return limiteConsultas;
	}

	public int getLimiteExames() {
		return limiteExames;
	}

	public int getLimiteConsultasOdonto() {
		return limiteConsultasOdonto;
	}
	
	public String getChave() {
		return chave;
	}

	public int getLimiteTratamentosOdonto() {
		return limiteTratamentosOdonto;
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
	
	public static Periodo getPeriodoAtValor(Integer valor) {
		for (Periodo p : Periodo.values()) {
			if(p.getValor().equals(valor))
				return p;
		}
		return null;
	}

	public String getDescricao() {
		return this.name();
	}
}