package br.com.infowaypi.ecare.scheduller.sms;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecarebc.atendimentos.GuiaSimples;
import br.com.infowaypi.ecarebc.procedimentos.ProcedimentoInterface;


/**
 * Classe que representa as mensagens de alerta para o regulador.
 * São mensagens avisando das guias pendentes de regulação de urgência.
 * 
 * @author DANNYLVAN
 *
 */
public class MensagemAvisoRegulador  implements Serializable {
    	
	private static final long serialVersionUID = 1L;
	private Long idMensagemAvisoRegulador;
	private String conteudo;
	private String destino;
	private Date dataEnvio;
	private GuiaSimples<ProcedimentoInterface> guia;
	
	public GuiaSimples<ProcedimentoInterface> getGuia() {
	    return guia;
	}

	public void setGuia(GuiaSimples<ProcedimentoInterface> guia) {
	    this.guia = guia;
	}

	public Long getIdMensagemAvisoRegulador() {
		return idMensagemAvisoRegulador;
	}

	public void setIdMensagemAvisoRegulador(Long idMensagemAvisoRegulador) {
		this.idMensagemAvisoRegulador = idMensagemAvisoRegulador;
	}

	public static long getSerialversionuid() {
	    return serialVersionUID;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}
	
}