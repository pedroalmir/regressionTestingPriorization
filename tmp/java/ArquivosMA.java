package br.com.infowaypi.ecare.geracaoCSV;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.FlushMode;

import br.com.infowaypi.ecare.segurados.DependenteInterface;
import br.com.infowaypi.ecare.segurados.DependenteSR;
import br.com.infowaypi.ecare.segurados.Empresa;
import br.com.infowaypi.ecare.segurados.Matricula;
import br.com.infowaypi.ecarebc.segurados.GrupoBC;
import br.com.infowaypi.molecular.HibernateUtil;

public class ArquivosMA {
	
	private byte[] titularesEncontrados;
	private byte[] dependentesEncontrados;
	private byte[] empresasEncontrados;
	
	@SuppressWarnings("unchecked")
	public ArquivosMA gerarCSVMedAliance() throws Exception{
		
		HibernateUtil.currentSession().setFlushMode(FlushMode.COMMIT);
		
		GeracaoCSVSegurados arquivos = new GeracaoCSVSegurados();
		
		List<DependenteInterface> dependentes = HibernateUtil.currentSession().createCriteria(DependenteSR.class)
		.setFetchSize(500)
		.setCacheable(false)
		.setFetchMode("segurado.consultasPromocionais", FetchMode.SELECT)
		.setFetchMode("segurado.matriculas", FetchMode.SELECT)
		.setFetchMode("segurado.cartoes", FetchMode.SELECT)
		.setFetchMode("situacaoCadastral", FetchMode.SELECT)
		.setFetchMode("guias", FetchMode.SELECT)
		.setFetchMode("consumoIndividual", FetchMode.SELECT)
		.setFetchMode("odontograma", FetchMode.SELECT)
		.list();
		
		dependentesEncontrados = arquivos.gerarCSVDependentes(dependentes);
//		HibernateUtil.currentSession().clear();
		
		
		List<Matricula> matriculas = HibernateUtil.currentSession().createCriteria(Matricula.class)
		.setFetchSize(500)
		.setCacheable(false)
		.setFetchMode("dependentes", FetchMode.SELECT)
		.setFetchMode("consignacoes", FetchMode.SELECT)
		.setFetchMode("contasMatriculas", FetchMode.SELECT)
		.setFetchMode("segurado.consultasPromocionais", FetchMode.SELECT)
		.setFetchMode("segurado.matriculas", FetchMode.SELECT)
		.setFetchMode("segurado.cartoes", FetchMode.SELECT)
		.setFetchMode("colecaoSituacoes", FetchMode.SELECT)
		.setFetchMode("situacaoCadastral", FetchMode.SELECT)
		.setFetchMode("guias", FetchMode.SELECT)
		.setFetchMode("consumoIndividual", FetchMode.SELECT)
		.setFetchMode("odontograma", FetchMode.SELECT)
		.list();
		
		titularesEncontrados = arquivos.gerarCSVTitulares(matriculas);
//		HibernateUtil.currentSession().clear();
		
		List<GrupoBC> empresas = HibernateUtil.currentSession().createCriteria(Empresa.class)
		.setFetchSize(500)
		.setCacheable(false)
		.setFetchMode("subGrupos", FetchMode.SELECT)
		.setFetchMode("acordos", FetchMode.SELECT)
		.setFetchMode("titulares", FetchMode.SELECT)
		.setFetchMode("matriculas", FetchMode.SELECT)
		.list();
		
		empresasEncontrados = arquivos.gerarCSVGrupos(empresas);
//		HibernateUtil.currentSession().clear();
		
		return this;
	}

	public byte[] getDependentesEncontrados() {
		return dependentesEncontrados;
	}

	public void setDependentesEncontrados(byte[] dependentesEncontrados) {
		this.dependentesEncontrados = dependentesEncontrados;
	}

	public byte[] getEmpresasEncontrados() {
		return empresasEncontrados;
	}

	public void setEmpresasEncontrados(byte[] empresasEncontrados) {
		this.empresasEncontrados = empresasEncontrados;
	}

	public byte[] getTitularesEncontrados() {
		return titularesEncontrados;
	}

	public void setTitularesEncontrados(byte[] titularesEncontrados) {
		this.titularesEncontrados = titularesEncontrados;
	}
	
	public String getFileDependentes(){
		return "Dependentes.csv";
	}
	
	public String getFileTitulares(){
		return "Titulares.csv";
	}
	
	public String getFileEmpresas(){
		return "Empresas.csv";
	}
}
