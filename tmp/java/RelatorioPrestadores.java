package br.com.infowaypi.ecare.services;

import java.util.Date;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecare.arquivos.ArquivoRedeCredenciadaPrinter;
import br.com.infowaypi.ecare.enums.TipoArquivoEnum;
import br.com.infowaypi.ecarebc.associados.Especialidade;
import br.com.infowaypi.ecarebc.associados.Prestador;
import br.com.infowaypi.ecarebc.associados.Profissional;
import br.com.infowaypi.ecarebc.associados.ResumoPrestadores;
import br.com.infowaypi.ecarebc.service.RelatorioPrestadoresService;
import br.com.infowaypi.msr.address.Municipio;

/**
 * @author Marcus bOolean
 * @chages Emanuel
 */
public class RelatorioPrestadores extends RelatorioPrestadoresService{

	/**
	 * Utilizado no relatorio resources/flows/relatorios/RelatorioPrestadores.jhm.xml
	 * @param prestador
	 * @param especialidade
	 * @param profissional
	 * @param bairro
	 * @param municipio
	 * @param tipoResultado
	 * @return
	 * @throws Exception
	 */
	public ResumoPrestadores buscarPrestadores(String prestador, Especialidade especialidade, Profissional profissional, String bairro, Municipio municipio, Integer tipoResultado) throws Exception {
		return super.buscarPrestadores(prestador, especialidade, profissional, null, bairro, municipio, tipoResultado, false, false,false);
	}
	
	/**
	 * Utilizado no relatorio resources/report/portalBeneficiario/RelatorioRedeCredenciada.jhm.xml
	 * @param prestador
	 * @param especialidade
	 * @param profissional
	 * @param municipio
	 * @param bairro
	 * @param eletivo
	 * @param urgencia
	 * @param odontologico
	 * @return
	 * @throws Exception
	 */
	public ResumoPrestadores buscarPrestadores(Prestador prestador, Especialidade especialidade, Profissional profissional, Municipio municipio, String bairro, boolean eletivo, boolean urgencia, boolean odontologico) throws Exception {
		String fantasia;
		if (prestador == null) {
			fantasia = null;
		} else {
			fantasia = prestador.getPessoaJuridica().getFantasia();
		}
		
		ResumoPrestadores resumo = super.buscarPrestadores(fantasia, especialidade, profissional, null, bairro, municipio, ResumoPrestadores.TIPO_RESULTADO_PRESTADORES, eletivo, urgencia, odontologico);
		
		ArquivoBase arquivoPdf = new ArquivoBase();
		arquivoPdf.setArquivo(new ArquivoRedeCredenciadaPrinter().createPDF(resumo.getPrestadoresEncontrados()));
		arquivoPdf.setDataCriacao(new Date());
		arquivoPdf.setZipado(false);
		arquivoPdf.setTipoArquivo(TipoArquivoEnum.PDF.getDescricao());
		arquivoPdf.setTituloArquivo("Rede Credenciada");
		
		resumo.setArquivoPdf(arquivoPdf);
		return resumo;
	}
}
