package br.com.infowaypi.ecarebc.financeiro.faturamento;

import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.msr.user.UsuarioInterface;

public class OcorrenciaFaturamento {
	
	private Long idOcorrenciaFaturamento;
	private String descricao;
	private Date dataOcorrencia;
	private AbstractFaturamento faturamento;
	private UsuarioInterface usuario;

	public OcorrenciaFaturamento() {
		this.dataOcorrencia = new Date();
	}
	
	public OcorrenciaFaturamento(AbstractFaturamento faturamento, String descricao, UsuarioInterface usuario) {
		this();
		this.faturamento = faturamento;
		this.descricao = descricao;
		this.usuario = usuario;
	}

	public Long getIdOcorrenciaFaturamento() {
		return idOcorrenciaFaturamento;
	}

	public void setIdOcorrenciaFaturamento(Long idOcorrenciaFaturamento) {
		this.idOcorrenciaFaturamento = idOcorrenciaFaturamento;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDataOcorrencia() {
		return dataOcorrencia;
	}

	public void setDataOcorrencia(Date dataOcorrencia) {
		this.dataOcorrencia = dataOcorrencia;
	}

	public AbstractFaturamento getFaturamento() {
		return faturamento;
	}

	public void setFaturamento(AbstractFaturamento faturamento) {
		this.faturamento = faturamento;
	}
	
	public UsuarioInterface getUsuario() {
		return usuario;
	}

	public void setUsuario(UsuarioInterface usuario) {
		this.usuario = usuario;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof OcorrenciaFaturamento)){
			return false;
		}
		
		OcorrenciaFaturamento ocorrencia = (OcorrenciaFaturamento)obj;
		
		EqualsBuilder builder = new EqualsBuilder();
		builder.append(this.descricao, ocorrencia.getDescricao())
				.append(this.getDataOcorrencia(), ocorrencia.getDataOcorrencia())
				.append(this.usuario, ocorrencia.getUsuario());
		
		return builder.isEquals();
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		
		builder.append(this.getDescricao())
				.append(this.getDataOcorrencia())
				.append(this.getUsuario());
		
		return builder.toHashCode();
	}
}
