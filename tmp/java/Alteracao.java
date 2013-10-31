package br.com.infowaypi.ecarebc.utils;

import java.util.Date;

import br.com.infowaypi.msr.user.UsuarioInterface;

public class Alteracao {

	private Long idAlteracao;
	private Date data;
	private UsuarioInterface usuario;
	private String motivo;
	private Integer ordem;

	public Alteracao() {
	}

	public Alteracao(UsuarioInterface usuario, Date date, String motivo, Integer ordem) {
		this.usuario = usuario;
		this.data = date;
		this.motivo = motivo;
		this.ordem = ordem;
	}

	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public Long getIdAlteracao() {
		return idAlteracao;
	}

	public void setIdAlteracao(Long idAlteracao) {
		this.idAlteracao = idAlteracao;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public int compareTo(Alteracao o) {
		if (o.getData().compareTo(this.getData()) == 0) {
			if (this.getOrdem() > o.getOrdem()) {
				return -1;
			} else {
				return 1;
			}
		} else {
			return o.getData().compareTo(this.getData());
		}
	}

}
