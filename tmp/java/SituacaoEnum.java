package br.com.infowaypi.ecarebc.enums;

/**
 * @author Josino
 */
public enum SituacaoEnum{
 
	PROBLEMAS_NO_CADASTRO("Cadastro com problemas"),
	CADASTRADO("Cadastrado"),
	RECADASTRADO("Recadastrado"),
	ATIVO("Ativo(a)"),
	INATIVO("Inativo(a)"),
	INICIADO("Iniciado(a)"),
	SUSPENSO("Suspenso(a)"),
	AGENDADA("Agendado(a)"),
	CANCELADO("Cancelado(a)"),
	CONFIRMADO("Confirmado(a)"),
	GLOSADO("Glosado(a)"),
	PAGO("Pago(a)"),	
	REALIZADO("Realizado(a)"),
	INTERNADO("Internado(a)"),
	GERADO("Gerado(a)"),
	SOLICITADO("Solicitado(a)"),
	SOLICITADO_INTERNACAO("Solicitado(a) Internação"),
	SOLICITADO_PRORROGACAO("Solicitado(a) Prorrogacao"),
	FATURADA("Faturado(a)"),
	FATURADA_PASSIVO_HONORARIO("Faturado(a) Passivo Honorarios"),
	AUTORIZADO("Autorizado(a)"),
	AUDITADO("Auditado(a)"),
	PRE_AUTORIZADO("Pré-autorizado"),
	PRORROGADO("Prorrogado(a)"),
	ABERTO("Aberto(a)"),
	FECHADO("Fechado(a)"),
	FECHADO_ESPECIAL("Fechado(a) Especial"),
	INAPTO("Inapto(a)"),
	BLOQUEADO("Bloqueado(a)"),
	ORDENADO("Ordenado(a)"),
	NEGADO("Negado(a)"),
	NAO_PRORROGADO("Não Prorrogado(a)"),
	NAO_AUTORIZADO("Não Autorizado(a)"),
	CREDENCIADO("Credenciado(a)"),
	DESCREDENCIADO("Descredenciado(a)"),
	ENVIADO("Enviado(a)"),
	RECEBIDO("Recebido(a)"),
	DEVOLVIDO("Devolvido(a)"),
	REMOVIDO("Removido(a)"),
	ALTA_REGISTRADA("Alta Registrada"),
	PROCEDIMENTO_COM_HONORARIO_GERADO("Honorário Gerado"),
	DEFERIDO("Deferido(a)"),
	INDEFERIDO("Indeferido(a)"),
	RECURSADO("Recursado(a)"),
	INSERIDO("Inserido(a)"),
	ENCERRADO("Encerrado(a)"),
	AGUARDANDO_COBRANCA("Aguardando cobrança"),
	PARCIALMENTE_AUTORIZADO("Parcialmente Autorizado(a)"),
	PENDENTE("Pendente");
	
	private String descricao;
	
	SituacaoEnum(String descricao){
		this.descricao = descricao;
	}
	
	public String descricao(){
		return descricao;
	}
	
}