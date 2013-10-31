package br.com.infowaypi.ecare.segurados;

import java.util.HashSet;
import java.util.Set;

import br.com.infowaypi.ecarebc.segurados.GrupoBC;
/**
 * 
 * @author Idelvane /Danilo Nogueira Portela 
 *
 */
public class Empresa extends GrupoBC{
	
	public Set<Matricula> matriculas;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Empresa() {
		super();
		this.matriculas = new HashSet<Matricula>();
	}
	
	public Set<Matricula> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Set<Matricula> matriculas) {
		this.matriculas = matriculas;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
