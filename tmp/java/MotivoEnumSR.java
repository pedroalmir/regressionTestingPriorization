/**
 * 
 */
package br.com.infowaypi.ecare.enums;

/**
 * @author Idelvane
 *
 */
public enum MotivoEnumSR {

	DEPENDENTE_COM_IDADE_SUPERIOR_A_PERMITIDA("Dependente com idade superior a permitida"),
	GERACAO_AUTOMATICA_BOLETO_NA_BAIXA_MANUAL("Gerado automaticamente durante a baixa manual"),
	PAGAMENTO_ATRAVES_BAIXA_MANUAL("Pago através da baixa manual"),
	REGULARIZACAO_DEPENDENTE("Regularizaçao de Dependente Estudante"),
	SOLICITADO_PELA_CENTRAL("Internação Eletiva SOLICITADA pela Central."),
	GUIA_ENVIADA("Guia enviada pelo prestador"), 
	GUIA_RECEBIDA("Guia recebida."),
	GUIA_FISICA_ENTREGUE("Guia física recebida pelo Saúde Recife."),
	GUIA_DEVOLVIDA("Guia devolvida"), 
	LOTE_RECEBIDO("Lote Recebido"), 
	REGISTRO_DE_ALTA("Registro De Alta"), 
	SUSPENSO_POR_INADIMPLENCIA("Beneficiário suspenso por inadimplência"), 
	SOLICITACAO_DIRETORIA_SAUDE("Solicitação da Diretoria de Saúde");
	
	private String descricao;
	
	MotivoEnumSR(String descricao) {
		this.descricao = descricao;
	}

	public String getMessage(String... param) {
		String mensagem = this.descricao();
		for(int i = 0; i < param.length; i++){
			if(mensagem.contains("{" + i + "}"))
				mensagem = mensagem.replace("{" + i + "}", (String)param[i]);
		}
		return mensagem;
	}

	private String descricao(){
		return descricao;
	}
}
