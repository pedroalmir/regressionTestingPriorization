package br.com.infowaypi.ecarebc.produto;

/**
 * Características dos planos de saúde quanto às formas de contratação
 */
public enum ModalidadeContratacao {
	/**
	 * O contrato é assinado entre um indivíduo (pessoa física) e a operadora, para prestação de serviços de saúde do titular 
	 * (nos individuais) e de seus dependentes (nos familiares).
	 */
	INDIVIDUAL_FAMILIAR("Individual ou Familiar"),
	
	/**
	 * O contrato é assinado entre uma pessoa jurídica a um determinado grupo de pessoas que possuem vínculo empregatício, 
	 * associativo ou sindical com o contratante. Todos têm livre escolha para aderir ou não. 
	 */
	COLETIVO_ADESAO("Coletivo por Adesão"),
	
	/**
	 * Contratados por pessoas jurídicas para atender a uma massa populacional específica que mantém um vínculo empregatício, 
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
