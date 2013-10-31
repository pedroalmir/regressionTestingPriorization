package br.com.infowaypi.ecarebc.atendimentos.enums;

/**
 * Representa os subtipos de tratamento seriado.
 * @author benedito
 * @changes patrícia
 *
 */
public enum TipoTratamentoSeriadoEnum {

//	Subtipos de Fisioterapia.
	MOTORA(1, "Motora"),
	RESPIRATORIA(2, "Respiratoria"),
	CARDIACA(3, "Cardiaca"),
	NEUROLOGICA(4, "Neurologica"),
	
//	Subtipos de Quimioterapia.
	PRIMEIRO_CICLO(5, "1° Ciclo",
			SubTipoTratamentoSeriadoEnum.PRIMEIRA_LINHA,
			SubTipoTratamentoSeriadoEnum.SEGUNDA_LINHA,
			SubTipoTratamentoSeriadoEnum.TERCEIRA_LINHA,
			SubTipoTratamentoSeriadoEnum.QUARTA_LINHA),
			
	CICLOS_SUBSEQUENTES(6,"Ciclos Subsequentes"),
	TRATAMENTO_ADJUVANTE(7,"Tratamento Adjuvante"),
	
//	Subtipos de Radioterapia.
	CLINICA(8, "Clinica"),
	BRAQUITERAPIA(9, "Braquiterapia"),
	
//	Subtipos de TRS
	HEMODIALISE(10, "Hemodialise",
			SubTipoTratamentoSeriadoEnum.AGUDA_POR_SESSAO,
			SubTipoTratamentoSeriadoEnum.CRONICA_POR_SESSAO,
			SubTipoTratamentoSeriadoEnum.CONTINUA),
			
	CAPD(11, "CAPD");
	
	private Integer valor;
	private String tipo;
	private SubTipoTratamentoSeriadoEnum[] subTipo;

	private TipoTratamentoSeriadoEnum(Integer valor,String tipo,SubTipoTratamentoSeriadoEnum ... subtipo) {
		this.valor = valor;
		this.tipo = tipo;
		this.subTipo = subtipo;
	}
	
	public SubTipoTratamentoSeriadoEnum[] subTipo() {
		return subTipo;
	}

	public String tipo() {
		return tipo;
	}

	public Integer valor() {
		return valor;
	}
	
	public static TipoTratamentoSeriadoEnum getTipoTratamento(Integer valor){
		if (valor.equals(1))
			return TipoTratamentoSeriadoEnum.MOTORA;
		if (valor.equals(2))
			return TipoTratamentoSeriadoEnum.RESPIRATORIA;
		if (valor.equals(3))
			return TipoTratamentoSeriadoEnum.CARDIACA;
		if (valor.equals(4))
			return TipoTratamentoSeriadoEnum.NEUROLOGICA;
		if (valor.equals(5))
			return TipoTratamentoSeriadoEnum.PRIMEIRO_CICLO;
		if (valor.equals(6))
			return TipoTratamentoSeriadoEnum.CICLOS_SUBSEQUENTES;
		if (valor.equals(7))
			return TipoTratamentoSeriadoEnum.TRATAMENTO_ADJUVANTE;
		if (valor.equals(8))
			return TipoTratamentoSeriadoEnum.CLINICA;
		if (valor.equals(9))
			return TipoTratamentoSeriadoEnum.BRAQUITERAPIA;
		if (valor.equals(10))
			return TipoTratamentoSeriadoEnum.HEMODIALISE;
		if (valor.equals(11))
			return TipoTratamentoSeriadoEnum.CAPD;
		
		return null;
	}

}
