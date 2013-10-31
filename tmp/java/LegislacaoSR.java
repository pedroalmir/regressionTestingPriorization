package br.com.infowaypi.ecare.legislacaosr;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecare.enums.TipoArquivoEnum;
import br.com.infowaypi.ecare.validacao.ValidatorTipoArquivoPDF;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

/**
 * Legislação do Saúde Recife
 * 
 * @author Jefferson
 * 
 */
public class LegislacaoSR extends ArquivoBase implements Serializable {

	private Date datapublicacao;
	private String situacao;
	private UsuarioInterface usuario;

	public Date getDatapublicacao() {
		return datapublicacao;
	}

	public void setDatapublicacao(Date datapublicacao) {
		this.datapublicacao = datapublicacao;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}

	public Boolean validate(UsuarioInterface usuario) throws ValidateException {
		setZipado(false);
		setTipoArquivo(TipoArquivoEnum.PDF.getDescricao());
		if (getArquivo() != null) {
			ValidatorTipoArquivoPDF.isArquivoPDF(getArquivo());
		}
		if (getDataCriacao() == null) {
			setDataCriacao(new Date());
		}
		setUsuario(usuario);
		return true;
	}

}
