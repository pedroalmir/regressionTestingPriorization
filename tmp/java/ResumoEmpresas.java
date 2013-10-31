/**
 * 
 */
package br.com.infowaypi.ecare.financeiro.consignacao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.infowaypi.ecare.segurados.Empresa;

/**
 * @author Marcus bOolean
 *
 */
public class ResumoEmpresas {
	
	private Date competencia;
	private Date dataPagamento;
	private List<Empresa> empresas;
	
	public ResumoEmpresas() {
		this.empresas = new ArrayList<Empresa>();
	}

	public Date getCompetencia() {
		return competencia;
	}

	public void setCompetencia(Date competencia) {
		this.competencia = competencia;
	}

	public List<Empresa> getEmpresas() {
		return empresas;
	}

	public void setEmpresas(List<Empresa> empresas) {
		this.empresas = empresas;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}
	
	
	
	

}
