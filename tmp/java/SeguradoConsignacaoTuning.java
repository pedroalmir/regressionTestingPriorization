package br.com.infowaypi.ecare.segurados.tuning;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecare.atendimentos.GuiaSimplesTuning;
import br.com.infowaypi.ecare.segurados.SeguradoConsignacaoInterface;
import br.com.infowaypi.msr.pessoa.PessoaFisica;
import br.com.infowaypi.msr.pessoa.PessoaFisicaInterface;
import br.com.infowaypi.msr.situations.ImplColecaoSituacoesComponent;

public class SeguradoConsignacaoTuning extends ImplColecaoSituacoesComponent implements SeguradoConsignacaoInterface{
	
	private Long idSegurado;
	private Set<GuiaSimplesTuning> guias;
	protected PessoaFisicaInterface pessoaFisica;
	protected Date dataAdesao;
	protected String numeroDoCartao;
	
	public SeguradoConsignacaoTuning(){
		guias = new HashSet<GuiaSimplesTuning>();
		pessoaFisica = new PessoaFisica();
	}

	public Long getIdSegurado() {
		return idSegurado;
	}
	public void setIdSegurado(Long idSegurado) {
		this.idSegurado = idSegurado;
	}
	public Set<GuiaSimplesTuning> getGuias() {
		return guias;
	}
	public void setGuias(Set<GuiaSimplesTuning> guias) {
		this.guias = guias;
	}
	public PessoaFisicaInterface getPessoaFisica() {
		return pessoaFisica;
	}
	public void setPessoaFisica(PessoaFisicaInterface pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}
	public Date getDataAdesao() {
		return dataAdesao;
	}
	public void setDataAdesao(Date dataAdesao) {
		this.dataAdesao = dataAdesao;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.idSegurado)
			.hashCode();
	}
	
	public String getNumeroDoCartao() {
		return numeroDoCartao;
	}

	public void setNumeroDoCartao(String numeroDoCartao) {
		this.numeroDoCartao = numeroDoCartao;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof SeguradoConsignacaoInterface)) {
			return false;
		}
		SeguradoConsignacaoInterface otherObject = (SeguradoConsignacaoInterface) object;
		return new EqualsBuilder()
			.append(this.getIdSegurado(), otherObject.getIdSegurado())
			.isEquals();
	}
}
