package br.com.infowaypi.ecare.arquivos;

import java.util.Date;

/**
 * Classe criada para representar um arquivo PDF.
 * 
 * @author Emanuel
 */
public class ArquivoDownloadPdf {

	private static final long serialVersionUID = 1L;
	
	private Long idArquivo;
	private Date dataUpload;
	private String descricaoArquivo;
	private byte[] arquivo;

	public Long getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Long idArquivo) {
		this.idArquivo = idArquivo;
	}

	public Date getDataUpload() {
		return dataUpload;
	}

	public void setDataUpload(Date dataUpload) {
		this.dataUpload = dataUpload;
	}

	public String getDescricaoArquivo() {
		return descricaoArquivo;
	}

	public void setDescricaoArquivo(String descricaoArquivo) {
		this.descricaoArquivo = descricaoArquivo;
	}

	public byte[] getArquivo() {
		return arquivo;
	}

	public void setArquivo(byte[] arquivo) {
		this.arquivo = arquivo;
	}

}
