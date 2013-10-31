package br.com.infowaypi.ecarebc.produto;

/**
*  Caracter�sticas dos planos de sa�de quanto �s formas de acesso � rede credenciada 
 * @author Alan
 *
 */
public enum AcessoRede {
	/**
	 * Para determinados procedimentos, o usu�rio � obrigado a solicitar um pedido de autoriza��o pr�via � operadora.
	 */
    AUTORIZACAO_PREVIA("Autoriza��o pr�via"),

    /**
     * Parte da realiza��o de um determinado procedimento (Consulta, exame ou interna��o) � custeada pelo usu�rio. 
     */
    CO_PARTICIPACAO("Co-participa��o"),

	/**
	 * Permite encaminhar o usu�rio a uma rede credenciada ou referenciada para realiza��o de procedimentos (consultas, exames ou interna��es) 
	 * previamente determinados. 
	 */
    DIRECIONAMENTO_REFERENCIAMENTO_HIERARQUIZACAO("Direcionamento ou Referenciamento ou Hierarquiza��o de Acesso"),

	/**
	 * A responsabilidade de cobertura da operadora � condicionada por um valor limite, previamente estabelecido em contrato.
	 */
    FRANQUIA("Franquia"),

	/**
	 * O usu�rio pode optar por receber atendimento de profissionais n�o credenciados e receber, 
	 * da operadora, o reembolso do valor pago pelo servi�o prestado, ou parte desse valor, de acordo com a tabela tamb�m prevista no contrato. 
	 */
    LIVRE_ESCOLHA("Livre Escolha de Prestador de Servi�os de Sa�de"),

	/**
	 * O usu�rio � avaliado previamente por um profissional da operadora, que fica respons�vel por encaminhar o consumidor para 
	 * o tratamento (m�dico, cir�rgico ou dental) necess�rio.
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
