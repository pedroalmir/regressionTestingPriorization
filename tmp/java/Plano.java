package br.com.infowaypi.ecarebc.planos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.planos.Carencia.TipoCarencia;
import br.com.infowaypi.ecarebc.segurados.AbstractSegurado;
import br.com.infowaypi.ecarebc.segurados.GrupoBC;
import br.com.infowaypi.ecarebc.segurados.TitularInterfaceBC;


public class Plano implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long idPlano;
	private String codigoLegado;
	private String descricao;
	private boolean consultaExame;
	private boolean internacao;
	private Set<AcordoGrupo> acordos;
	private Set<TitularInterfaceBC> titulares;
	
	public Plano() {
		this.acordos = new HashSet<AcordoGrupo>();
	}

	public String getCodigoLegado() {
		return codigoLegado;
	}
	
	public void setCodigoLegado(String codigoLegado) {
		this.codigoLegado = codigoLegado;
	}
	
	public boolean isConsultaExame() {
		return consultaExame;
	}

	public void setConsultaExame(boolean consultaExame) {
		this.consultaExame = consultaExame;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getIdPlano() {
		return idPlano;
	}

	public void setIdPlano(Long idPlano) {
		this.idPlano = idPlano;
	}

	public boolean isInternacao() {
		return internacao;
	}

	public void setInternacao(boolean internacao) {
		this.internacao = internacao;
	}

	public Set<TitularInterfaceBC> getTitulares() {
		return titulares;
	}

	public void setTitulares(Set<TitularInterfaceBC> titulares) {
		this.titulares = titulares;
	}

	public Set<AcordoGrupo> getAcordos() {
		return acordos;
	}

	public void setAcordos(Set<AcordoGrupo> acordos) {
		this.acordos = acordos;
	}
	
	public BigDecimal getValor(AbstractSegurado segurado, Date date) {
		return segurado.getTitular().getGrupo().getValor(segurado, date);
	}
	
	public int getCarencia(TipoCarencia tipo, GrupoBC grupoBC){
		return grupoBC.getCarencia(tipo);
	}

}
