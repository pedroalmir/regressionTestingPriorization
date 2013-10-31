package br.com.infowaypi.ecarebc.produto;

/**
 * Caracter�sticas dos planos de sa�de quanto �s formas de contrata��o
 */
public enum ModalidadeContratacao {
	/**
	 * O contrato � assinado entre um indiv�duo (pessoa f�sica) e a operadora, para presta��o de servi�os de sa�de do titular 
	 * (nos individuais) e de seus dependentes (nos familiares).
	 */
	INDIVIDUAL_FAMILIAR("Individual ou Familiar"),
	
	/**
	 * O contrato � assinado entre uma pessoa jur�dica a um determinado grupo de pessoas que possuem v�nculo empregat�cio, 
	 * associativo ou sindical com o contratante. Todos t�m livre escolha para aderir ou n�o. 
	 */
	COLETIVO_ADESAO("Coletivo por Ades�o"),
	
	/**
	 * Contratados por pessoas jur�dicas para atender a uma massa populacional espec�fica que mant�m um v�nculo empregat�cio, 
	 * associativo ou sindical com o contratante.
	 */
	COLETIVO_EMPRESARIAL("Coletivo Empresarial");
	
	private String descricao;
	
	private ModalidadeContratacao parent = null;

    private ModalidadeContratacao(ModalidadeContratacao parent) {
        this.parent = parent;
    }

    public boolean is(ModalidadeContratacao other) {
        if (other == null) {
            return false;
        }
       
        for (ModalidadeContratacao t = this;  t != null;  t = t.parent) {
            if (other == t) {
                return true;
            }
        }
        return false;
    }
	
    private ModalidadeContratacao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
