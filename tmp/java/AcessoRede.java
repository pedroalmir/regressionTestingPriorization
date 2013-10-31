package br.com.infowaypi.ecarebc.produto;

/**
*  Características dos planos de saúde quanto às formas de acesso à rede credenciada 
 * @author Alan
 *
 */
public enum AcessoRede {
	/**
	 * Para determinados procedimentos, o usuário é obrigado a solicitar um pedido de autorização prévia à operadora.
	 */
    AUTORIZACAO_PREVIA("Autorização prévia"),

    /**
     * Parte da realização de um determinado procedimento (Consulta, exame ou internação) é custeada pelo usuário. 
     */
    CO_PARTICIPACAO("Co-participação"),

	/**
	 * Permite encaminhar o usuário a uma rede credenciada ou referenciada para realização de procedimentos (consultas, exames ou internações) 
	 * previamente determinados. 
	 */
    DIRECIONAMENTO_REFERENCIAMENTO_HIERARQUIZACAO("Direcionamento ou Referenciamento ou Hierarquização de Acesso"),

	/**
	 * A responsabilidade de cobertura da operadora é condicionada por um valor limite, previamente estabelecido em contrato.
	 */
    FRANQUIA("Franquia"),

	/**
	 * O usuário pode optar por receber atendimento de profissionais não credenciados e receber, 
	 * da operadora, o reembolso do valor pago pelo serviço prestado, ou parte desse valor, de acordo com a tabela também prevista no contrato. 
	 */
    LIVRE_ESCOLHA("Livre Escolha de Prestador de Serviços de Saúde"),

	/**
	 * O usuário é avaliado previamente por um profissional da operadora, que fica responsável por encaminhar o consumidor para 
	 * o tratamento (médico, cirúrgico ou dental) necessário.
	 */
    PORTA_DE_ENTRADA("Porta de entrada");
    
    private String descricao;

	private AcessoRede(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
