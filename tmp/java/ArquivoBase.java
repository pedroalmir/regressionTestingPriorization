package br.com.infowaypi.ecare.arquivos;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe base para os arquivos que serão disponibilizados para download, está classe tem o 
 * propósito de oferecer os atributos básicos para um arquivo a ser baixado.  
 * 
 * @author wislanildo
 *
 */
public class ArquivoBase implements Serializable{

	private static final long serialVersionUID = -7129947007115968427L;
	
	private Long idArquivoBase;
	private byte[] arquivo;
	private Date dataCriacao;
	private Boolean zipado;
	private String tipoArquivo;
	private String tituloArquivo;
	
	
	public Long getIdArquivoBase() {
		return idArquivoBase;
	}
	public void setIdArquivoBase(Long idArquivoBase) {
		this.idArquivoBase = idArquivoBase;
	}
	public byte[] getArquivo() {
		return arquivo;
	}
	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}
	public Date getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public Boolean getZipado() {
		return zipado;
	}
	public void setZipado(Boolean zipado) {
		this.zipado = zipado;
	}
	public String getTipoArquivo() {
		return tipoArquivo;
	}
	public void setTipoArquivo(String tipoArquivo) {
		this.tipoArquivo = tipoArquivo;
	}
	public String getTituloArquivo() {
		return tituloArquivo;
	}
	public void setTituloArquivo(String tituloArquivo) {
		this.tituloArquivo = tituloArquivo;
	}
}
