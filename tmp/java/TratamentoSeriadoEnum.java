package br.com.infowaypi.ecarebc.atendimentos.enums;

/**
 * Representa os tipos de tratamento seriado.
 * @author benedito
 * @changes patrícia
 */
public enum TratamentoSeriadoEnum {
	
	FISIOTERAPIA(1, "Fisioterapia", 10, 60,
			TipoTratamentoSeriadoEnum.MOTORA,
			TipoTratamentoSeriadoEnum.RESPIRATORIA,
			TipoTratamentoSeriadoEnum.CARDIACA,
			TipoTratamentoSeriadoEnum.NEUROLOGICA),

	FONOAUDIOLOGIA(2, "Fonoaudiologia", 2, 20),

	PSICOLOGIA(3, "Psicologia", 2, 20),
	
	TERAPIA_OCUPACIONAL (4, "Terapia Ocupacional", null, null),
	
	TERAPIA_GLOBAL (5, "Terapia Global", null, null),

	QUIMIOTERAPIA(6, "Quimioterapia", null, null,
			TipoTratamentoSeriadoEnum.PRIMEIRO_CICLO,
			TipoTratamentoSeriadoEnum.CICLOS_SUBSEQUENTES,
			TipoTratamentoSeriadoEnum.TRATAMENTO_ADJUVANTE),

	RADIOTERAPIA(7, "Radioterapia", null, null,
			TipoTratamentoSeriadoEnum.CLINICA,
			TipoTratamentoSeriadoEnum.BRAQUITERAPIA),

	TRS(8, "Terapia renal substitutiva", null, null,
			TipoTratamentoSeriadoEnum.HEMODIALISE,
			TipoTratamentoSeriadoEnum.CAPD);

	private Integer valor;
	private String descricao;
	private Integer limiteMensal;
	private Integer limiteAnual;
	private TipoTratamentoSeriadoEnum[] tipo;

	private TratamentoSeriadoEnum(Integer valor, String tratamento, Integer limiteMensal, Integer limiteAnual, TipoTratamentoSeriadoEnum ... tipo) {
		this.valor = valor;
		this.descricao = tratamento;
		this.limiteMensal = limiteMensal;
		this.limiteAnual = limiteAnual;
		this.tipo = tipo;
	}

	public TipoTratamentoSeriadoEnum[] tipo() {
		return tipo;
	}

	public Integer valor() {
		return valor;
	}

	public String descricao() {
		return descricao;
	}

	public Integer limiteMensal() {
		return limiteMensal;
	}

	public Integer limiteAnual() {
		return limiteAnual;
	}

	public static TratamentoSeriadoEnum getTratamento(Integer valor){
		if (valor.equals(1))
			return TratamentoSeriadoEnum.FISIOTERAPIA;
		if (valor.equals(2))
			return TratamentoSeriadoEnum.FONOAUDIOLOGIA;
		if (valor.equals(3))
			return TratamentoSeriadoEnum.PSICOLOGIA;
		if (valor.equals(4))
			return TratamentoSeriadoEnum.TERAPIA_OCUPACIONAL;
		if (valor.equals(5))
			return TratamentoSeriadoEnum.TERAPIA_GLOBAL;
		if (valor.equals(6))
			return TratamentoSeriadoEnum.QUIMIOTERAPIA;
		if (valor.equals(7))
			return TratamentoSeriadoEnum.RADIOTERAPIA;
		if (valor.equals(8))
			return TratamentoSeriadoEnum.TRS;
		
		return null;
	}
	
}
