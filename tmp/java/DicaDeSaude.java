package br.com.infowaypi.ecare.dicadesaude;

import java.io.Serializable;
import java.util.Date;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecare.enums.TipoArquivoEnum;
import br.com.infowaypi.ecare.validacao.ValidatorTipoArquivoPDF;
import br.com.infowaypi.msr.exceptions.ValidateException;
import br.com.infowaypi.msr.user.UsuarioInterface;

public class DicaDeSaude extends ArquivoBase implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String situacao;
	private UsuarioInterface usuario;
	
	public DicaDeSaude() {
		this.setTipoArquivo(TipoArquivoEnum.PDF.getDescricao());
		this.setZipado(false);
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}
	
	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}
	
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String descricao) {
		this.situacao = descricao;
	}
	
	public Boolean validate(UsuarioInterface usuario) throws ValidateException {

		if(this.getArquivo()!=null){
			ValidatorTipoArquivoPDF.isArquivoPDF(this.getArquivo());
		}
		if (this.getDataCriacao()==null){
			this.setDataCriacao(new Date());
		}
		setUsuario(usuario);	
		return true;
	}
	
}
