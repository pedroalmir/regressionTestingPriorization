package br.com.infowaypi.ecarebc.portalTiss.report;

import java.util.Date;
import java.util.List;

import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.portalTiss.ArquivoTiss;
import br.com.infowaypi.msr.utils.Utils;

/**
 * Resumo do Report @ReportBaixarArquivosTiss
 * @author Emanuel
 *
 */
/**
 * @author Emanuel
 *
 */
public class ResumoBaixarArquivoTiss {

	private Date dataInicial; 
	private Date dataFinal;
	private List<ArquivoTiss> arquivos;
	private Prestador prestador;
	
	public ResumoBaixarArquivoTiss(Date dataInicial, Date dataFinal, List<ArquivoTiss> arquivos, Prestador prestador) {
		this.arquivos = arquivos;
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
		this.prestador = prestador;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public List<ArquivoTiss> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<ArquivoTiss> arquivos) {
		this.arquivos = arquivos;
	}

	public Prestador getPrestador() {
		return prestador;
	}

	public void setPrestador(Prestador prestador) {
		this.prestador = prestador;
	}
	
	public String getPeriodo(){
		return "De " + Utils.format(dataInicial) + " a " + Utils.format(dataFinal);
	}
}
