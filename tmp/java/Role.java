package br.com.infowaypi.ecarebc.enums;

/**
 * Obs: ao inserir um novo role, colocar em ordem alfabetica.
 * @changes Emanuel
 *
 */

public enum Role { 
	ALO_SAUDE("aloSaude","Al� Sa�de"),
	ATENDENTE("atendente", "Atendente"),
	AUDITOR("auditor", "Auditor"),
	AUDITOR_ODONTO("auditorOdonto", "Auditor Odontol�gico"),
	CADASTRO  ("cadastro", "X"),
	CENTRAL_DE_SERVICOS("centralDeServico", "Central de Servi�os"),
	COBRANCA("cobranca","Cobran�a"),
	CONVIDADO("convidado","Convidado"),
	/**
	 * Representa um segurado que n�o possui rela��o financeira - Dependente.
	 */
	DEPENDENTE("dependente","Dependente"),	
	DIGITADOR("digitador", "Digitador"),
	DIRETORIA_MEDICA ("diretoriaMedica", "Diretor"),
	FATURISTA("faturista","Faturista"),
	FINANCEIRO("financeiro","Financeiro"),
	GESTOR("gestor", "Gestor"),
	GERENCIA_DE_ADESAO("gerenciaAdesao", "Ger�ncia de Ades�o"),
	GERENCIA_REDE_CREDENCIADA("gerenciaRedeCredenciada", "Ger�ncia de Rede Credenciada"),
	INTERIOR ("interior", "X"),
	MARCACAO  ("marcacao", "X"), 
	REGULADOR("regulador", "M�dico Regulador"),
	OPERADOR("operador","Operador"),
	PRESTADOR("prestador","X"),
	PRESTADOR_ANESTESISTA("prestadorAnestesista", "X"),
	PRESTADOR_COMPLETO ("prestadorCompleto", "Prestador Completo"),
	PRESTADOR_CONS_EXM_INT_URG("prestadorConsExmIntUrg", "Prestador Consultas/Exames/Interna��o/Urg�ncia"),
	PRESTADOR_CONSULTA("prestadorConsulta", "Prestador Consultas"),
	PRESTADOR_CONS_INT_URG("prestadorConsIntUrg","Prestador Consultas/Interna��o/Urg�ncia"),
	PRESTADOR_CONS_EXM("prestadorConsultaExame", "Prestador Consultas/Exames"),
	PRESTADOR_EXAME("prestadorExame","Prestador Exames"),
	PRESTADOR_INT_EXM_URG("prestadorInternacaoExameUrgencia", "Prestador Exames/Interna��o/Urg�ncia"),
	PRESTADOR_INT_URG("prestadorInternacaoUrgencia", "Prestador Interna��o/Urg�ncia"),
	PRESTADOR_ODONTOLOGICO("prestadorOdonto", "Prestador Odontol�gico"),
	ROOT      ("root", "Root"), 
	RELACIONAMENTO("relacionamento","Relacionamento"),
	SUPERVISOR ("supervisor", "X"), 
	SUPERVISOR_ODONTOLOGICO ("supervisorOdonto", "X"),
	/**
	 * Representa um titular financeiro - Titular, Dependente Suplementar e Pensionista.
	 */
	TITULAR("titular","Titular");

	private final String valor;
	private final String descricao;
	
	Role(String valor, String descricao){
		this.valor = valor;
		this.descricao = descricao;
	}
	

    public String getValor()   { return this.valor; }
    
    public String descricao()   { return this.descricao; }
    
    public static Role getRoleByValor(String valor){
    	for (Role r : Role.values()) {
			if(r.getValor().equals(valor)){
				return r;
			}
		}
    	
    	return null;
    }
}