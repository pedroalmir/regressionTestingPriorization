package br.com.infowaypi.ecare.sistema;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe que representa o contador de acessos ao sistema.
 * @author Emanuel
 *
 */
@SuppressWarnings("serial")
public class ContadorAcesso implements Serializable{

	private Long idContador;
	private int quantidade;
	private Date dataEnvio;
	private boolean enviado;

	public ContadorAcesso() {
		this.enviado = false;
	}

	public ContadorAcesso(int quantidade) {
		this();
		this.quantidade = quantidade;
	}
	
	public Long getIdContador() {
		return idContador;
	}
	public void setIdContador(Long idContador) {
		this.idContador = idContador;
	}
	
	public int getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	
	public Date getDataEnvio() {
		return dataEnvio;
	}
	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}
	
	public boolean isEnviado() {
		return enviado;
	}
	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}
	
	public void incrementaContador(){
		this.quantidade = quantidade + 1;
	}
}
