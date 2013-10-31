package br.com.infowaypi.ecarebc.atendimentos.enums;

/**
 * Enumeration para especificação de classes de especialidades
 * @author Danilo Nogueira Portela
 *
 */
public enum ClasseEspecialidade {

	NENHUMA(0),
	MEDICINA(1),
	ODONTOLOGIA(2),
	FISIOTERAPIA(3),
	PSICOLOGIA(4),
	NUTRICAO(5);
	
	private Integer codigo;
	
	ClasseEspecialidade(Integer codigo){
		this.codigo = codigo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
}
