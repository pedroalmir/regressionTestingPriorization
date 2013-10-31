package br.com.infowaypi.ecare.financeiro.conta;

import java.util.Date;

import br.com.infowaypi.ecare.segurados.DependenteSR;

/**
 * 
 * @author Diogo Vinícius
 *
 */
public class DetalheContaDependente extends DetalheContaSegurado {
	
	private static final long serialVersionUID = 1L;
	private DependenteSR dependente;

	private DetalheContaDependente(){}
	
	public DetalheContaDependente(Date competencia, DependenteSR dependente){
		super(competencia);
		this.dependente = dependente;
		dependente.getDetalhesContaDependente().add(this);
	}

	public DependenteSR getDependente() {
		return dependente;
	}
	public void setDependente(DependenteSR dependente) {
		this.dependente = dependente;
	}
		
}
