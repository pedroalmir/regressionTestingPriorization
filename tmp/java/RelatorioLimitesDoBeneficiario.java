/**
 * 
 */
package br.com.infowaypi.ecare.services;

import java.util.Date;

import br.com.infowaypi.ecare.arquivos.ArquivoBase;
import br.com.infowaypi.ecare.arquivos.ArquivoLimitesBeneficiarioPrinter;
import br.com.infowaypi.ecare.enums.TipoArquivoEnum;
import br.com.infowaypi.ecare.segurados.ResumoSegurados;
import br.com.infowaypi.ecare.segurados.Segurado;
import br.com.infowaypi.ecare.services.enuns.TipoLimiteEnum;
import br.com.infowaypi.msr.exceptions.ValidateException;

/**
 * @author Marcus bOolean
 * @modify Anderson
 */
public class RelatorioLimitesDoBeneficiario {
	
	/**
	 * Metodo busca Segurados pelo cpf e numeroDoCartao 
	 * e retorna tal segurado
	 * @param cpfDoTitular
	 * @param numeroDoCartao
	 * */
	public ResumoSegurados buscarSegurados(String cpfDoTitular, String numeroDoCartao) throws ValidateException {
		
		return BuscarSegurados.buscar(cpfDoTitular, numeroDoCartao, Segurado.class);
	}
	
	/**
	 * Método busca Segurado pelo numero do cartão.
	 * Alteração feita para permitir o Portal do Beneficiário reaproveite o Relatório de limites do Beneficiário 
	 * @param numeroDoCartao
	 * @return
	 * @throws ValidateException
	 */
	public ResumoSegurados buscarSegurados(Segurado segurado) throws ValidateException {
		return BuscarSegurados.buscar("", segurado.getNumeroDoCartao(), Segurado.class);
	}
	/**
	 * Metodo que cria e retorna um resumo 
	 * para determinado segurado com a data atual 
	 * @param segurado
	 * */
	public ResumoLimitesBeneficiario selecionarSegurado(Segurado segurado, TipoLimiteEnum tipoLimite) throws Exception {
		ResumoLimitesBeneficiario resumo = new ResumoLimitesBeneficiario(segurado, new Date(), tipoLimite);
		ArquivoBase arquivoPdf = new ArquivoBase();
		arquivoPdf.setArquivo(new ArquivoLimitesBeneficiarioPrinter().createPDF(resumo,tipoLimite));
		arquivoPdf.setDataCriacao(new Date());
		arquivoPdf.setZipado(false);
		arquivoPdf.setTipoArquivo(TipoArquivoEnum.PDF.getDescricao());
		arquivoPdf.setTituloArquivo("Limite Beneficiário");
		resumo.setArquivoPdf(arquivoPdf);
		return resumo;
	}
	
	/**
	 * Metodo que retorna um resumo representando o resultado
	 * @param resumo
	 * */
	public void resultados() {}


}
