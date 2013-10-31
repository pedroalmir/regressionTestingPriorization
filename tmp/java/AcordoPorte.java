package br.com.infowaypi.ecarebc.atendimentos.acordos;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import br.com.infowaypi.ecarebc.procedimentos.Porte;
import br.com.infowaypi.msr.exceptions.ValidateException;


public class AcordoPorte extends AbstractAcordo{
	
	private static final long serialVersionUID = 1L;

	private Porte porte;

	private float valorProfissional;
	private float valorAnestesista;
	private float valorAuxiliar1;
	private float valorAuxiliar2;
	private float valorAuxiliar3;
	private float valorAuxiliar4;
	private float valorTaxaSala;
	
	public AcordoPorte(){
		super();
	}
	
	public Porte getPorte() {
		return porte;
	}
	
	public void setPorte(Porte porte) {
		this.porte = porte;
	}

	public float getValorAnestesista() {
		return valorAnestesista;
	}

	public void setValorAnestesista(float valorAnestesista) {
		this.valorAnestesista = valorAnestesista;
	}

	public float getValorAuxiliar1() {
		return valorAuxiliar1;
	}

	public void setValorAuxiliar1(float valorAuxiliar1) {
		this.valorAuxiliar1 = valorAuxiliar1;
	}

	public float getValorAuxiliar2() {
		return valorAuxiliar2;
	}

	public void setValorAuxiliar2(float valorAuxiliar2) {
		this.valorAuxiliar2 = valorAuxiliar2;
	}

	public float getValorProfissional() {
		return valorProfissional;
	}

	public void setValorProfissional(float valorProfissional) {
		this.valorProfissional = valorProfissional;
	}

	public float getValorTaxaSala() {
		return valorTaxaSala;
	}

	public void setValorTaxaSala(float valorTaxaSala) {
		this.valorTaxaSala = valorTaxaSala;
	}

	public float getValorAuxiliar3() {
		return valorAuxiliar3;
	}

	public void setValorAuxiliar3(float valorAuxiliar3) {
		this.valorAuxiliar3 = valorAuxiliar3;
	}

	public float getValorAuxiliar4() {
		return valorAuxiliar4;
	}

	public void setValorAuxiliar4(float valorAuxiliar4) {
		this.valorAuxiliar4 = valorAuxiliar4;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AcordoPorte))
			return false;
		AcordoPorte acordoPorte = (AcordoPorte)obj;
		return new EqualsBuilder()
			.append(this.getPrestador(), acordoPorte.getPrestador())
			.append(this.getPorte(), acordoPorte.getPorte())
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getPrestador())
			.append(this.getPorte())
			.toHashCode();
	}

	@Override
	public Boolean validate() throws ValidateException {
		return Boolean.TRUE;
	}
	
}
