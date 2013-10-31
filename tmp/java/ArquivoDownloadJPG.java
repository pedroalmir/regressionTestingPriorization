package br.com.infowaypi.ecare.arquivos;

import java.util.Date;

import br.com.infowaypi.msr.utils.Utils;

/**
 * Classe criada para representar um arquivo JPG.
 * 
 * @author DANNYLVAN
 */
public class ArquivoDownloadJPG {

	private static final long serialVersionUID = 1L;
	
	private Long idArquivoDownloadJPG;
	private Date dataUpload;
	private String descricaoArquivo;
	private byte[] arquivo;

	public Long getIdArquivoDownloadJPG() {
		return idArquivoDownloadJPG;
	}

	public void setIdArquivoDownloadJPG(Long idArquivoDownloadJPG) {
		this.idArquivoDownloadJPG = idArquivoDownloadJPG;
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

	public void setDescricaoQuestionario(String complemento){
		this.setDescricaoArquivo("questionario_"+Utils.format(new Date(), "ddMMyyyyHHmm") + "_" + complemento + ".jpg");
	}
}
