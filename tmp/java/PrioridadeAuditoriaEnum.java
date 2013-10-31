package br.com.infowaypi.ecarebc.atendimentos.honorario;

public enum PrioridadeAuditoriaEnum {
	
	PRIORIDADE_ZERO(0, "Vermelho", "Nenhum honor�rio auditado."),
	PRIORIDADE_UM(1, "Amarelo", "Alguns honor�rios auditados, mas n�o todos."),
	PRIORIDADE_DOIS(2, "Verde", "Todos os honor�rios auditados.");
	
	private Integer prioridade;
	private String cor;
	private String descricao;

	private PrioridadeAuditoriaEnum(Integer prioridade, String cor, String descricao) {
		this.prioridade = prioridade;
		this.cor = cor;
		this.descricao = descricao;
	}
	
	public String getCor() {
		return cor;
	}

	public String getDescricao() {
		return descricao;
	}

	public Integer getPrioridade() {
		return prioridade;
	}
}
