package br.com.infowaypi.ecarebc.painelDeControle;

import java.io.Serializable;

/**
 * Armazenar endereços de e-mail dos usuários para envio de informações sobre o acesso ao
 * Portal do Beneficiário e alertas sobre a próximidade do vencimento de contratos dos 
 * prestadores. 
 * 
 * @author jefferson, Marcos Roberto - 29.06.2012
 */
public class EmailPainel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idEmail;
	private PainelDeControle painel;
	private String email;

	public PainelDeControle getPainel() {
		return painel;
	}

	public String getEmail() {
		return email;
	}

	public Long getIdEmail() {
		return idEmail;
	}

	public void setIdEmail(Long idEmail) {
		this.idEmail = idEmail;
	}

	public void setPainel(PainelDeControle painel) {
		this.painel = painel;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}