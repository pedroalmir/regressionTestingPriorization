/**
 * 
 */
package br.com.infowaypi.ecare.financeiro.consignacao;


/**
 * 
 * @author Marcus bOolean
 *
 */
public class ResumoBaseSalarial {
	
	public int numeroTitulares;
	public int numeroMatriculas;
	public int numeroMatriculasInseridas;
	public byte[] conteudoErrado;
	
	
	public ResumoBaseSalarial() {
	
	}

	public int getNumeroTitulares() {
		return numeroTitulares;
	}

	public void setNumeroTitulares(int numeroTitulares) {
		this.numeroTitulares = numeroTitulares;
	}

	public int getNumeroMatriculas() {
		return numeroMatriculas;
	}

	public void setNumeroMatriculas(int numeroMatriculas) {
		this.numeroMatriculas = numeroMatriculas;
	}

	public int getNumeroMatriculasInseridas() {
		return numeroMatriculasInseridas;
	}

	public void setNumeroMatriculasInseridas(int numeroMatriculasInseridas) {
		this.numeroMatriculasInseridas = numeroMatriculasInseridas;
	}

	public byte[] getConteudoErrado() {
		return conteudoErrado;
	}

	public void setConteudoErrado(byte[] conteudoErrado) {
		this.conteudoErrado = conteudoErrado;
	}
	
	public String getFileName() {
		return "RegistrosErrados.txt";
	}

}
