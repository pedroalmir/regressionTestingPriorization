package br.com.infowaypi.ecare.services.cadastros;

import br.com.infowaypi.ecare.segurados.Dependente;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecare.segurados.Segurado;

public class ResumoMigracaoDependentes {

	private Dependente dependente; 
	private Matricula matricula;
	private Integer tipoBeneficiarioSelecionado;
	
	public Integer getTipoBeneficiarioSelecionado() {
		return tipoBeneficiarioSelecionado;
	}
	
	public void setTipoBeneficiarioSelecionado(Integer tipoBeneficiarioSelecionado) {
		this.tipoBeneficiarioSelecionado = tipoBeneficiarioSelecionado;
	}
	
	public Matricula getMatricula() {
		return matricula;
	}
	public void setMatricula(Matricula matricula) {
		this.matricula = matricula;
	} 
	
	public Dependente getDependente() {
		return dependente;
	}
	
	public void setDependente(Dependente segurado) {
		this.dependente = segurado;
	}
	
	
}
